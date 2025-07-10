package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class EtutorMigrationService(
    private val etutorCourseMigrationService: rwilk.exploreenglish.migration.service.course.EtutorCourseMigrationService
) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(EtutorMigrationService::class.java)

    override fun run(vararg args: String?) {
        migrate()
    }

    override fun migrate() {
        etutorCourseMigrationService.migrate()
    }
}
