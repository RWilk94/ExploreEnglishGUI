package rwilk.exploreenglish.scrapper.englishintranslations;

import static rwilk.exploreenglish.exception.ExceptionControllerAdvice.NOT_FOUND_COURSE_INSTANCE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import rwilk.exploreenglish.exception.RequiredObjectNotFoundException;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Definition;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.DefinitionService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.WordService;

@RequiredArgsConstructor
@Service
public class EnglishInTranslationsScrapper implements CommandLineRunner {

  private static final String FILE_PATH = "angielski_w_tlumaczeniach.txt";
  private final CourseService courseService;
  private final LessonService lessonService;
  private final LessonWordService lessonWordService;
  private final WordService wordService;
  private final DefinitionService definitionService;

  @Override
  public void run(final String... args) throws Exception {
    // run();
  }

  private void run() throws Exception {
    final Path path = Paths.get(getClass().getClassLoader()
                                          .getResource(FILE_PATH).toURI());

    try (Stream<String> linesStream = Files.lines(path)) {

      final Course course = courseService.getById(13L)
                                         .orElseThrow(() -> new RequiredObjectNotFoundException(NOT_FOUND_COURSE_INSTANCE));
      final List<String> lines = linesStream.toList();
      Lesson lesson = null;
      final Map<Integer, String> polishMap = new HashMap<>();
      final Map<Integer, String> englishMap = new HashMap<>();

      for (final String line : lines) {
        if (StringUtils.isNoneBlank(line)) {

          if (line.startsWith("Unit")) {
            if (lesson != null) {
              if (polishMap.size() != englishMap.size()) {
                throw new IllegalArgumentException("Maps have invalid size");
              }

              final Lesson savedLesson = lessonService.save(lesson);

              for (int i = 1; i <= polishMap.size(); i++) {
                final Word word = Word.builder()
                                      .polishName(polishMap.get(i))
                                      .partOfSpeech("")
                                      .build();
                final Word savedWord = wordService.save(word);
                final Definition definition = Definition.builder()
                                                        .englishName(englishMap.get(i))
                                                        .type(WordTypeEnum.WORD.toString())
                                                        .word(savedWord)
                                                        .americanSound("")
                                                        .britishSound("")
                                                        .build();
                final Definition savedDefinition = definitionService.save(definition);


                final LessonWord lessonWord = LessonWord.builder()
                                                        .word(savedWord)
                                                        .lesson(savedLesson)
                                                        .position(lessonWordService.getCountByLesson(savedLesson))
                                                        .build();
                final LessonWord savedLessonWord = lessonWordService.save(lessonWord);
              }
            }
            lesson = Lesson.builder()
                           .englishName(line)
                           .polishName(line)
                           .course(course)
                           .position(lessonService.getNextPosition(course.getId()))
                           .build();
            polishMap.clear();
            englishMap.clear();
          } else {
            final int index = Integer.parseInt(line.substring(0, line.indexOf(".")));
            final String value = line.substring(line.indexOf(".") + 1).trim();
            if (!polishMap.containsKey(index)) {
              polishMap.put(index, value);
            } else {
              englishMap.put(index, value);
            }
          }
        }
      }
    }
  }

}
