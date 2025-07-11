package rwilk.exploreenglish.migration.service.exercise

import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.service.MigrationService

interface ExerciseMigrationService : MigrationService {
    fun migrate(lesson: FinalLesson): List<FinalExercise>
}
