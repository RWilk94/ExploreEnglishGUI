package rwilk.exploreenglish.scrapper.langeek.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.langeek.LangeekDefinition;
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise;
import rwilk.exploreenglish.model.entity.langeek.LangeekExerciseWord;
import rwilk.exploreenglish.model.entity.langeek.LangeekWord;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseRepository;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseWordRepository;
import rwilk.exploreenglish.repository.langeek.LangeekWordRepository;
import rwilk.exploreenglish.scrapper.langeek.schema.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("DataFlowIssue")
public class LangeekWordScrapper {
    private static final String BASE_URL = "https://langeek.co";

    private final LangeekDictionaryScrapper langeekDictionaryScrapper;
    private final LangeekWordRepository langeekWordRepository;
    private final LangeekExerciseWordRepository langeekExerciseWordRepository;
    private final LangeekExerciseRepository langeekExerciseRepository;

    @Transactional
    public void webScrap(final LangeekExercise langeekExercise) {
        try {
            log.info("Scraping langeek words for exercise: {} {}", langeekExercise.getName(), langeekExercise.getHref());
            final URL url = new URL(langeekExercise.getHref());
            final Document document = Jsoup.parse(url, 20000);

            final Elements mainElements = mainElements(document);

            for (final Element wordElement : mainElements) {
                log.info("Scraping word: {}", wordElement.child(1).text());

                final LangeekWord langeekWord = createLangeekWord(wordElement);
                final List<LangeekDefinition> langeekDefinitions = createDefinitions(langeekWord, wordElement);

                langeekWord.setDefinitions(langeekDefinitions);
                save(langeekExercise, langeekWord, findDuplicates(langeekWord));
                log.info("Finish scraping word: {}", wordElement.child(1).text());
            }

            langeekExercise.setIsReady(true);
            langeekExerciseRepository.save(langeekExercise);

            log.info("Saved langeek words for exercise: {}", langeekExercise.getName());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Elements mainElements(final Document document) {
        return document.selectFirst("main").child(1).child(0).children();
    }

    private String extractImage(final Element wordElement) {
        String image = wordElement.child(0).select("a").attr("href");
        if (!image.startsWith("https://")) {
            image = BASE_URL + image;
        }
        return image.endsWith("no-pic-700w.png")
                ? null
                : image;
    }

    private String extractForeignTranslation(final Element wordElement) {
        return wordElement.child(1).text();
    }

    private String extractDescription(final Element wordElement) {
        return wordElement.child(2).selectFirst("p").text();
    }

    private String extractNativeTranslation(final Element wordElement) {
        try {
            return wordElement.child(2).select("p").get(1).text();
        } catch (IndexOutOfBoundsException e) {
            return wordElement.child(2).select("p").get(0).text();
        }
    }

    private String extractPartOfSpeech(final Element wordElement) {
        String partOfSpeech = wordElement.child(3).text();
        return partOfSpeech.substring(1, partOfSpeech.length() - 1);
    }

    private String extractDictionaryHref(final Element wordElement) {
        return wordElement.child(4).select("a").get(1).attr("href");
    }

    private Word extractDictionaryWord(final LangeekResponse dictionaryEntry, final String foreignTranslation, final String description) {
        final List<Word> words = dictionaryEntry
                .getPageProps()
                .getInitialState()
                .getStaticData()
                .getWordEntry()
                .getWords();

        List<Word> dictionaryWords = words.stream()
                .filter(dictionaryWord -> dictionaryWord.getTranslations()
                        .stream()
                        .anyMatch(translation -> translation.getTranslation().replaceAll("  ", " ").equals(description))
                )
                .toList();

        final List<Translation> dictionaryTranslations = new ArrayList<>();
        words.forEach(dictionaryWord -> dictionaryWord.getTranslations()
                .forEach(translation -> {
                    dictionaryTranslations.add(translation);
                    if (translation.getSubTranslations() != null) {
                        dictionaryTranslations.addAll(translation.getSubTranslations());
                    }
                }));

        List<Translation> translations = dictionaryTranslations.stream()
                .filter(translation -> translation.getTranslation().replaceAll("  ", " ").equals(description))
                .toList();

        if (translations.size() == 1) {
            dictionaryWords = words.stream()
                    .filter(dictionaryWord -> dictionaryWord.getTranslations()
                            .stream()
                            .anyMatch(translation -> translation.getId() == translations.get(0).getId() ||
                                    (translation.getSubTranslations() != null && translation.getSubTranslations()
                                            .stream()
                                            .anyMatch(subTranslation -> subTranslation.getId() == translations.get(0).getId()))
                            )
                    )
                    .toList();
        }

        if (words.size() == 1) {
            return words.get(0);
        }

        if (dictionaryWords.size() > 1) {
            dictionaryWords = dictionaryWords.stream()
                    .filter(dictionaryWord -> dictionaryWord.getWord().equals(foreignTranslation))
                    .toList();
        }



//        List<Word> dictionaryWords = words
//                .stream()
//                .filter(dictionaryWord -> replaceBrackets(dictionaryWord.getWord()).equals(replaceBrackets(foreignTranslation))
//                        || (foreignTranslation.startsWith("to ") && replaceBrackets(dictionaryWord.getWord()).equals(replaceBrackets(foreignTranslation).substring(3)))
//                        || (foreignTranslation.startsWith("the ") && replaceBrackets(dictionaryWord.getWord()).equals(replaceBrackets(foreignTranslation).substring(4)))
//                        || (foreignTranslation.startsWith("The ") && replaceBrackets(dictionaryWord.getWord()).equals(replaceBrackets(foreignTranslation).substring(4)))
//                )
//                .toList();
//
//        if (dictionaryWords.isEmpty()) {
//            dictionaryWords =
//                    words.stream()
//                            .filter(dictionaryWord -> replaceBrackets(dictionaryWord.getWord()).equalsIgnoreCase(replaceBrackets(foreignTranslation))
//                                    || (foreignTranslation.startsWith("to ") && replaceBrackets(dictionaryWord.getWord()).equalsIgnoreCase(replaceBrackets(foreignTranslation).substring(3)))
//                                    || (foreignTranslation.startsWith("the ") && replaceBrackets(dictionaryWord.getWord()).equalsIgnoreCase(replaceBrackets(foreignTranslation).substring(4)))
//                                    || (foreignTranslation.startsWith("The ") && replaceBrackets(dictionaryWord.getWord()).equalsIgnoreCase(replaceBrackets(foreignTranslation).substring(4)))
//                            ).toList();
//        }
//        if (dictionaryWords.isEmpty()) {
//            dictionaryWords = words.stream()
//                    .filter(dictionaryWord -> dictionaryWord.getTranslations()
//                            .stream()
//                            .anyMatch(translation -> translation.getTranslation().replaceAll("  ", " ").equals(description))
//                    )
//                    .toList();
//        }
//
//        if (dictionaryWords.size() > 1) {
//            dictionaryWords = dictionaryWords.stream()
//                    .filter(dictionaryWord -> dictionaryWord.getTranslations()
//                            .stream()
//                            .anyMatch(translation -> translation.getTranslation().replaceAll("  ", " ").equals(description))
//                    )
//                    .toList();
//        }
        if (dictionaryWords.size() != 1) {
            throw new RuntimeException("Word not found");
        }

        return dictionaryWords.get(0);
    }

//    private String replaceBrackets(final String translation) {
//        return translation
//                .replace("[", "")
//                .replace("]", "")
//                .replace("(", "")
//                .replace(")", "")
//                .replace("|", "");
//    }

    private Translation extractDictionaryWordTranslation(final Word dictionaryWord, final String description, final String nativeTranslation) {
        final List<Translation> dictionaryTranslations = new ArrayList<>();
        dictionaryWord.getTranslations()
                .forEach(translation -> {
                    dictionaryTranslations.add(translation);
                    if (translation.getSubTranslations() != null) {
                        dictionaryTranslations.addAll(translation.getSubTranslations());
                    }
                });

        List<Translation> translations = dictionaryTranslations.stream()
                .filter(translation -> translation.getTranslation().replaceAll("  ", " ").equals(description))
                .toList();
        if (translations.size() > 1) {
            translations = translations.stream()
                    .filter(translation -> translation.getWordPhoto() != null && translation.getWordPhoto().getPhoto() != null)
                    .toList();
        }

        if (translations.size() == 1) {
            return translations.get(0);
        }

        translations = dictionaryTranslations.stream()
                .filter(translation -> concatLocalizedTranslations(translation).equals(nativeTranslation))
                .toList();

        if (translations.isEmpty()) {
            translations = dictionaryTranslations.stream()
                    .filter(translation -> StringUtils.isNotEmpty(translation.getLevel()))
                    .toList();
        }

        if (translations.size() == 1) {
            return translations.get(0);
        }

        throw new RuntimeException("Translation not found");
    }

    private String concatLocalizedTranslations(final Translation translation) {
        if (translation.getLocalizedProperties() != null) {
            return StringUtils.defaultString(translation.getLocalizedProperties().getTranslation()).trim()
                    + ", "
                    + StringUtils.defaultString(translation.getLocalizedProperties().getOtherTranslations()).trim();
        }
        return "";
    }

    private List<Example> extractDictionaryWordTranslationExamples(final LangeekResponse dictionaryEntry, final int translationId) {
        return dictionaryEntry.getPageProps()
                .getInitialState()
                .getStaticData()
                .getSimpleExamples()
                .get(String.valueOf(translationId));
    }

    private String checkTranslations(final Translation selectedTranslation, final String nativeTranslation) {
        if (selectedTranslation.getLocalizedProperties() != null) {
            String localizedPropertyTranslation = selectedTranslation.getLocalizedProperties().getTranslation().trim();
            if (StringUtils.isNotEmpty(selectedTranslation.getLocalizedProperties().getOtherTranslations())) {
                localizedPropertyTranslation += ", " + selectedTranslation.getLocalizedProperties().getOtherTranslations().trim();
            }

            if (!localizedPropertyTranslation.equals(nativeTranslation)) {
                log.warn("Translations are not equal {} != {}", localizedPropertyTranslation, nativeTranslation);
                return localizedPropertyTranslation;
            }
        }
        return nativeTranslation;
    }

    private String extractLevel(final Translation translation) {
        return translation.getLevel();
    }

    private LangeekWord createLangeekWord(final Element wordElement) {
        return LangeekWord.builder()
                .nativeTranslation(extractNativeTranslation(wordElement))
                .additionalInformation(null)
                .partOfSpeech(extractPartOfSpeech(wordElement))
                .article(null)
                .grammarType(null)
                .image(extractImage(wordElement))
                .href(extractDictionaryHref(wordElement))
                .level(null)
                .build();
    }

    private LangeekDefinition createPrimaryDefinition(final LangeekWord langeekWord, final Element wordElement,
                                                      final Translation selectedTranslation) {
        return LangeekDefinition.builder()
                .type(WordTypeEnum.WORD.toString())
                .foreignTranslation(extractForeignTranslation(wordElement))
                .additionalInformation(extractDescription(wordElement))
                .primarySound(selectedTranslation.getTitleVoice())
                .language("en-PL")
                .word(langeekWord)
                .build();
    }

    private void createOtherFormsDefinitions(final LangeekWord langeekWord, final Word selectedDictionaryWord,
                                             final List<LangeekDefinition> langeekDefinitions) {
        langeekDefinitions.addAll(
                ListUtils.emptyIfNull(selectedDictionaryWord.getOtherForms())
                        .stream()
                        .map(otherForm -> LangeekDefinition.builder()
                                .type(WordTypeEnum.WORD.toString())
                                .foreignTranslation(otherForm.getWordForm())
                                .additionalInformation(otherForm.getLocalSource())
                                .primarySound(null)
                                .language("en-PL")
                                .word(langeekWord)
                                .build()
                        )
                        .toList()
        );
    }

    private Long extractLangeekWordId(final Element wordElement) {
        final String href = extractDictionaryHref(wordElement);
        return Long.parseLong(href.substring(href.lastIndexOf("/") + 1));
    }

    private List<LangeekDefinition> createDefinitions(final LangeekWord langeekWord, final Element wordElement) {
        final LangeekResponse dictionaryEntry = langeekDictionaryScrapper.webScrap(extractLangeekWordId(wordElement), "en-PL");
        final Word selectedDictionaryWord = extractDictionaryWord(dictionaryEntry, extractForeignTranslation(wordElement), extractDescription(wordElement));
        final Translation selectedTranslation = extractDictionaryWordTranslation(selectedDictionaryWord, extractDescription(wordElement), extractNativeTranslation(wordElement));
        langeekWord.setLevel(extractLevel(selectedTranslation));

        final String translations = checkTranslations(selectedTranslation, extractNativeTranslation(wordElement));
        langeekWord.setNativeTranslation(translations);

        final List<LangeekDefinition> langeekDefinitions = new ArrayList<>();
        langeekDefinitions.add(createPrimaryDefinition(langeekWord, wordElement, selectedTranslation));
        createOtherFormsDefinitions(langeekWord, selectedDictionaryWord, langeekDefinitions);
        if (selectedTranslation.getMetadata() != null && selectedTranslation.getMetadata().getNlpAnalyzedData() != null) {
            createLangeekPastFormDefinition(selectedTranslation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekPastParticipleDefinition(selectedTranslation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekPresentParticipleDefinition(selectedTranslation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekComparativeDefinition(selectedTranslation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekSuperlativeDefinition(selectedTranslation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekPluralDefinition(selectedTranslation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
        }
        createLangeekSynonymsDefinition(selectedTranslation.getSynonyms(), langeekWord, langeekDefinitions);
        createLangeekAntonymsDefinition(selectedTranslation.getAntonyms(), langeekWord, langeekDefinitions);
        createLangeekSentenceDefinition(extractDictionaryWordTranslationExamples(dictionaryEntry, selectedTranslation.getId()), langeekWord, langeekDefinitions);

        return langeekDefinitions;
    }

    private List<LangeekWord> findDuplicates(final LangeekWord langeekWord) {
        final List<LangeekWord> duplicatesByNativeTranslation = langeekWordRepository.findAllByHref(langeekWord.getHref())
                .stream()
                .filter(duplicate -> duplicate.getNativeTranslation().equals(langeekWord.getNativeTranslation())
                        && duplicate.getPartOfSpeech().equals(langeekWord.getPartOfSpeech())
                )
                .filter(duplicate -> duplicate.getDefinitions().size() == langeekWord.getDefinitions().size())
                .toList();

        final List<LangeekDefinition> wordDefinitions = langeekWord.getDefinitions()
                .stream()
                .filter(definition -> definition.getType().equals(WordTypeEnum.WORD.toString()))
                .toList();

        final List<LangeekWord> finalDuplicates = duplicatesByNativeTranslation.stream()
                .filter(duplicateWord -> duplicateWord.getDefinitions()
                        .stream()
                        .filter(definition -> definition.getType().equals(WordTypeEnum.WORD.toString())).count() == wordDefinitions.size())
                .filter(duplicateWord -> wordDefinitions.stream()
                        .allMatch(wordDefinition -> duplicateWord.getDefinitions()
                                .stream()
                                .anyMatch(duplicateDefinition -> duplicateDefinition.getForeignTranslation().equals(wordDefinition.getForeignTranslation())
                                        && StringUtils.defaultString(duplicateDefinition.getAdditionalInformation()).equals(wordDefinition.getAdditionalInformation())
                                )
                        )
                ).toList();

        if (finalDuplicates.size() > 1) {
            throw new RuntimeException("More than one duplicate");
        }

        return finalDuplicates;
    }

    private void save(final LangeekExercise langeekExercise, final LangeekWord langeekWord, final List<LangeekWord> duplicates) {
        if (CollectionUtils.isNotEmpty(duplicates)) {
            saveDuplicated(langeekExercise, langeekWord, duplicates);
        } else {
            saveUnique(langeekExercise, langeekWord);
        }
    }

    private void saveDuplicated(final LangeekExercise langeekExercise, final LangeekWord langeekWord, final List<LangeekWord> duplicates) {
        log.info("Word already exists: {}", langeekWord.getHref());

        final LangeekWord langeekWordExisting = duplicates.get(0);
        final LangeekExerciseWord langeekExerciseWord = LangeekExerciseWord.builder()
                .exercise(langeekExercise)
                .word(langeekWordExisting)
                .build();

        LangeekExerciseWord existedLangeekExerciseWord = langeekExerciseWordRepository.findByExercise_IdAndWord_Id(langeekExercise.getId(), langeekWordExisting.getId());
        if (existedLangeekExerciseWord == null) {
            langeekExerciseWordRepository.save(langeekExerciseWord);
        } else {
            log.warn("Exercise word already exists: {} {}", langeekExercise.getName(), langeekWordExisting.getNativeTranslation());
        }
    }

    private void saveUnique(final LangeekExercise langeekExercise, final LangeekWord langeekWord) {
        final LangeekExerciseWord langeekExerciseWord = LangeekExerciseWord.builder()
                .exercise(langeekExercise)
                .word(langeekWord)
                .build();

        langeekWord.setEtutorLessonWords(List.of(langeekExerciseWord));
        langeekWordRepository.save(langeekWord);
    }

    private void createLangeekPastFormDefinition(final NLPAnalyzedData nlpAnalyzedData,
                                                 final LangeekWord langeekWord,
                                                 final List<LangeekDefinition> langeekDefinitions) {
        Optional.ofNullable(nlpAnalyzedData.getPastForm())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(pastForm -> langeekDefinitions.add(
                        createLangeekDefinitionObject(WordTypeEnum.PAST_TENSE, pastForm, langeekWord)
                ));
    }

    private void createLangeekPastParticipleDefinition(final NLPAnalyzedData nlpAnalyzedData,
                                                       final LangeekWord langeekWord,
                                                       final List<LangeekDefinition> langeekDefinitions) {
        createLangeekDefinitionIfPresent(
                nlpAnalyzedData.getPresentParticipleForm(),
                WordTypeEnum.PAST_PARTICIPLE,
                langeekWord,
                langeekDefinitions
        );
    }

    private void createLangeekPresentParticipleDefinition(final NLPAnalyzedData nlpAnalyzedData,
                                                          final LangeekWord langeekWord,
                                                          final List<LangeekDefinition> langeekDefinitions) {
        createLangeekDefinitionIfPresent(
                nlpAnalyzedData.getPresentForm(),
                WordTypeEnum.PRESENT_PARTICIPLE,
                langeekWord,
                langeekDefinitions
        );
    }

    private void createLangeekComparativeDefinition(final NLPAnalyzedData nlpAnalyzedData,
                                                    final LangeekWord langeekWord,
                                                    final List<LangeekDefinition> langeekDefinitions) {
        createLangeekDefinitionIfPresent(
                nlpAnalyzedData.getComparativeForm(),
                WordTypeEnum.COMPARATIVE,
                langeekWord,
                langeekDefinitions
        );
    }

    private void createLangeekSuperlativeDefinition(final NLPAnalyzedData nlpAnalyzedData,
                                                    final LangeekWord langeekWord,
                                                    final List<LangeekDefinition> langeekDefinitions) {
        createLangeekDefinitionIfPresent(
                nlpAnalyzedData.getSuperlativeForm(),
                WordTypeEnum.SUPERLATIVE,
                langeekWord,
                langeekDefinitions
        );
    }

    private void createLangeekPluralDefinition(final NLPAnalyzedData nlpAnalyzedData,
                                               final LangeekWord langeekWord,
                                               final List<LangeekDefinition> langeekDefinitions) {
        createLangeekDefinitionIfPresent(
                nlpAnalyzedData.getPluralForm(),
                WordTypeEnum.PLURAL,
                langeekWord,
                langeekDefinitions
        );
    }

    private void createLangeekSynonymsDefinition(final List<Synonym> synonyms,
                                                 final LangeekWord langeekWord,
                                                 final List<LangeekDefinition> langeekDefinitions) {
        Optional.ofNullable(synonyms)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(synonymsList -> synonymsList.forEach(synonym ->
                        langeekDefinitions.add(
                                createLangeekDefinitionObject(WordTypeEnum.SYNONYM, synonym.getWord(), langeekWord)
                        )
                ));
    }

    private void createLangeekAntonymsDefinition(final List<Antonym> antonyms,
                                                 final LangeekWord langeekWord,
                                                 final List<LangeekDefinition> langeekDefinitions) {
        Optional.ofNullable(antonyms)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(antonymsList -> antonymsList.forEach(antonym ->
                        langeekDefinitions.add(
                                createLangeekDefinitionObject(WordTypeEnum.OPPOSITE, antonym.getWord(), langeekWord)
                        )
                ));
    }

    private void createLangeekSentenceDefinition(final List<Example> examples,
                                                 final LangeekWord langeekWord,
                                                 final List<LangeekDefinition> langeekDefinitions) {
        Optional.ofNullable(examples)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(examplesList -> examplesList.forEach(example ->
                        langeekDefinitions.add(
                                createLangeekDefinitionObject(WordTypeEnum.SENTENCE, StringUtils.join(example.getWords(), ""), langeekWord)
                        )
                ));
    }

    private void createLangeekDefinitionIfPresent(final String foreignTranslation,
                                                  final WordTypeEnum type,
                                                  final LangeekWord langeekWord,
                                                  final List<LangeekDefinition> langeekDefinitions) {
        Optional.ofNullable(foreignTranslation)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(item -> langeekDefinitions.add(
                        createLangeekDefinitionObject(type, item, langeekWord)
                ));
    }

    private LangeekDefinition createLangeekDefinitionObject(final WordTypeEnum wordTypeEnum, final String foreignTranslation,
                                                            final LangeekWord langeekWord) {
        return LangeekDefinition.builder()
                .type(wordTypeEnum.toString())
                .foreignTranslation(foreignTranslation)
                .additionalInformation(null)
                .primarySound(null)
                .language("en-PL")
                .word(langeekWord)
                .build();
    }
}
