package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalLesson

interface FinalLessonRepository : JpaRepository<FinalLesson, Long> {
    fun findByCourseId(courseId: Long): List<FinalLesson>
}
