package rwilk.exploreenglish.migration.controller

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller
import rwilk.exploreenglish.migration.service.EtutorMigrationService
import rwilk.exploreenglish.migration.service.EwaMigrationService

@Controller
class MigrationController(
    private val etutorMigrationService: EtutorMigrationService,
    private val ewaMigrationService: EwaMigrationService,
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // migrate()
    }

    fun migrate() {
        // etutorMigrationService.migrate()
        ewaMigrationService.migrate()
    }
}
