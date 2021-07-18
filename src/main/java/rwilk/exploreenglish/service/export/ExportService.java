package rwilk.exploreenglish.service.export;

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
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSentence;
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
import rwilk.exploreenglish.utils.WordUtils;

import java.io.PrintWriter;
import java.util.*;

@Slf4j
@Service
public class ExportService {

  private final static String PARAM_SEPARATOR = ", ";
  private final static String QUOTE_SIGN = "'";
  private final static Integer CHUNK_SIZE = 1;
  private final CourseService courseService;
  private final LessonService lessonService;
  private final LessonWordService lessonWordService;
  private final WordService wordService;
  private final WordSentenceService wordSentenceService;
  private final SentenceService sentenceService;
  private final NoteService noteService;
  private final ExerciseService exerciseService;
  private final ExerciseItemService exerciseItemService;
  private final TermService termService;

  public ExportService(CourseService courseService, LessonService lessonService, LessonWordService lessonWordService,
                       WordService wordService, WordSentenceService wordSentenceService, SentenceService sentenceService,
                       NoteService noteService, ExerciseService exerciseService, ExerciseItemService exerciseItemService, TermService termService) {
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.lessonWordService = lessonWordService;
    this.wordService = wordService;
    this.wordSentenceService = wordSentenceService;
    this.sentenceService = sentenceService;
    this.noteService = noteService;
    this.exerciseService = exerciseService;
    this.exerciseItemService = exerciseItemService;
    this.termService = termService;
  }

  public void export() {
    exportVersion();
    exportCourses(courseService.getAll());
    exportLessons(lessonService.getAll());
    exportLessonWords(lessonWordService.getAll());
    exportWords(wordService.getAll());
    exportWordSentences(wordSentenceService.getAll());
    exportSentences(sentenceService.getAll());
    exportNotes(noteService.getAll());
    exportExercises(exerciseService.getAll());
    exportExerciseItems(exerciseItemService.getAll());
    exportTerms(termService.getAll());
  }

  public void exportVersion() {
    String tag = "VERSIONS";
    StringBuilder sb = new StringBuilder(String.valueOf(System.currentTimeMillis()));
    exportFile(sb, tag.toLowerCase() + ".txt", tag);
  }

