package rwilk.exploreenglish.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.export.generator.*;
import rwilk.exploreenglish.model.entity.etutor.*;
import rwilk.exploreenglish.repository.etutor.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceV2Impl implements ExportServiceV2, CommandLineRunner {

    private final EtutorCourseRepository etutorCourseRepository;
    private final EtutorLessonRepository etutorLessonRepository;
    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorNoteRepository etutorNoteRepository;
    private final EtutorNoteItemRepository etutorNoteItemRepository;
    private final EtutorDialogRepository etutorDialogRepository;
    private final EtutorExerciseItemRepository etutorExerciseItemRepository;
    private final EtutorLessonWordRepository etutorLessonWordRepository;

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
        // export();
    }

    @Override
    public void export() {
        final List<EtutorCourse> etutorCourses = etutorCourseRepository.findAll();
        etutorCourseSqlGenerator.generateSql(etutorCourses, "");

        for (final EtutorCourse etutorCourse : etutorCourses) {

            final String directoryAlias = etutorCourse.getId().toString();

            final List<EtutorLesson> etutorLessons = etutorLessonRepository.findAllByCourse_Id(etutorCourse.getId());
            etutorLessonSqlGenerator.generateSql(etutorLessons, directoryAlias);

            final List<EtutorExercise> etutorExercises = new ArrayList<>();
            for (final EtutorLesson etutorLesson : etutorLessons) {
                etutorExercises.addAll(etutorExerciseRepository.findAllByLesson_Id(etutorLesson.getId()));
            }
            etutorExerciseSqlGenerator.generateSql(etutorExercises, directoryAlias);

            final List<EtutorNote> etutorNotes = new ArrayList<>();
            final List<EtutorExerciseItem> etutorExerciseItems = new ArrayList<>();
            final List<EtutorDialogItem> etutorDialogItems = new ArrayList<>();
            final List<EtutorLessonWord> etutorLessonWords = new ArrayList<>();
            for (final EtutorExercise etutorExercise : etutorExercises) {
                etutorNotes.addAll(etutorNoteRepository.findAllByExercise_Id(etutorExercise.getId()));
                etutorExerciseItems.addAll(etutorExerciseItemRepository.findAllByExercise_Id(etutorExercise.getId()));
                etutorDialogItems.addAll(etutorDialogRepository.findAllByExercise_Id(etutorExercise.getId()));
                etutorLessonWords.addAll(etutorLessonWordRepository.findAllByExercise_Id(etutorExercise.getId()));
            }
            etutorExerciseItemSqlGenerator.generateSql(etutorExerciseItems, directoryAlias);
            etutorDialogSqlGenerator.generateSql(etutorDialogItems, directoryAlias);
            etutorLessonWordSqlGenerator.generateSql(etutorLessonWords, directoryAlias);

            final List<EtutorNoteItem> etutorNoteItems = new ArrayList<>();
            for (final EtutorNote etutorNote : etutorNotes) {
                etutorNoteItems.addAll(etutorNoteItemRepository.findAllByNote_Id(etutorNote.getId()));
            }
            etutorNoteSqlGenerator.generateSql(etutorNotes, directoryAlias);
            etutorNoteItemSqlGenerator.generateSql(etutorNoteItems, directoryAlias);

            final List<EtutorWord> etutorWords = new ArrayList<>();
            for (final EtutorLessonWord etutorLessonWord : etutorLessonWords) {
                etutorWords.add(etutorLessonWord.getWord());
            }

            final List<EtutorDefinition> etutorDefinitions = new ArrayList<>();
            for (final EtutorWord etutorWord : etutorWords) {
                etutorDefinitions.addAll(etutorWord.getDefinitions());
            }
            etutorWordSqlGenerator.generateSql(etutorWords, directoryAlias);
            etutorDefinitionSqlGenerator.generateSql(etutorDefinitions, directoryAlias);
        }

    }
}
