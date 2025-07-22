package rwilk.exploreenglish.migration.service.dialog

import rwilk.exploreenglish.migration.entity.FinalExercise

interface DialogMigrationService {
    fun migrate(finalExercise: FinalExercise)
}
