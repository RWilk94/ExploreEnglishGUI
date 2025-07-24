package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalExerciseWord

interface FinalExerciseWordRepository : JpaRepository<FinalExerciseWord, Long> {
    fun findAllByExercise_Id(exerciseId: Long): List<FinalExerciseWord>
}
