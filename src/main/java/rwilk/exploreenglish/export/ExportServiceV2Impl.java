package rwilk.exploreenglish.export;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import rwilk.exploreenglish.export.generator.*;
import rwilk.exploreenglish.model.entity.etutor.*;
import rwilk.exploreenglish.repository.etutor.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceV2Impl implements ExportServiceV2, CommandLineRunner {

  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorLessonRepository etutorLessonRepository;
  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorNoteRepository etutorNoteRepository;
  private final EtutorNoteItemRepository etutorNoteItemRepository;
  private final EtutorDefinitionRepository etutorDefinitionRepository;
  private final EtutorDialogRepository etutorDialogRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;
  private final EtutorLessonWordRepository etutorLessonWordRepository;
  private final EtutorWordRepository etutorWordRepository;

  private final EtutorCourseSqlGenerator etutorCourseSqlGenerator;
  private final EtutorLessonSqlGenerator etutorLessonSqlGenerator;
  private final EtutorExerciseSqlGenerator etutorExerciseSqlGenerator;
  private final EtutorNoteSqlGenerator etutorNoteSqlGenerator;
  private final EtutorNoteItemSqlGenerator etutorNoteItemSqlGenerator;
  private final EtutorDefinitionSqlGenerator etutorDefinitionSqlGenerator;
  private final EtutorDialogItemSqlGenerator etutorDialogSqlGenerator;
  private final EtutorExerciseItemSqlGenerator etutorExerciseItemSqlGenerator;
  private final EtutorLessonWordSqlGenerator etutorLessonWordSqlGenerator;
  private final EtutorWordSqlGenerator etutorWordSqlGenerator;

  @Override
  public void run(final String... args) throws Exception {
    export();
  }

  @Override
  public void export() {
    final List<EtutorCourse> etutorCourses = etutorCourseRepository.findAll();
    final List<EtutorLesson> etutorLessons = etutorLessonRepository.findAll();
    final List<EtutorExercise> etutorExercises = etutorExerciseRepository.findAll();
    final List<EtutorNote> etutorNotes = etutorNoteRepository.findAll();
    final List<EtutorNoteItem> etutorNoteItems = etutorNoteItemRepository.findAll();
    final List<EtutorDefinition> etutorDefinitions = etutorDefinitionRepository.findAll();
    final List<EtutorDialogItem> etutorDialogItems = etutorDialogRepository.findAll();
    final List<EtutorExerciseItem> etutorExerciseItems = etutorExerciseItemRepository.findAll();
    final List<EtutorLessonWord> etutorLessonWords = etutorLessonWordRepository.findAll();
    final List<EtutorWord> etutorWords = etutorWordRepository.findAll();

    // TODO fix all generators after unifying names
    etutorCourseSqlGenerator.generateSql(etutorCourses);
    etutorLessonSqlGenerator.generateSql(etutorLessons);
    etutorExerciseSqlGenerator.generateSql(etutorExercises);
    etutorNoteSqlGenerator.generateSql(etutorNotes);
    etutorNoteItemSqlGenerator.generateSql(etutorNoteItems);
    etutorDefinitionSqlGenerator.generateSql(etutorDefinitions);
    etutorDialogSqlGenerator.generateSql(etutorDialogItems);
    etutorExerciseItemSqlGenerator.generateSql(etutorExerciseItems);
    etutorLessonWordSqlGenerator.generateSql(etutorLessonWords);
    etutorWordSqlGenerator.generateSql(etutorWords);
  }
}
