package rwilk.exploreenglish.scrapper.langeek.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
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
import rwilk.exploreenglish.scrapper.langeek.exception.WordNotFoundException;
import rwilk.exploreenglish.scrapper.langeek.schema.exercise.Card;
import rwilk.exploreenglish.scrapper.langeek.schema.exercise.LangeekDictionaryExerciseResponse;
import rwilk.exploreenglish.scrapper.langeek.schema.exercise.MainTranslation;
import rwilk.exploreenglish.scrapper.langeek.schema.word.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekWordV2Scrapper implements CommandLineRunner {
    private final LangeekDictionaryScrapper langeekDictionaryScrapper;
    private final LangeekWordRepository langeekWordRepository;
    private final LangeekExerciseWordRepository langeekExerciseWordRepository;
    private final LangeekExerciseRepository langeekExerciseRepository;

    @Override
    public void run(String... args) throws Exception {
        // fixSynonyms();
    }

    @Transactional
    public void webScrap(final LangeekExercise langeekExercise) {
        log.info("Scraping exercise: {} {} {}", langeekExercise.getId(), langeekExercise.getName(), langeekExercise.getHref());
        final LangeekDictionaryExerciseResponse langeekDictionaryExerciseResponse = langeekDictionaryScrapper
                .webScrapExercise(langeekExercise.getHref());

        final List<MainTranslation> mainTranslations = getMainTranslations(langeekDictionaryExerciseResponse);

        for (final MainTranslation mainTranslation : mainTranslations) {
            log.info("Scraping word: {}", mainTranslation.getTitle());

            final int dictionaryId;
            final int wordId;
            final int translationId;

            try {
                if (mainTranslation.getWord() == null || mainTranslation.getWord().getWordEntry() == null) {
                    log.warn("Word is null: {}", mainTranslation.getTitle());
                    throw new WordNotFoundException(String.format("404 - Word %s not found", mainTranslation.getTitle()));
                }

                dictionaryId = mainTranslation.getWord().getWordEntry().getId();
                wordId = mainTranslation.getWord().getId();
                translationId = mainTranslation.getId();

                final LangeekDictionaryWordResponse langeekDictionaryWordResponse = langeekDictionaryScrapper
                        .webScrap((long) dictionaryId, "en-PL");


                final Word selectedWord = getSelectedWord(langeekDictionaryWordResponse, wordId);
                final Translation selectedTranslation = getSelectedTranslation(selectedWord, translationId);

                final LangeekWord langeekWord = createLangeekWord(dictionaryId, selectedTranslation);
                langeekWord.setLangeekWordId((long) wordId);
                langeekWord.setLangeekDictionaryId((long) dictionaryId);
                langeekWord.setLangeekWordTranslationId((long) translationId);
                final List<LangeekDefinition> langeekDefinitions = createDefinitions(langeekDictionaryWordResponse, langeekWord, selectedWord, selectedTranslation);
                langeekWord.setDefinitions(langeekDefinitions);

                save(langeekExercise, langeekWord, findDuplicates(langeekWord));
            } catch (WordNotFoundException e) {
                log.warn("Word dictionary not found: {}, trying to save via backup", e.getMessage());
                final LangeekWord langeekWord = LangeekWord.builder()
                        .nativeTranslation(getNativeTranslation(mainTranslation.getLocalizedProperties()))
                        .additionalInformation(null)
                        .partOfSpeech(mainTranslation.getPartOfSpeech().getPartOfSpeechType())
                        .article(null)
                        .grammarType(null)
                        .image(mainTranslation.getWordPhoto() != null ? mainTranslation.getWordPhoto().getPhoto() : null)
                        .level(null)
                        .definitions(new ArrayList<>())
                        .build();

                final LangeekDefinition definition = LangeekDefinition.builder()
                        .type(WordTypeEnum.WORD.toString())
                        .foreignTranslation(mainTranslation.getTitle())
                        .additionalInformation(mainTranslation.getTranslation())
                        .primarySound(null)
                        .secondarySound(null)
                        .language("en-PL")
                        .word(langeekWord)
                        .build();

                langeekWord.getDefinitions().add(definition);
                save(langeekExercise, langeekWord, findDuplicates(langeekWord));
            }

            log.info("Finish scraping word: {}", mainTranslation.getTitle());
        }
        langeekExercise.setIsReady(true);
        langeekExerciseRepository.save(langeekExercise);
        log.info("Finish scraping exercise: {} {} {}", langeekExercise.getId(), langeekExercise.getName(), langeekExercise.getHref());
    }

    private List<MainTranslation> getMainTranslations(final LangeekDictionaryExerciseResponse langeekDictionaryExerciseResponse) {
        return langeekDictionaryExerciseResponse.getPageProps()
                .getInitialState()
                .getStaticData()
                .getSubcategory()
                .getCards()
                .stream()
                .map(Card::getMainTranslation)
                .toList();
    }

    private Word getSelectedWord(final LangeekDictionaryWordResponse langeekDictionaryWordResponse, final int wordId) {
        if (langeekDictionaryWordResponse.getPageProps()
                .getInitialState()
                .getStaticData()
                .getWordEntry() == null) {
            throw new WordNotFoundException(String.format("404 - Word %s not found", wordId));
        }

        return langeekDictionaryWordResponse.getPageProps()
                .getInitialState()
                .getStaticData()
                .getWordEntry()
                .getWords()
                .stream()
                .filter(word -> word.getId() == wordId)
                .findFirst()
                .orElseThrow();
    }

    private Translation getSelectedTranslation(final Word word, final int translationId) {
        final List<Translation> translations = new ArrayList<>();
        word.getTranslations()
                .forEach(translation -> {
                    translations.addAll(ListUtils.emptyIfNull(translation.getSubTranslations()));
                    translations.add(translation);
                });

        return translations
                .stream()
                .filter(translation -> translation.getId() == translationId)
                .findFirst()
                .orElseThrow();
    }

    private LangeekWord createLangeekWord(final int dictionaryId, final Translation translation) {
        final String nativeTranslation = getNativeTranslation(translation);
        final String partOfSpeech = getPartOfSpeech(translation);
        final String level = translation.getLevel();
        final String image = getPhoto(translation);
        final String href = "https://dictionary.langeek.co/en-PL/word/" + dictionaryId;

        return LangeekWord.builder()
                .nativeTranslation(nativeTranslation)
                .additionalInformation(null)
                .partOfSpeech(partOfSpeech)
                .article(null)
                .grammarType(null)
                .image(image)
                .href(href)
                .level(level)
                .build();
    }

    private String getNativeTranslation(final Translation translation) {
        if (translation.getLocalizedProperties() == null) {
            return translation.getTranslation();
        }

        String nativeTranslation = translation.getLocalizedProperties().getTranslation();
        if (StringUtils.isNotEmpty(translation.getLocalizedProperties().getOtherTranslations())) {
            nativeTranslation += ", " + translation.getLocalizedProperties().getOtherTranslations();
        }
        if (StringUtils.isBlank(nativeTranslation)) {
            throw new IllegalArgumentException("Native translation is empty");
        }

        return nativeTranslation.trim();
    }

    private String getNativeTranslation(final LocalizedProperties localizedProperties) {
        if (localizedProperties == null) {
            return null;
        }

        String nativeTranslation = localizedProperties.getTranslation();
        if (StringUtils.isNotEmpty(localizedProperties.getOtherTranslations())) {
            nativeTranslation += ", " + localizedProperties.getOtherTranslations();
        }
        if (StringUtils.isBlank(nativeTranslation)) {
            throw new IllegalArgumentException("Native translation is empty");
        }

        return nativeTranslation.trim();
    }

    private String getPartOfSpeech(final Translation translation) {
        final String partOfSpeech = translation.getPartOfSpeech().getPartOfSpeechType();
        if (StringUtils.isEmpty(partOfSpeech)) {
            return translation.getType();
        }
        return partOfSpeech;
    }

    private String getPhoto(final Translation translation) {
        if (translation.getWordPhoto() != null) {
            return translation.getWordPhoto().getPhoto();
        }
        return null;
    }

    private List<LangeekDefinition> createDefinitions(final LangeekDictionaryWordResponse langeekDictionaryWordResponse,
                                                      final LangeekWord langeekWord,
                                                      final Word word,
                                                      final Translation translation) {
        final List<LangeekDefinition> langeekDefinitions = new ArrayList<>();

        langeekDefinitions.add(createPrimaryDefinition(langeekWord, translation));
        langeekDefinitions.addAll(createOtherFormsDefinitions(langeekWord, word));

        if (translation.getMetadata() != null && translation.getMetadata().getNlpAnalyzedData() != null) {
            createLangeekPastFormDefinition(translation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekPastParticipleDefinition(translation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekPresentParticipleDefinition(translation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekComparativeDefinition(translation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekSuperlativeDefinition(translation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
            createLangeekPluralDefinition(translation.getMetadata().getNlpAnalyzedData(), langeekWord, langeekDefinitions);
        }
        createLangeekSynonymsDefinition(translation, langeekWord, langeekDefinitions);
        createLangeekAntonymsDefinition(translation.getAntonyms(), langeekWord, langeekDefinitions);
        createLangeekSentenceDefinition(
                extractDictionaryWordTranslationExamples(langeekDictionaryWordResponse, translation.getId()),
                langeekWord,
                langeekDefinitions
        );

        return langeekDefinitions;
    }

    private LangeekDefinition createPrimaryDefinition(final LangeekWord langeekWord, final Translation translation) {
        return LangeekDefinition.builder()
                .type(WordTypeEnum.WORD.toString())
                .foreignTranslation(translation.getTitle())
                .additionalInformation(translation.getTranslation())
                .primarySound(translation.getTitleVoice())
                .secondarySound(null)
                .language("en-PL")
                .word(langeekWord)
                .build();
    }

    private List<LangeekDefinition> createOtherFormsDefinitions(final LangeekWord langeekWord, final Word word) {
        return ListUtils.emptyIfNull(word.getOtherForms())
                .stream()
                .map(otherForm -> LangeekDefinition.builder()
                        .type(WordTypeEnum.WORD.toString())
                        .foreignTranslation(otherForm.getWordForm().trim())
                        .additionalInformation(otherForm.getLocalSource())
                        .primarySound(null)
                        .secondarySound(null)
                        .language("en-PL")
                        .word(langeekWord)
                        .build()
                )
                .toList();
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


    private void createLangeekSynonymsDefinition(final Translation translation,
                                                 final LangeekWord langeekWord,
                                                 final List<LangeekDefinition> langeekDefinitions) {
        if (translation.getSynonymCluster() != null && CollectionUtils.isNotEmpty(translation.getSynonymCluster().getTranslations())) {
            final SynonymCluster synonymCluster = translation.getSynonymCluster();

            final List<SynonymTranslation> synonymTranslations = synonymCluster.getTranslations()
                    .stream()
                    .filter(clusterTranslation -> clusterTranslation.getTranslationId() != translation.getId())
                    .toList();

            synonymTranslations.forEach(
                    synonymTranslation -> langeekDefinitions.add(
                            createLangeekDefinitionObject(WordTypeEnum.SYNONYM, synonymTranslation.getWord(), langeekWord)
                    )
            );
        }
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
                .ifPresent(examplesList -> examplesList
                        .forEach(example ->
                                langeekDefinitions.add(
                                        createLangeekSentenceDefinitionObject(
                                                StringUtils.join(example.getWords(), ""),
                                                langeekWord,
                                                example.getMainWordIndexes()
                                        )
                                )
                        )
                );
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

    private LangeekDefinition createLangeekSentenceDefinitionObject(final String foreignTranslation,
                                                                    final LangeekWord langeekWord,
                                                                    final List<Integer> mainWordIndexes) {
        final String mainWordIndexesString = mainWordIndexes.isEmpty()
                ? null
                : StringUtils.join(mainWordIndexes, ",");

        return LangeekDefinition.builder()
                .type(WordTypeEnum.SENTENCE.toString())
                .foreignTranslation(foreignTranslation)
                .additionalInformation(null)
                .primarySound(null)
                .language("en-PL")
                .word(langeekWord)
                .mainWordIndexes(mainWordIndexesString)
                .build();
    }

    private List<Example> extractDictionaryWordTranslationExamples(final LangeekDictionaryWordResponse dictionaryEntry, final int translationId) {
        return dictionaryEntry.getPageProps()
                .getInitialState()
                .getStaticData()
                .getSimpleExamples()
                .get(String.valueOf(translationId));
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
        log.info("Duplicated word: {}", langeekWord.getDefinitions().get(0).getForeignTranslation());

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

        langeekWord.setExerciseWords(List.of(langeekExerciseWord));
        langeekWordRepository.save(langeekWord);
    }
}
