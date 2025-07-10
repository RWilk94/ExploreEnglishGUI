package rwilk.exploreenglish.migration.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.FinalLessonMapper
import rwilk.exploreenglish.migration.repository.FinalLessonRepository
import rwilk.exploreenglish.repository.ewa.EwaLessonRepository

@Service
open class EwaLessonMigrationService(
    private val finalLessonRepository: FinalLessonRepository,
    private val ewaLessonRepository: EwaLessonRepository,
    private val finalLessonMapper: FinalLessonMapper
) : LessonMigrationService {

    @Transactional
    override fun migrate(course: FinalCourse): List<FinalLesson> {
        return ewaLessonRepository.findAllByCourse_Id(course.sourceId).map {
            finalLessonMapper.map(it, course)
        }.also {
            finalLessonRepository.saveAll(it)
        }
    }
}
