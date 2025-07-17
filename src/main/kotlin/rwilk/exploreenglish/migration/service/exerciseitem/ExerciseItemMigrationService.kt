package rwilk.exploreenglish.migration.service.exerciseitem

import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.service.MigrationService

interface ExerciseItemMigrationService : MigrationService {
    fun migrate(exercise: FinalExercise)
}
