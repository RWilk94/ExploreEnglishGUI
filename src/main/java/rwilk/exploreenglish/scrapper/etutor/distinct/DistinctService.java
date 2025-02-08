package rwilk.exploreenglish.scrapper.etutor.distinct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Definition;
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;
import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord;
import rwilk.exploreenglish.model.entity.etutor.EtutorWord;
import rwilk.exploreenglish.repository.etutor.EtutorDefinitionRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonWordRepository;
import rwilk.exploreenglish.repository.etutor.EtutorWordRepository;
import rwilk.exploreenglish.scrapper.etutor.content.WordScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Slf4j
@Service
public class DistinctService implements CommandLineRunner {

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private final EtutorLessonWordRepository lessonWordRepository;
  private final EtutorWordRepository wordRepository;
  private final EtutorDefinitionRepository etutorDefinitionRepository;

  public DistinctService(final EtutorLessonWordRepository lessonWordRepository,
                         final EtutorWordRepository wordRepository,
                         final EtutorDefinitionRepository etutorDefinitionRepository) {
    this.lessonWordRepository = lessonWordRepository;
    this.wordRepository = wordRepository;
    this.etutorDefinitionRepository = etutorDefinitionRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    // generateEtutorLessonWords();
    // fixEmptyForeignTranslationInDefinitions();
  }

  // use fixEmptyPolishNameInWordsByHtml
  @Deprecated
  public void fixEmptyPolishNameInWords() {
    final List<EtutorWord> words = wordRepository.findAllByNativeTranslationLikeOrNativeTranslationIsNull("");

    words.forEach(word -> word.setNativeTranslation(
                    word.getDefinitions().stream()
                      .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
                      .findFirst()
                      .orElseThrow(() -> new IllegalStateException("sa"))
                      .getForeignTranslation()
                  )
    );
    wordRepository.saveAll(words);
  }

  public void fixEmptyPolishNameInWordsByHtml() {
    final List<EtutorWord> words = wordRepository.findAllByNativeTranslationLikeOrNativeTranslationIsNull("");

    for (final EtutorWord word : words) {

      final int indexOfEqualSign = word.getHtml().indexOf(" = ");
      if (indexOfEqualSign != -1) {

        String html = word.getHtml().substring(indexOfEqualSign + 3);
        int indexOfHtmlTag = html.indexOf("<");

        html = html.substring(0, indexOfHtmlTag);

        word.setNativeTranslation(html.trim());
        wordRepository.save(word);
      }
    }
  }

  public void fixEmptyForeignTranslationInDefinitions() {
    final List<EtutorDefinition> definitions = etutorDefinitionRepository.findAllByForeignTranslation("")
            .stream()
            .filter(definition -> StringUtils.isNotEmpty(definition.getPrimarySound()) || StringUtils.isNotEmpty(definition.getSecondarySound()))
            .toList();

    IntStream.range(0, definitions.size())
            .forEach(index -> {
//              final EtutorDefinition definition = definitions.get(43);
              final EtutorDefinition definition = definitions.get(index);
              final EtutorWord word = definition.getWord();
              final String html = word.getHtml();

              if (WordTypeEnum.valueOf(definition.getType()) == WordTypeEnum.WORD) {

                String sound = definition.getPrimarySound() != null ? definition.getPrimarySound() : definition.getSecondarySound();
                sound = sound.substring(sound.indexOf("/images-common"));
                String substring = html.substring(0, html.indexOf(sound));
                substring = substring.substring(substring.lastIndexOf("phraseEntityLine"));
                substring = substring.substring(substring.indexOf(">") + 1, substring.indexOf("<"));

                String languageVariety = html.substring(html.indexOf(sound));

                if (languageVariety.contains("#language-variety-list")
                        && (!languageVariety.contains("phraseEntityLine")
                        || languageVariety.indexOf("phraseEntityLine") > languageVariety.indexOf("#language-variety-list"))) {
                  languageVariety = languageVariety.substring(languageVariety.indexOf("#language-variety-list"));
                  languageVariety = languageVariety.substring(languageVariety.indexOf(">") + 1, languageVariety.indexOf("<"));
                  languageVariety = WordScrapper.formatLanguageVariety(languageVariety);
                } else {
                  languageVariety = null;
                }

                System.out.println("UPDATE etutor_definition SET foreign_translation = '" + replaceApostrophe(substring) + "', additional_information = '" + languageVariety + "' WHERE id = " + definition.getId() + ";");

              } else if (WordTypeEnum.valueOf(definition.getType()) == WordTypeEnum.SENTENCE) {

                String sound = definition.getPrimarySound() != null ? definition.getPrimarySound() : definition.getSecondarySound();
                sound = sound.substring(sound.indexOf("/images-common"));
                String substring = html.substring(html.indexOf(sound));

                String sentence = substring.substring(substring.indexOf("sentenceTranslation"));

                substring = substring.substring(0, substring.indexOf("<span class=\"repetitionAddOrRemoveLinkWrapper\">"));
                substring = substring.substring(substring.lastIndexOf("</span>") + 7);

                sentence = sentence.substring(sentence.indexOf(">") + 1, sentence.indexOf("<"));

                System.out.println("UPDATE etutor_definition SET foreign_translation = '" + replaceApostrophe(substring) + "', additional_information = '" + sentence + "' WHERE id = " + definition.getId() + ";");
                // System.out.println("[" + (index + 1) + "] " +  substring + " (" + sentence + ")");

              } else {
                throw new IllegalStateException("Unexpected value: " + WordTypeEnum.valueOf(definition.getType()));
              }

            });
  }

