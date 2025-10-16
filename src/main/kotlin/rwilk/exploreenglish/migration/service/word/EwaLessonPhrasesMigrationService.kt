package rwilk.exploreenglish.migration.service.word

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.mapper.FinalExerciseWordMapper
import rwilk.exploreenglish.migration.mapper.FinalWordMapper
import rwilk.exploreenglish.migration.repository.FinalExerciseWordRepository
import rwilk.exploreenglish.migration.repository.FinalWordRepository

@Service
class EwaLessonPhrasesMigrationService(
    private val finalWordMapper: FinalWordMapper,
    private val finalExerciseWordMapper: FinalExerciseWordMapper,
    private val finalWordRepository: FinalWordRepository,
    private val finalExerciseWordRepository: FinalExerciseWordRepository
) {

    fun migrate(exercise: FinalExercise, node: JsonNode): FinalExercise {
        migrateWord(
            finalExercise = exercise,
            node = node
        )
        return exercise
    }

    private fun migrateWord(finalExercise: FinalExercise, node: JsonNode) {
        val finalWordToSave = finalWordMapper.mapLessonPhrasesType(node, finalExercise)
        val finalWord = finalWordRepository.save(finalWordToSave)

        finalExerciseWordRepository.save(
            finalExerciseWordMapper.map(
                finalExercise = finalExercise,
                finalWord = finalWord
            )
        )
    }

}
