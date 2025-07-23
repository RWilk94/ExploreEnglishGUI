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
        val finalCourseIds = finalCourseRepository.findAll()
            .map { it.sourceId }

        return etutorCourseRepository.findAll()
            .filter { it.language == "ENGLISH" }
            .filter { it -> it.id !in finalCourseIds }
            .takeIf { it.isNotEmpty() }!!
            .subList(0, 1)
            .map { finalCourseMapper.map(it) }
            .also {
                finalCourseRepository.saveAll(it)
            }
    }
}
