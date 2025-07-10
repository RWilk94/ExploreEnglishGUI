package rwilk.exploreenglish.migration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.service.course.LangeekCourseMigrationService

@Service
class LangeekMigrationService(
    private val langeekCourseMigrationService: LangeekCourseMigrationService
) : MigrationService, CommandLineRunner {
    private val logger: Logger = LoggerFactory.getLogger(LangeekMigrationService::class.java)

    override fun run(vararg args: String?) {
         migrate()
    }

    override fun migrate() {
        langeekCourseMigrationService.migrate()
    }

}
