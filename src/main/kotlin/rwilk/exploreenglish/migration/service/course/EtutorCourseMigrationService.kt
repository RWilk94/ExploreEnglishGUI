package rwilk.exploreenglish.migration.service.course

import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.mapper.FinalCourseMapper
import rwilk.exploreenglish.migration.repository.FinalCourseRepository
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository

@Service
class EtutorCourseMigrationService(
    private val finalCourseMapper: FinalCourseMapper,
    private val finalCourseRepository: FinalCourseRepository,
    private val etutorCourseRepository: EtutorCourseRepository
) : CourseMigrationService {

    override fun migrate() {
        etutorCourseRepository.findAll().forEach { etutorCourse ->
            val finalCourse = finalCourseMapper.map(etutorCourse)
            finalCourseRepository.save(finalCourse)
        }
    }
}
