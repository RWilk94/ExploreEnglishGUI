package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.service.course.EtutorCourseMigrationService
import rwilk.exploreenglish.migration.service.lesson.EtutorLessonMigrationService

@Service
class EtutorMigrationService(
    private val etutorCourseMigrationService: EtutorCourseMigrationService,
    private val etutorLessonMigrationService: EtutorLessonMigrationService,

) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(EtutorMigrationService::class.java)

    override fun run(vararg args: String?) {
        migrate()
    }

    fun migrate() {
        val finalCourses = etutorCourseMigrationService.migrate()
        logger.info("Migrated ${finalCourses.size} Etutor courses")

        finalCourses.forEach {
            val finalLessons = etutorLessonMigrationService.migrate(it)
            logger.info("Migrated ${finalLessons.size} Etutor lessons for course ${it.name}")
        }
    }
}
