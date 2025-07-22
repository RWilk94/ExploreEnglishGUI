package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.service.course.EtutorCourseMigrationService
import rwilk.exploreenglish.migration.service.exercise.EtutorExerciseMigrationService
import rwilk.exploreenglish.migration.service.exerciseitem.EtutotExerciseItemMigrationService
import rwilk.exploreenglish.migration.service.lesson.EtutorLessonMigrationService
import rwilk.exploreenglish.migration.service.note.EtutorNoteMigrationService
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType

@Service
class EtutorMigrationService(
    private val etutorCourseMigrationService: EtutorCourseMigrationService,
    private val etutorLessonMigrationService: EtutorLessonMigrationService,
    private val etutorExerciseMigrationService: EtutorExerciseMigrationService,
    private val etutorExerciseItemMigrationService: EtutotExerciseItemMigrationService,
    private val etutorNoteMigrationService: EtutorNoteMigrationService,
) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(EtutorMigrationService::class.java)

    override fun run(vararg args: String?) {
        // migrate()
    }

    fun migrate() {
        val finalCourses = etutorCourseMigrationService.migrate()
        logger.info("Migrated ${finalCourses.size} Etutor courses")

        finalCourses.forEach { course ->
            val finalLessons = etutorLessonMigrationService.migrate(course)
            logger.info("Migrated ${finalLessons.size} Etutor lessons for course ${course.name}")

            finalLessons.forEach { lesson ->
                val finalExercises = etutorExerciseMigrationService.migrate(lesson)
                logger.info("Migrated ${finalExercises.size} Etutor exercises for lesson ${lesson.name}")

                finalExercises.forEach { exercise ->
                    migrateExerciseItems(exercise)
                    migrateNotes(exercise)
                    migrateDialogues(exercise)
                    // TODO migrate dialogues
                    // TODO migrate word lists
                }
            }
        }
    }

    private fun migrateExerciseItems(finalExercise: FinalExercise) {
        if (listOf(
                ExerciseType.PICTURES_LISTENING,
                ExerciseType.PICTURES_CHOICE,
                ExerciseType.EXERCISE,
                ExerciseType.MATCHING_PAIRS,
                ExerciseType.PICTURES_MASKED_WRITING,
                ExerciseType.SPEAKING,
                ExerciseType.MATCHING_PAIRS_GRAMMAR,
                ExerciseType.DIALOG,
                ExerciseType.READING
            ).contains(ExerciseType.valueOf(finalExercise.type!!))
        ) {
            etutorExerciseItemMigrationService.migrate(finalExercise)
        }
    }

    private fun migrateNotes(finalExercise: FinalExercise) {
        if (listOf(
                ExerciseType.SCREEN,
                ExerciseType.SCREEN_MUSIC,
                ExerciseType.SCREEN_CULINARY,
                ExerciseType.SCREEN_CULTURAL,
                ExerciseType.TIP,
                ExerciseType.READING,
                ExerciseType.GRAMMAR_NOTE,
                ExerciseType.SPEAKING,
                ExerciseType.WRITING,
            ).contains(ExerciseType.valueOf(finalExercise.type!!))
        ) {
            etutorNoteMigrationService.migrate(finalExercise)
        }
    }

    private fun migrateDialogues(finalExercise: FinalExercise) {
        if (listOf(
                ExerciseType.COMIC_BOOK,
                ExerciseType.DIALOG,
                ExerciseType.VIDEO,
            ).contains(ExerciseType.valueOf(finalExercise.type!!))
        ) {
            // etutorDialogMigrationService.migrate(finalExercise)
        }
    }
}
