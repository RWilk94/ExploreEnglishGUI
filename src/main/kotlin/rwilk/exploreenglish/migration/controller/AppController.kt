package rwilk.exploreenglish.migration.controller

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

@Controller
class AppController(
    private val exportController: ExportController,
    private val migrationController: MigrationController,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        migrationController.migrate()
        exportController.export()
    }

}