  private void exportCourses(List<Course> courses) {
    String tag = "COURSES";
    log.info("START GENERATING {}", tag);
    List<List<Course>> chunks = ListUtils.partition(courses, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Course> chunk : chunks) {
      sql.append("INSERT INTO 'courses' ('id', 'english_name', 'polish_name', 'image', 'position') VALUES ");

      for (Course course : chunk) {
        sql.append("\n")
            .append("(")
            .append(course.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(course.getEnglishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(course.getPolishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN POLISH NAME
            .append(PARAM_SEPARATOR)
            .append("NULL") // COLUMN IMAGE
            .append(PARAM_SEPARATOR)
            .append(course.getPosition()); // COLUMN POSITION
        insertEndLineCharacter(sql, chunk, course);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportLessons(List<Lesson> lessons) {
    String tag = "LESSONS";
    log.info("START GENERATING {}", tag);
    List<List<Lesson>> chunks = ListUtils.partition(lessons, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Lesson> chunk : chunks) {
      sql.append("INSERT INTO 'lessons' ('id', 'english_name', 'polish_name', 'image', 'position', 'course_id') VALUES ");

      for (Lesson lesson : chunk) {
        sql.append("\n")
            .append("(")
            .append(lesson.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(lesson.getEnglishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(lesson.getPolishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN POLISH NAME
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
    String tag = "LESSON_WORDS";
    log.info("START GENERATING {}", tag);
    List<List<LessonWord>> chunks = ListUtils.partition(lessonWords, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<LessonWord> chunk : chunks) {
      sql.append("INSERT INTO 'lesson_word' ('id', 'position', 'lesson_id', 'word_id') VALUES ");

      for (LessonWord lessonWord : chunk) {
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

  private void exportWords(List<Word> words) {
    String tag = "WORDS";
    log.info("START GENERATING {}", tag);
    List<List<Word>> chunks = ListUtils.partition(words, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Word> chunk : chunks) {
      sql.append("INSERT INTO 'words' ('id', 'english_name', 'polish_name', 'part_of_speech', 'sound', 'article', " +
          "'comparative', 'superlative', 'past_tense', 'past_participle', 'grammar_type', 'plural', 'synonym', " +
          "'opposite', 'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', " +
          "'series') VALUES ");

      for (Word word : chunk) {
        sql.append("\n")
            .append("(")
            .append(word.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getEnglishNames().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getPolishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN POLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getPartOfSpeech().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PART OF SPEECH
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(word.getSound()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SOUND
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getArticle().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ARTICLE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getComparative().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN COMPARATIVE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getSuperlative().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SUPERLATIVE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getPastTense().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PAST TENSE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getPastParticiple().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PAST PARTICIPLE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getGrammarType().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN GRAMMAR TYPE
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getPlural().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN PLURAL
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getSynonym().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SYNONYM
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(word.getOpposite().replaceAll("'", "''")).append(QUOTE_SIGN); // COLUMN OPPOSITE
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, word);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportWordSentences(List<WordSentence> wordSentences) {
    String tag = "WORD_SENTENCE";
    log.info("START GENERATING {}", tag);
    List<List<WordSentence>> chunks = ListUtils.partition(wordSentences, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<WordSentence> chunk : chunks) {
      sql.append("INSERT INTO 'word_sentence' ('id', 'position', 'word_id', 'sentence_id') VALUES ");

      for (WordSentence wordSentence : chunk) {
        sql.append("\n")
            .append("(")
            .append(wordSentence.getId()) // COLUMN ID
            .append(PARAM_SEPARATOR)
            .append(wordSentence.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(wordSentence.getWord().getId()) // COLUMN WORD ID
            .append(PARAM_SEPARATOR)
            .append(wordSentence.getSentence().getId()); // COLUMN SENTENCE ID
        insertEndLineCharacter(sql, chunk, wordSentence);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportSentences(List<Sentence> sentences) {
    String tag = "SENTENCES";
    log.info("START GENERATING {}", tag);
    List<List<Sentence>> chunks = ListUtils.partition(sentences, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Sentence> chunk : chunks) {
      sql.append("INSERT INTO 'sentences' ('id', 'english_name', 'polish_name', 'sound', " +
          "'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series') VALUES ");
      for (Sentence sentence : chunk) {
        sql.append("\n")
            .append("(")
            .append(sentence.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(sentence.getEnglishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN ENGLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(sentence.getPolishName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN POLISH NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(sentence.getSound()).replaceAll("'", "''")).append(QUOTE_SIGN); // COLUMN SOUND
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, sentence);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportNotes(List<Note> notes) {
    String tag = "NOTES";
    log.info("START GENERATING {}", tag);
    List<List<Note>> chunks = ListUtils.partition(notes, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Note> chunk : chunks) {
      sql.append("INSERT INTO 'notes' ('id', 'note', 'viewed', 'position', 'lesson_id') VALUES ");
      for (Note note : chunk) {
        sql.append("\n")
            .append("(")
            .append(note.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(note.getNote().replaceAll("'", "''").replaceAll("\\n", "\\\\n")).append(QUOTE_SIGN) // COLUMN NOTE
            .append(PARAM_SEPARATOR)
            .append("0")
            .append(PARAM_SEPARATOR)
            .append(note.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(note.getLesson().getId()); // COLUMN LESSON ID
        insertEndLineCharacter(sql, chunk, note);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportExercises(List<Exercise> exercises) {
    String tag = "EXERCISES";
    log.info("START GENERATING {}", tag);
    List<List<Exercise>> chunks = ListUtils.partition(exercises, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<Exercise> chunk : chunks) {
      sql.append("INSERT INTO 'exercises' ('id', 'name', 'type', 'position', 'lesson_id', 'progress', 'skip', " +
          "'difficult', 'correct', 'wrong', 'next_repeat', 'series') VALUES ");
      for (Exercise exercise : chunk) {
        sql.append("\n")
            .append("(")
            .append(exercise.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(exercise.getName().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN NAME
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(exercise.getType().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN TYPE
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

  private void exportExerciseItems(List<ExerciseItem> exerciseItems) {
    String tag = "EXERCISES_ITEMS";
    log.info("START GENERATING {}", tag);
    List<List<ExerciseItem>> chunks = ListUtils.partition(exerciseItems, CHUNK_SIZE);
    StringBuilder sql = new StringBuilder();
    for (List<ExerciseItem> chunk : chunks) {
      sql.append("INSERT INTO 'exercise_items' ('id', 'question', 'correct_answer', 'final_answer', " +
          "'first_possible_answer', 'second_possible_answer', 'third_possible_answer', 'forth_possible_answer', " +
          "'dialogue_english', 'dialogue_polish', 'description', 'position', 'exercise_id') VALUES ");
      for (ExerciseItem exerciseItem : chunk) {
        sql.append("\n")
            .append("(")
            .append(exerciseItem.getId()) // COLUMN_ID
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(exerciseItem.getQuestion().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN QUESTION
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(exerciseItem.getCorrectAnswer().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN CORRECT ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getFinalAnswer()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN FINAL ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getFirstPossibleAnswer()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN FIRST POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getSecondPossibleAnswer()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SECOND POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getThirdPossibleAnswer()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN THIRD POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getForthPossibleAnswer()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN FORTH POSSIBLE ANSWER
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getDialogueEnglish()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN DIALOGUE ENGLISH
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getDialoguePolish()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN DIALOGUE POLISH
            .append(PARAM_SEPARATOR)
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(exerciseItem.getDescription()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN DESCRIPTION
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
    log.info("START GENERATING {}", tag);
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
          englishNames = englishNames.substring(englishNames.length() - 1).equals(";")
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
            .append(term.getIsIgnored() ? 1 : 0) // COLUMN IS_IGNORED
            .append()
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
    if (((List) chunk).indexOf(course) + 1 == ((List) chunk).size()) {
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
