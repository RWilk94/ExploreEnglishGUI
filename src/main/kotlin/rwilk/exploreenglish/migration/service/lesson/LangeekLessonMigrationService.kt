package rwilk.exploreenglish.migration.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.FinalLessonMapper
import rwilk.exploreenglish.migration.repository.FinalLessonRepository
import rwilk.exploreenglish.repository.langeek.LangeekLessonRepository

@Service
open class LangeekLessonMigrationService(
    private val finalLessonRepository: FinalLessonRepository,
    private val langeekLessonRepository: LangeekLessonRepository,
    private val finalLessonMapper: FinalLessonMapper
) : LessonMigrationService {

    @Transactional
    override fun migrate(course: FinalCourse): List<FinalLesson> {
        return langeekLessonRepository.findAllByCourse_Id(course.sourceId).map {
            finalLessonMapper.map(it, course)
        }.also {
            finalLessonRepository.saveAll(it)
        }
    }
}
