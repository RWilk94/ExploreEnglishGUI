package rwilk.exploreenglish.migration.service.course

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.mapper.FinalCourseMapper
import rwilk.exploreenglish.migration.repository.FinalCourseRepository
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository

@Service
open class EtutorCourseMigrationService(
    private val finalCourseMapper: FinalCourseMapper,
    private val finalCourseRepository: FinalCourseRepository,
    private val etutorCourseRepository: EtutorCourseRepository
) : CourseMigrationService {

    @Transactional
    override fun migrate(): List<FinalCourse> {
        return etutorCourseRepository.findAll()
            .filter { it.language == "ENGLISH" }
            .map { finalCourseMapper.map(it) }
            .also {
                finalCourseRepository.saveAll(it)
            }
    }
}
