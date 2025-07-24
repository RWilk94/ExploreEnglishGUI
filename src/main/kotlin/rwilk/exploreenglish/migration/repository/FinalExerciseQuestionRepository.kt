package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalExerciseQuestion

interface FinalExerciseQuestionRepository : JpaRepository<FinalExerciseQuestion, Long> {
    fun findAllByExercise_Id(exerciseId: Long): List<FinalExerciseQuestion>
}
