package rwilk.exploreenglish.migration.service.note

import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.service.MigrationService

interface NoteMigrationService : MigrationService {
    fun migrate(exercise: FinalExercise)
}
