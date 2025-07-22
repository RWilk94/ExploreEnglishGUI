package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalExercise

interface FinalExerciseRepository : JpaRepository<FinalExercise, Long> {
    fun findAllByTypeIn(types: List<String>): List<FinalExercise>
    fun findBySourceId(sourceId: Long): FinalExercise?
}
