package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.service.course.EwaCourseMigrationService
import rwilk.exploreenglish.migration.service.lesson.EwaLessonMigrationService

@Service
class EwaMigrationService(
    private val ewaCourseMigrationService: EwaCourseMigrationService,
    private val ewaLessonMigrationService: EwaLessonMigrationService
) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(EwaMigrationService::class.java)

    override fun run(vararg args: String?) {
         migrate()
    }

    fun migrate() {
        val finalCourses = ewaCourseMigrationService.migrate()
        logger.info("Migrated ${finalCourses.size} Ewa courses")

        finalCourses.forEach {
            val finalLessons = ewaLessonMigrationService.migrate(it)
            logger.info("Migrated ${finalLessons.size} Ewa lessons for course ${it.name}")
        }
    }
}
