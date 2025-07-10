package rwilk.exploreenglish.migration.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.FinalLessonMapper
import rwilk.exploreenglish.migration.repository.FinalLessonRepository
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository

@Service
open class EtutorLessonMigrationService(
    private val finalLessonRepository: FinalLessonRepository,
    private val etutorLessonRepository: EtutorLessonRepository,
    private val finalLessonMapper: FinalLessonMapper
) : LessonMigrationService {

    @Transactional
    override fun migrate(course: FinalCourse): List<FinalLesson> {
        return etutorLessonRepository.findAllByCourse_Id(course.sourceId).map {
            finalLessonMapper.map(it, course)
        }.also {
            finalLessonRepository.saveAll(it)
        }
    }
}
