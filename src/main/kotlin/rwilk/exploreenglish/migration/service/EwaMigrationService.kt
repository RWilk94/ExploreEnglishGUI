package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.service.course.EwaCourseMigrationService
import rwilk.exploreenglish.migration.service.exercise.EwaExerciseMigrationService
import rwilk.exploreenglish.migration.service.lesson.EwaLessonMigrationService

@Service
class EwaMigrationService(
    private val ewaCourseMigrationService: EwaCourseMigrationService,
    private val ewaLessonMigrationService: EwaLessonMigrationService,
    private val ewaExerciseMigrationService: EwaExerciseMigrationService,
) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(EwaMigrationService::class.java)

    override fun run(vararg args: String?) {
         // migrate()
    }

    fun migrate() {
        val finalCourses = ewaCourseMigrationService.migrate()
        logger.info("Migrated ${finalCourses.size} Ewa courses")

        finalCourses.forEach { course ->
            val finalLessons = ewaLessonMigrationService.migrate(course)
            logger.info("Migrated ${finalLessons.size} Ewa lessons for course ${course.name}")

            finalLessons.forEach { lesson ->
                val finalExercises = ewaExerciseMigrationService.migrate(lesson)
                logger.info("Migrated ${finalExercises.size} Ewa exercises for lesson ${lesson.name}")
            }
        }
    }
}
