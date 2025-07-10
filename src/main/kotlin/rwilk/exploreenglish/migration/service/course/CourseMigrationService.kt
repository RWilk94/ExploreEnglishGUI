package rwilk.exploreenglish.migration.service.course

import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.service.MigrationService

interface CourseMigrationService : MigrationService {
    fun migrate(): List<FinalCourse>
}
