package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.service.course.EwaCourseMigrationService

@Service
class EwaMigrationService(
    private val ewaCourseMigrationService: EwaCourseMigrationService
) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(EwaMigrationService::class.java)

    override fun run(vararg args: String?) {
         migrate()
    }

    override fun migrate() {
        ewaCourseMigrationService.migrate()
    }
}