  protected String replaceApostrophe(final String text) {
    return StringUtils.trimToEmpty(StringUtils.defaultString(text)).replace("'", "''");
  }

  @Transactional
  public void generateEtutorLessonWords() {
    final Map<String, Map<String, List<EtutorWord>>> wordsGroupedByName =
      wordRepository.findAll()
        .stream()
              .filter(word -> StringUtils.isNoneBlank(word.getNativeTranslation()))
        .collect(Collectors.groupingBy(
          EtutorWord::getNativeTranslation,
          Collectors.groupingBy(word -> word.getDefinitions()
            .stream()
            .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
            .map(EtutorDefinition::getForeignTranslation)
            .findFirst()
            .orElse("")
          )
        ));

    log.info("[DistinctService] FOUND [{}] group", wordsGroupedByName.size());

    final List<Map<String, List<EtutorWord>>> duplicates = wordsGroupedByName.values()
      .stream()
      .filter(item -> item.values()
                        .stream()
                        .findFirst()
                        .orElse(List.of())
                        .size() >= 1) // TODO >= 1
      .toList();

    log.info("[DistinctService] FOUND [{}] duplicates", duplicates.size());

    final AtomicInteger index = new AtomicInteger(0);

    for (Map<String, List<EtutorWord>> duplicate : duplicates) {

      for (Map.Entry<String, List<EtutorWord>> entry : duplicate.entrySet()) {

        final String key = entry.getKey();
        log.info("[REMOVE_DUPLICATES] FIXING [{}] at index [{}]", key, index.getAndIncrement());
        final List<EtutorWord> values = entry.getValue();

        final List<EtutorWord> wordsToRemove = new ArrayList<>();
        final EtutorWord firstWord = values.get(0);

//        if (CollectionUtils.isNotEmpty(firstWord.getEtutorLessonWords())) {
//          continue;
//        }
        values.remove(0);

        final EtutorLessonWord etutorLessonWord = EtutorLessonWord
          .builder()
          .word(firstWord)
          .exercise(firstWord.getExercise())
          .build();
        firstWord.getEtutorLessonWords().add(etutorLessonWord);

        for (EtutorWord word : values) {
          final EtutorLessonWord etutorLessonWord2 = EtutorLessonWord
            .builder()
            .word(firstWord)
            .exercise(word.getExercise())
            .build();
          firstWord.getEtutorLessonWords().add(etutorLessonWord2);

          for (EtutorDefinition definition : word.getDefinitions()) {
            if (firstWord.getDefinitions()
                  .stream()
                  .filter(item -> item.getType().equals(definition.getType()))
                  .filter(item -> StringUtils.equals(item.getForeignTranslation(), definition.getForeignTranslation()))
                  .filter(item -> StringUtils.equals(item.getAdditionalInformation(), definition.getAdditionalInformation()))
                  .filter(item -> StringUtils.equals(item.getPrimarySound(), definition.getPrimarySound()))
                  .filter(item -> StringUtils.equals(item.getSecondarySound(), definition.getSecondarySound()))
                  .count() == 0) {
              definition.setWord(firstWord);
              firstWord.getDefinitions().add(definition);
            }
          }
          wordsToRemove.add(word);
        }

        final List<EtutorLessonWord> distinctEtutorLessonWord = firstWord.getEtutorLessonWords()
                .stream()
                .filter(distinctByKey(it -> it.getExercise().getId()))
                .toList();

        firstWord.setEtutorLessonWords(new ArrayList<>(distinctEtutorLessonWord));

        wordRepository.save(firstWord);

        for (EtutorWord word : wordsToRemove) {
            wordRepository.delete(wordRepository.getById(word.getId()));
        }
      }
    }
  }

}
