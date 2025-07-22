package rwilk.exploreenglish.migration.service.word

import rwilk.exploreenglish.migration.entity.FinalExercise

interface WordMigrationService {
    fun migrate(finalExercise: FinalExercise)
}
