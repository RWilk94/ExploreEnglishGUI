package rwilk.exploreenglish.service.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSound;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.ExerciseItemService;
import rwilk.exploreenglish.service.ExerciseService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.NoteService;
import rwilk.exploreenglish.service.SentenceService;
import rwilk.exploreenglish.service.TermService;
import rwilk.exploreenglish.service.WordSentenceService;
import rwilk.exploreenglish.service.WordService;
import rwilk.exploreenglish.service.WordSoundService;
import rwilk.exploreenglish.utils.WordUtils;

import java.io.PrintWriter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

  private static final String PARAM_SEPARATOR = ", ";
  private static final String QUOTE_SIGN = "'";
  private static final Integer CHUNK_SIZE = 1;
  private static final String LOG_PREFIX = "START GENERATING {}";
  private final CourseService courseService;
  private final LessonService lessonService;
  private final LessonWordService lessonWordService;
  private final WordService wordService;
  private final WordSoundService wordSoundService;
  private final WordSentenceService wordSentenceService;
  private final SentenceService sentenceService;
  private final NoteService noteService;
  private final ExerciseService exerciseService;
  private final ExerciseItemService exerciseItemService;
  private final TermService termService;

  public void export() {
    exportVersion();
    exportCourses(courseService.getAll());
    exportLessons(lessonService.getAll());
    exportLessonWords(lessonWordService.getAll());
    exportWords(wordService.getAll());
    exportWordSounds(wordSoundService.getAll());
    // exportWordSentences(wordSentenceService.getAll());
    // exportSentences(sentenceService.getAll());
    exportNotes(noteService.getAll());
    exportExercises(exerciseService.getAll());
    exportExerciseItems(exerciseItemService.getAll());
    // exportTerms(termService.getAll());
  }

  public void exportVersion() {
    String tag = "VERSIONS";
    StringBuilder sb = new StringBuilder(String.valueOf(System.currentTimeMillis()));
    exportFile(sb, tag.toLowerCase() + ".txt", tag);
  }

  private void exportCourses(final List<Course> courses) {
    final String tag = "COURSES";
    log.info(LOG_PREFIX, tag);

    final List<List<Course>> chunks = ListUtils.partition(courses, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<Course> chunk : chunks) {
      sql.append("INSERT INTO 'courses' ('id', 'english_name', 'polish_name', 'image', 'position') VALUES ");

      for (final Course course : chunk) {
        sql.append("\n")
           .append("(")
           .append(course.getId()) // COLUMN_ID
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(course.getEnglishName())).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(course.getPolishName())).append(QUOTE_SIGN) // COLUMN POLISH NAME
           .append(PARAM_SEPARATOR)
           .append("NULL") // COLUMN IMAGE
           .append(PARAM_SEPARATOR)
           .append(course.getPosition()); // COLUMN POSITION
        insertEndLineCharacter(sql, chunk, course);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportLessons(final List<Lesson> lessons) {
    final String tag = "LESSONS";
    log.info(LOG_PREFIX, tag);

    final List<List<Lesson>> chunks = ListUtils.partition(lessons, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<Lesson> chunk : chunks) {
      sql.append("INSERT INTO 'lessons' ('id', 'english_name', 'polish_name', 'image', 'position', 'course_id') VALUES ");

      for (final Lesson lesson : chunk) {
        sql.append("\n")
           .append("(")
           .append(lesson.getId()) // COLUMN_ID
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(lesson.getEnglishName())).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(lesson.getPolishName())).append(QUOTE_SIGN) // COLUMN POLISH NAME
           .append(PARAM_SEPARATOR)
           .append("NULL") // COLUMN IMAGE
           .append(PARAM_SEPARATOR)
           .append(lesson.getPosition()) // COLUMN POSITION
           .append(PARAM_SEPARATOR)
           .append(lesson.getCourse().getId()); // COLUMN COURSE ID
        insertEndLineCharacter(sql, chunk, lesson);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportLessonWords(List<LessonWord> lessonWords) {
    final String tag = "LESSON_WORDS";
    log.info(LOG_PREFIX, tag);

    final List<List<LessonWord>> chunks = ListUtils.partition(lessonWords, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<LessonWord> chunk : chunks) {
      sql.append("INSERT INTO 'lesson_word' ('id', 'position', 'lesson_id', 'word_id') VALUES ");

      for (final LessonWord lessonWord : chunk) {
        sql.append("\n")
            .append("(")
            .append(lessonWord.getId()) // COLUMN ID
            .append(PARAM_SEPARATOR)
            .append(lessonWord.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(lessonWord.getLesson().getId()) // COLUMN LESSON ID
            .append(PARAM_SEPARATOR)
            .append(lessonWord.getWord().getId()); // COLUMN WORD ID
        insertEndLineCharacter(sql, chunk, lessonWord);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportWords(final List<Word> words) {
    final String tag = "WORDS";
    log.info(LOG_PREFIX, tag);

    final List<List<Word>> chunks = ListUtils.partition(words, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<Word> chunk : chunks) {
      sql.append("INSERT INTO 'words' ('id', 'polish_name', 'part_of_speech', 'article', 'grammar_type', 'level'"
                 + "'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series') VALUES ");

      for (final Word word : chunk) {
        sql.append("\n")
           .append("(")
           .append(word.getId()) // COLUMN_ID
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(word.getPolishName())).append(QUOTE_SIGN) // COLUMN POLISH NAME
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(word.getPartOfSpeech())).append(QUOTE_SIGN) // COLUMN PART OF SPEECH
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(word.getArticle())).append(QUOTE_SIGN) // COLUMN ARTICLE
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(word.getGrammarType())).append(QUOTE_SIGN) // COLUMN GRAMMAR TYPE
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(word.getLevel())).append(QUOTE_SIGN); // COLUMN LEVEL
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, word);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportWordSounds(final List<WordSound> wordSounds) {
    final String tag = "WORD_SOUNDS";
    log.info(LOG_PREFIX, tag);

    final List<List<WordSound>> chunks = ListUtils.partition(wordSounds, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<WordSound> chunk : chunks) {
      sql.append("INSERT INTO 'word_sounds' ('id', 'type', 'english_name', 'british_sound', 'american_sound', " +
                 "'word_id') VALUES ");

      for (final WordSound wordSound : chunk) {
        sql.append("\n")
           .append("(")
           .append(wordSound.getId()) // COLUMN_ID
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(wordSound.getType())).append(QUOTE_SIGN) // COLUMN TYPE
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(wordSound.getEnglishName())).append(QUOTE_SIGN) // COLUMN ENGLISH_NAME
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(wordSound.getBritishSound())).append(QUOTE_SIGN) // COLUMN BRITISH_SOUND
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceApostrophe(wordSound.getAmericanSound())).append(QUOTE_SIGN) // COLUMN AMERICAN_SOUND
           .append(PARAM_SEPARATOR)
           .append(wordSound.getWord().getId()); // COLUMN WORD_ID
        insertEndLineCharacter(sql, chunk, wordSound);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

//  private void exportWordSentences(List<WordSentence> wordSentences) {
//    String tag = "WORD_SENTENCE";
//    log.info(LOG_PREFIX, tag);
//    List<List<WordSentence>> chunks = ListUtils.partition(wordSentences, CHUNK_SIZE);
//    StringBuilder sql = new StringBuilder();
//    for (List<WordSentence> chunk : chunks) {
//      sql.append("INSERT INTO 'word_sentence' ('id', 'position', 'word_id', 'sentence_id') VALUES ");
//
//      for (WordSentence wordSentence : chunk) {
//        sql.append("\n")
//            .append("(")
//            .append(wordSentence.getId()) // COLUMN ID
//            .append(PARAM_SEPARATOR)
//            .append(wordSentence.getPosition()) // COLUMN POSITION
//            .append(PARAM_SEPARATOR)
//            .append(wordSentence.getWord().getId()) // COLUMN WORD ID
//            .append(PARAM_SEPARATOR)
//            .append(wordSentence.getSentence().getId()); // COLUMN SENTENCE ID
//        insertEndLineCharacter(sql, chunk, wordSentence);
//      }
//    }
//    exportFile(sql, tag.toLowerCase() + ".txt", tag);
//  }

//  private void exportSentences(List<Sentence> sentences) {
//    String tag = "SENTENCES";
//    log.info(LOG_PREFIX, tag);
//    List<List<Sentence>> chunks = ListUtils.partition(sentences, CHUNK_SIZE);
//    StringBuilder sql = new StringBuilder();
//    for (List<Sentence> chunk : chunks) {
//      sql.append("INSERT INTO 'sentences' ('id', 'english_name', 'polish_name', 'sound', " +
//          "'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series') VALUES ");
//      for (Sentence sentence : chunk) {
//        sql.append("\n")
//            .append("(")
//            .append(sentence.getId()) // COLUMN_ID
//            .append(PARAM_SEPARATOR)
//            .append(QUOTE_SIGN).append(sentence.getEnglishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
//            .append(PARAM_SEPARATOR)
//            .append(QUOTE_SIGN).append(sentence.getPolishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN POLISH NAME
//            .append(PARAM_SEPARATOR)
//            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(sentence.getSound()).replaceAll("'", "''")).append(QUOTE_SIGN); // COLUMN SOUND
//        insertRepeatablePart(sql);
//        insertEndLineCharacter(sql, chunk, sentence);
//      }
//    }
//    exportFile(sql, tag.toLowerCase() + ".txt", tag);
//  }

  private void exportNotes(final List<Note> notes) {
    final String tag = "NOTES";
    log.info(LOG_PREFIX, tag);

    final List<List<Note>> chunks = ListUtils.partition(notes, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<Note> chunk : chunks) {
      sql.append("INSERT INTO 'notes' ('id', 'note', 'viewed', 'position', 'lesson_id') VALUES ");

      for (final Note note : chunk) {
        sql.append("\n")
           .append("(")
           .append(note.getId()) // COLUMN_ID
           .append(PARAM_SEPARATOR)
           .append(QUOTE_SIGN).append(replaceNewLine(replaceApostrophe(note.getNote()))).append(QUOTE_SIGN) // COLUMN NOTE
           .append(PARAM_SEPARATOR)
           .append("0") // COLUMNT VIEWED
           .append(PARAM_SEPARATOR)
           .append(note.getPosition()) // COLUMN POSITION
           .append(PARAM_SEPARATOR)
           .append(note.getLesson().getId()); // COLUMN LESSON ID
        insertEndLineCharacter(sql, chunk, note);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportExercises(final List<Exercise> exercises) {
    final String tag = "EXERCISES";
    log.info(LOG_PREFIX, tag);

    final List<List<Exercise>> chunks = ListUtils.partition(exercises, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<Exercise> chunk : chunks) {
      sql.append("INSERT INTO 'exercises' ('id', 'name', 'type', 'position', 'lesson_id', 'progress', 'skip', " +
          "'difficult', 'correct', 'wrong', 'next_repeat', 'series') VALUES ");

      for (final Exercise exercise : chunk) {
        sql.append("\n")
            .append("(")
            .append(exercise.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exercise.getName())).append(QUOTE_SIGN) // COLUMN NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exercise.getType())).append(QUOTE_SIGN) // COLUMN TYPE
            .append(PARAM_SEPARATOR)
            .append(exercise.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(exercise.getLesson().getId()); // COLUMN LESSON ID
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, exercise);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportExerciseItems(final List<ExerciseItem> exerciseItems) {
    final String tag = "EXERCISES_ITEMS";
    log.info(LOG_PREFIX, tag);

    final List<List<ExerciseItem>> chunks = ListUtils.partition(exerciseItems, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();
    for (final List<ExerciseItem> chunk : chunks) {
      sql.append("INSERT INTO 'exercise_items' ('id', 'question', 'correct_answer', 'final_answer', " +
          "'first_possible_answer', 'second_possible_answer', 'third_possible_answer', 'forth_possible_answer', " +
          "'dialogue_english', 'dialogue_polish', 'description', 'position', 'exercise_id') VALUES ");

      for (final ExerciseItem exerciseItem : chunk) {
        sql.append("\n")
            .append("(")
            .append(exerciseItem.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getQuestion())).append(QUOTE_SIGN) // COLUMN QUESTION
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getCorrectAnswer())).append(QUOTE_SIGN) // COLUMN CORRECT ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getFinalAnswer())).append(QUOTE_SIGN) // COLUMN FINAL ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getFirstPossibleAnswer())).append(QUOTE_SIGN) // COLUMN FIRST POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getSecondPossibleAnswer())).append(QUOTE_SIGN) // COLUMN SECOND POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getThirdPossibleAnswer())).append(QUOTE_SIGN) // COLUMN THIRD POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getForthPossibleAnswer())).append(QUOTE_SIGN) // COLUMN FORTH POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getDialogueEnglish())).append(QUOTE_SIGN) // COLUMN DIALOGUE ENGLISH
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getDialoguePolish())).append(QUOTE_SIGN) // COLUMN DIALOGUE POLISH
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(replaceApostrophe(exerciseItem.getDescription())).append(QUOTE_SIGN) // COLUMN DESCRIPTION
            .append(PARAM_SEPARATOR)
            .append(exerciseItem.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(exerciseItem.getExercise().getId()); // COLUMN EXERCISE ID
        insertEndLineCharacter(sql, chunk, exerciseItem);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportTerms(List<Term> terms) {
    String tag = "TERMS";
    log.info(LOG_PREFIX, tag);
    List<List<Term>> chunks = ListUtils.partition(terms, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Term> chunk : chunks) {
      sql.append("INSERT INTO 'terms' ('id', 'english_names', 'polish_name', 'part_of_speech', " +
          "'comparative', 'superlative', 'past_tense', 'past_participle', 'plural', 'synonym', " +
          "'category', 'source', 'is_ignored', 'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', " +
          "'series') VALUES ");

      for (Term term : chunk) {
        String englishNames = (StringUtils.isNoneEmpty(term.getEnglishName()) ? term.getEnglishName() + "; " : "")
            .concat((StringUtils.isNoneEmpty(term.getAmericanName()) ? term.getAmericanName() + "; " : ""))
            .concat((StringUtils.isNoneEmpty(term.getOtherName()) ? term.getOtherName() + "; " : "")).trim();
        if (StringUtils.isBlank(englishNames)) {
          englishNames = "";
        } else {
          englishNames = englishNames.endsWith(";")
              ? englishNames.substring(0, englishNames.length() - 1)
              : englishNames;
          if (englishNames.startsWith("a ")) {
            englishNames = englishNames.substring(englishNames.indexOf(" "));
          } else if (englishNames.startsWith("an ")) {
            englishNames = englishNames.substring(englishNames.indexOf(" "));
          } else if (englishNames.startsWith("the ")) {
            englishNames = englishNames.substring(englishNames.indexOf(" "));
          }
        }

        sql.append("\n")
            .append("(")
            .append(term.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(WordUtils.replaceSpecialText(englishNames).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ENGLISH NAMES
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(term.getPolishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN POLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getPartOfSpeech()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PART OF SPEECH
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getComparative()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN COMPARATIVE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getSuperlative()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SUPERLATIVE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getPastTense()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PAST TENSE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getPastParticiple()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PAST PARTICIPLE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getPlural()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PLURAL
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getSynonym()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SYNONYM
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getCategory()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN CATEGORY
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(term.getSource()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SOURCE
            .append(PARAM_SEPARATOR)
            .append(term.getIsIgnored() ? 1 : 0); // COLUMN IS_IGNORED
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, term);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void insertRepeatablePart(StringBuilder sql) {
    sql.append(PARAM_SEPARATOR)
        .append(-1) // COLUMN PROGRESS
        .append(PARAM_SEPARATOR)
        .append(0) // COLUMN SKIP
        .append(PARAM_SEPARATOR)
        .append(0) // COLUMN DIFFICULT
        .append(PARAM_SEPARATOR)
        .append(0) // COLUMN CORRECT
        .append(PARAM_SEPARATOR)
        .append(0) // COLUMN WRONG
        .append(PARAM_SEPARATOR)
        .append(0) // COLUMN NEXT REPEAT
        .append(PARAM_SEPARATOR)
        .append(0);// COLUMN SERIES
  }

  private void insertEndLineCharacter(StringBuilder sql, Object chunk, Object course) {
    if (((List<?>) chunk).indexOf(course) + 1 == ((List<?>) chunk).size()) {
      sql.append(");\n");
    } else {
      sql.append("),");
    }
  }

  private void exportFile(StringBuilder sql, String fileName, String tag) {
    try (PrintWriter out = new PrintWriter("scripts/" + fileName)) {
      out.println(sql.toString());
      log.info("FINISH GENERATING {}", tag);
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING {}: ", tag, e);
    }
  }

  private String replaceApostrophe(final String text) {
    return StringUtils.trimToEmpty(StringUtils.defaultString(text)).replace("'", "''");
  }

  private String replaceNewLine(final String text) {
    return StringUtils.trimToEmpty(StringUtils.defaultString(text)).replace("\\n", "\\\\n");
  }

//  @Override
//  public void run(String... args) throws Exception {
//
//    final Lesson lesson = lessonService.getById(121L).get();
//
//    final List<Triple<Integer, String, String>> triples = Arrays.asList(
//        Triple.of(1 , "", ""),
//        Triple.of(2 , "", ""),
//        Triple.of(3 , "", ""),
//        Triple.of(4 , "", ""),
//        Triple.of(5 , "", ""),
//        Triple.of(6 , "", ""),
//        Triple.of(7 , "", ""),
//        Triple.of(8 , "", ""),
//        Triple.of(9 , "", ""),
//        Triple.of(10, "", ""),
//        Triple.of(11, "", ""),
//        Triple.of(12, "", ""),
//        Triple.of(13, "", ""),
//        Triple.of(14, "", ""),
//        Triple.of(15, "", ""),
//        Triple.of(16, "", ""),
//        Triple.of(17, "", ""),
//        Triple.of(18, "", ""),
//        Triple.of(19, "", ""),
//        Triple.of(20, "", ""),
//        Triple.of(21, "", ""),
//        Triple.of(22, "", ""),
//        Triple.of(23, "", ""),
//        Triple.of(24, "", ""),
//        Triple.of(25, "", ""),
//        Triple.of(26, "", ""),
//        Triple.of(27, "", ""),
//        Triple.of(28, "", ""),
//        Triple.of(29, "", ""),
//        Triple.of(30, "", "")
//    );
//
//    triples.forEach(item -> {
//      Word word = Word.builder()
//          .id(null)
//          .englishNames(item.getRight())
//          .polishName(item.getMiddle())
//          .partOfSpeech(PartOfSpeechEnum.WYRAZENIE.getValue())
//          .article("")
//          .grammarType("")
//          .comparative("")
//          .superlative("")
//          .pastTense("")
//          .pastParticiple("")
//          .plural("")
//          .opposite("")
//          .synonym("")
//          .build();
//      try {
//        word = wordService.save(word);
//        final LessonWord lessonWord = LessonWord.builder()
//            .lesson(lesson)
//            .word(word)
//            .position(lessonWordService.getCountByLesson(lesson))
//            .build();
//
//        lessonWordService.save(lessonWord);
//      } catch (Exception e) {}
//
//    });
//  }
}
