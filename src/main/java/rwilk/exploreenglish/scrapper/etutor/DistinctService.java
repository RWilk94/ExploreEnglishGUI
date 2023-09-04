package rwilk.exploreenglish.scrapper.etutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;
import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord;
import rwilk.exploreenglish.model.entity.etutor.EtutorWord;
import rwilk.exploreenglish.repository.etutor.EtutorDefinitionRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonWordRepository;
import rwilk.exploreenglish.repository.etutor.EtutorWordRepository;

@Slf4j
@Service
public class DistinctService implements CommandLineRunner {

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
  }

  private void fixEmptyPolishNameInWords() {
    final List<EtutorWord> words = wordRepository.findAllByPolishNameLike("");

    words.forEach(word -> word.setPolishName(
                    word.getDefinitions().stream()
                      .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
                      .findFirst()
                      .orElseThrow(() -> new IllegalStateException("sa"))
                      .getEnglishName()
                  )
    );
    wordRepository.saveAll(words);
  }

  private void generateEtutorLessonWords() {
    final Map<String, Map<String, List<EtutorWord>>> wordsGroupedByName =
      wordRepository.findAll()
        .stream()
        .collect(Collectors.groupingBy(
          EtutorWord::getPolishName,
          Collectors.groupingBy(word -> word.getDefinitions()
            .stream()
            .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
            .map(EtutorDefinition::getEnglishName)
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

        if (CollectionUtils.isNotEmpty(firstWord.getEtutorLessonWords())) {
          continue;
        }
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
                  .filter(item -> StringUtils.equals(item.getEnglishName(), definition.getEnglishName()))
                  .filter(item -> StringUtils.equals(item.getAdditionalInformation(), definition.getAdditionalInformation()))
                  .filter(item -> StringUtils.equals(item.getBritishSound(), definition.getBritishSound()))
                  .filter(item -> StringUtils.equals(item.getAmericanSound(), definition.getAmericanSound()))
                  .count() == 0) {
              definition.setWord(firstWord);
              firstWord.getDefinitions().add(definition);
            }
          }
          wordsToRemove.add(word);
        }

        wordRepository.save(firstWord);
        wordRepository.deleteAll(wordsToRemove);
      }
    }
  }

}
