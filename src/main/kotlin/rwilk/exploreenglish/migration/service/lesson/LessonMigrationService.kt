package rwilk.exploreenglish.migration.service.lesson

import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.service.MigrationService

interface LessonMigrationService : MigrationService {
    fun migrate(course: FinalCourse): List<FinalLesson>
}
