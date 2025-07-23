package rwilk.exploreenglish.migration.controller

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller
import rwilk.exploreenglish.migration.service.EtutorMigrationService

@Controller
class MigrationController(
    private val etutorMigrationService: EtutorMigrationService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        etutorMigrationService.migrate()
    }
}
