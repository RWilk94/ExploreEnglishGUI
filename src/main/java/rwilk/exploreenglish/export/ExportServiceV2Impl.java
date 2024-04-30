package rwilk.exploreenglish.export;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import rwilk.exploreenglish.export.generator.EtutorCourseSqlGenerator;
import rwilk.exploreenglish.export.generator.EtutorExerciseSqlGenerator;
import rwilk.exploreenglish.export.generator.EtutorLessonSqlGenerator;
import rwilk.exploreenglish.export.generator.EtutorNoteItemSqlGenerator;
import rwilk.exploreenglish.export.generator.EtutorNoteSqlGenerator;
import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorLesson;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceV2Impl implements ExportServiceV2, CommandLineRunner {

  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorLessonRepository etutorLessonRepository;
  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorNoteRepository etutorNoteRepository;
  private final EtutorNoteItemRepository etutorNoteItemRepository;

  private final EtutorCourseSqlGenerator etutorCourseSqlGenerator;
  private final EtutorLessonSqlGenerator etutorLessonSqlGenerator;
  private final EtutorExerciseSqlGenerator etutorExerciseSqlGenerator;
  private final EtutorNoteSqlGenerator etutorNoteSqlGenerator;
  private final EtutorNoteItemSqlGenerator etutorNoteItemSqlGenerator;

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

    etutorCourseSqlGenerator.generateSql(etutorCourses);
    etutorLessonSqlGenerator.generateSql(etutorLessons);
    etutorExerciseSqlGenerator.generateSql(etutorExercises);
    etutorNoteSqlGenerator.generateSql(etutorNotes);
    etutorNoteItemSqlGenerator.generateSql(etutorNoteItems);
  }
}
