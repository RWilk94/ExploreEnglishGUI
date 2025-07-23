package rwilk.exploreenglish.migration.service.course

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.mapper.FinalCourseMapper
import rwilk.exploreenglish.migration.repository.FinalCourseRepository
import rwilk.exploreenglish.repository.ewa.EwaCourseRepository

@Service
open class EwaCourseMigrationService(
    private val finalCourseRepository: FinalCourseRepository,
    private val ewaCourseRepository: EwaCourseRepository,
    private val finalCourseMapper: FinalCourseMapper
) : CourseMigrationService {

    @Transactional
    override fun migrate(): List<FinalCourse> {
        val finalCourseIds = finalCourseRepository.findAll()
            .map { it.sourceId }

        return ewaCourseRepository.findAll()
            .filter { it -> it.id !in finalCourseIds }
            .takeIf { it.isNotEmpty() }!!
//            .take( 1)
            .map { ewaCourse ->
            finalCourseMapper.map(ewaCourse)
        }.also {
            finalCourseRepository.saveAll(it)
        }
    }
}
