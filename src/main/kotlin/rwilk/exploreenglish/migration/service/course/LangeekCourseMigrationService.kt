package rwilk.exploreenglish.migration.service.course

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.mapper.FinalCourseMapper
import rwilk.exploreenglish.migration.repository.FinalCourseRepository
import rwilk.exploreenglish.repository.langeek.LangeekCourseRepository

@Service
open class LangeekCourseMigrationService(
    private val finalCourseRepository: FinalCourseRepository,
    private val langeekCourseRepository: LangeekCourseRepository,
    private val finalCourseMapper: FinalCourseMapper
) : CourseMigrationService {

    @Transactional
    override fun migrate(): List<FinalCourse> {
        return langeekCourseRepository.findAll().map {
            finalCourseMapper.map(it)
        }.also {
            finalCourseRepository.saveAll(it)
        }
    }
}
