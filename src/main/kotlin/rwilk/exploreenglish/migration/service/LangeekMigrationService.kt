package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.service.course.LangeekCourseMigrationService
import rwilk.exploreenglish.migration.service.exercise.LangeekExerciseMigrationService
import rwilk.exploreenglish.migration.service.lesson.LangeekLessonMigrationService

@Service
class LangeekMigrationService(
    private val langeekCourseMigrationService: LangeekCourseMigrationService,
    private val langeekLessonMigrationService: LangeekLessonMigrationService,
    private val langeekExerciseMigrationService: LangeekExerciseMigrationService,

    ) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(LangeekMigrationService::class.java)

    override fun run(vararg args: String?) {
        migrate()
    }

    fun migrate() {
        val finalCourses = langeekCourseMigrationService.migrate()
        logger.info("Migrated ${finalCourses.size} Langeek courses")

        finalCourses.forEach { course ->
            val finalLessons = langeekLessonMigrationService.migrate(course)
            logger.info("Migrated ${finalLessons.size} Langeek lessons for course ${course.name}")

            finalLessons.forEach { lesson ->
                val finalExercises = langeekExerciseMigrationService.migrate(lesson)
                logger.info("Migrated ${finalExercises.size} Langeek exercises for lesson ${lesson.name}")
            }
        }
    }
}
