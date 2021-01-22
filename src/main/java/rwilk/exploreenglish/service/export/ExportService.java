package rwilk.exploreenglish.service.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.ExerciseItemService;
import rwilk.exploreenglish.service.ExerciseService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.NoteService;
import rwilk.exploreenglish.service.SentenceService;
import rwilk.exploreenglish.service.WordService;

import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Service
public class ExportService implements CommandLineRunner {

  private final static String PARAM_SEPARATOR = ", ";
  private final static String QUOTE_SIGN = "'";
  private final CourseService courseService;
  private final LessonService lessonService;
  private final WordService wordService;
  private final SentenceService sentenceService;
  private final NoteService noteService;
  private final ExerciseService exerciseService;
  private final ExerciseItemService exerciseItemService;

  public ExportService(CourseService courseService, LessonService lessonService, WordService wordService,
                       SentenceService sentenceService, NoteService noteService, ExerciseService exerciseService,
                       ExerciseItemService exerciseItemService) {
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.sentenceService = sentenceService;
    this.noteService = noteService;
    this.exerciseService = exerciseService;
    this.exerciseItemService = exerciseItemService;
  }

  public void export() {
    exportCourses(courseService.getAll());
    exportLessons(lessonService.getAll());
    exportWords(wordService.getAll());
    exportSentences(sentenceService.getAll());
    exportNotes(noteService.getAll());
    exportExercises(exerciseService.getAll());
    exportExerciseItems(exerciseItemService.getAll());
  }

  private void exportCourses(List<Course> courses) {
    String tag = "COURSES";
    log.info("START GENERATING {}", tag);
    List<List<Course>> chunks = ListUtils.partition(courses, 100);
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
    List<List<Lesson>> chunks = ListUtils.partition(lessons, 100);
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

  private void exportWords(List<Word> words) {
    String tag = "WORDS";
    log.info("START GENERATING {}", tag);
    List<List<Word>> chunks = ListUtils.partition(words, 100);
    StringBuilder sql = new StringBuilder();
    for (List<Word> chunk : chunks) {
      sql.append("INSERT INTO 'words' ('id', 'english_name', 'polish_name', 'part_of_speech', 'sound', 'article', " +
          "'comparative', 'superlative', 'past_tense', 'past_participle', 'grammar_type', 'plural', 'synonym', " +
          "'opposite', 'position', 'lesson_id', 'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', " +
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
            .append(QUOTE_SIGN).append(word.getOpposite().replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN OPPOSITE
            .append(PARAM_SEPARATOR)
            .append(word.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(word.getLesson().getId()); // COLUMN LESSON ID
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, word);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportSentences(List<Sentence> sentences) {
    String tag = "SENTENCES";
    log.info("START GENERATING {}", tag);
    List<List<Sentence>> chunks = ListUtils.partition(sentences, 100);
    StringBuilder sql = new StringBuilder();
    for (List<Sentence> chunk : chunks) {
      sql.append("INSERT INTO 'sentences' ('id', 'english_name', 'polish_name', 'sound', 'position', 'word_id', " +
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
            .append(QUOTE_SIGN).append(StringUtils.trimToEmpty(sentence.getSound()).replaceAll("'", "''")).append(QUOTE_SIGN) // COLUMN SOUND
            .append(PARAM_SEPARATOR)
            .append(sentence.getPosition()) // COLUMN POSITION
            .append(PARAM_SEPARATOR)
            .append(sentence.getWord().getId()); // COLUMN WORD ID
        insertRepeatablePart(sql);
        insertEndLineCharacter(sql, chunk, sentence);
      }
    }
    exportFile(sql, tag.toLowerCase() + ".txt", tag);
  }

  private void exportNotes(List<Note> notes) {
    String tag = "NOTES";
    log.info("START GENERATING {}", tag);
    List<List<Note>> chunks = ListUtils.partition(notes, 100);
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
    List<List<Exercise>> chunks = ListUtils.partition(exercises, 100);
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
    List<List<ExerciseItem>> chunks = ListUtils.partition(exerciseItems, 100);
    StringBuilder sql = new StringBuilder();
    for (List<ExerciseItem> chunk : chunks) {
      sql.append("INSERT INTO 'exercise_item' ('id', 'question', 'correct_answer', 'final_answer', " +
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


  @Override
  public void run(String... args) throws Exception {
    export();
  }
}
