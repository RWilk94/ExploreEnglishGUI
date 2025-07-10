package rwilk.exploreenglish.migration.service.course

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    override fun migrate() {
        ewaCourseRepository.findAll().forEach { ewaCourse ->
            val finalCourse = finalCourseMapper.map(ewaCourse)
            finalCourseRepository.save(finalCourse)
        }
    }
}
