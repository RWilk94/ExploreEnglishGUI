package rwilk.exploreenglish.migration.service.exerciseitem

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.*
import rwilk.exploreenglish.migration.repository.*
import rwilk.exploreenglish.model.entity.ewa.EwaExercise

@Service
class EwaExerciseItemMigrationService(
    private val finalWordMapper: FinalWordMapper,
    private val finalExerciseWordMapper: FinalExerciseWordMapper,
    private val finalExerciseMapper: FinalExerciseMapper,
    private val finalExerciseQuestionMapper: FinalExerciseQuestionMapper,
    private val finalExerciseAnswerMapper: FinalExerciseAnswerMapper,
    private val finalWordRepository: FinalWordRepository,
    private val finalExerciseWordRepository: FinalExerciseWordRepository,
    private val finalExerciseRepository: FinalExerciseRepository,
    private val finalExerciseQuestionRepository: FinalExerciseQuestionRepository,
    private val finalExerciseAnswerRepository: FinalExerciseAnswerRepository,
) : ExerciseItemMigrationService {

    override fun migrate(exercise: FinalExercise) {
    }

    fun migrate(lesson: FinalLesson, ewaExercise: EwaExercise, type: String, nodes: List<JsonNode>): FinalExercise {
        val finalExercise = finalExerciseRepository.save(
            finalExerciseMapper.map(ewaExercise, type, lesson)
        )

        when (type) {
            "explain" -> migrateExplain(
                finalExercise = finalExercise,
                nodes = nodes
            )

            "explainWord" -> migrateExplainWord(
                finalExercise = finalExercise,
                nodes = nodes
            )

            "chooseByVideo", "chooseByImage", "chooseAnswerByText" -> migrateChooseTypes(
                finalExercise = finalExercise,
                nodes = nodes
            )

            "composePhraseByVideo", "composeWord", "composePhraseByText" -> migrateComposeType(
                finalExercise = finalExercise,
                nodes = nodes
            )

            "dialog" -> migrateDialog(finalExercise = finalExercise, nodes = nodes)
            "speechExercise" -> migrateSpeechExercise(finalExercise = finalExercise, nodes = nodes)

            else -> {}
        }

        return finalExercise
    }

    private fun migrateExplain(finalExercise: FinalExercise, nodes: List<JsonNode>) {
        nodes.forEach { node ->
            val finalWordToSave = finalWordMapper.mapExplainType(node, finalExercise)
            val finalWord = finalWordRepository.save(finalWordToSave)

            finalExerciseWordRepository.save(
                finalExerciseWordMapper.map(
                    finalExercise = finalExercise,
                    finalWord = finalWord
                )
            )
        }
    }

    private fun migrateExplainWord(finalExercise: FinalExercise, nodes: List<JsonNode>) {
        nodes.forEach { node ->
            val finalWordToSave = finalWordMapper.mapExplainWordType(node, finalExercise)
            val finalWord = finalWordRepository.save(finalWordToSave)

            finalExerciseWordRepository.save(
                finalExerciseWordMapper.map(
                    finalExercise = finalExercise,
                    finalWord = finalWord
                )
            )
        }
    }

    private fun migrateChooseTypes(finalExercise: FinalExercise, nodes: List<JsonNode>) {
        nodes.forEach { node ->
            val finalQuestion = finalExerciseQuestionMapper.map(node, finalExercise)
            val finalAnswers = finalExerciseAnswerMapper.mapEwaChooseType(node, finalQuestion)

            val savedFinalQuestion = finalExerciseQuestionRepository.save(finalQuestion)
            finalAnswers.forEach {
                it.question = savedFinalQuestion
                finalExerciseAnswerRepository.save(it)
            }
        }
    }

    private fun migrateComposeType(finalExercise: FinalExercise, nodes: List<JsonNode>) {
        nodes.forEach { node ->
            val finalQuestion = finalExerciseQuestionMapper.map(node, finalExercise)
            val finalAnswers = finalExerciseAnswerMapper.mapEwaComposeType(node, finalQuestion)

            val savedFinalQuestion = finalExerciseQuestionRepository.save(finalQuestion)
            finalAnswers.forEach {
                it.question = savedFinalQuestion
                finalExerciseAnswerRepository.save(it)
            }
        }
    }

    private fun migrateDialog(finalExercise: FinalExercise, nodes: List<JsonNode>) {
        nodes.forEach { node ->

            node.path("content").path("messages").forEach { dialogNode ->
                val finalQuestion = finalExerciseQuestionMapper.mapEwaDialogType(
                    node = node,
                    dialogNode = dialogNode,
                    finalExercise = finalExercise
                )

                val finalAnswers = finalExerciseAnswerMapper.mapEwaDialogType(dialogNode, finalQuestion)
                val savedFinalQuestion = finalExerciseQuestionRepository.save(finalQuestion)
                finalAnswers.forEach {
                    it.question = savedFinalQuestion
                    finalExerciseAnswerRepository.save(it)
                }
            }
        }
    }

    private fun migrateSpeechExercise(finalExercise: FinalExercise, nodes: List<JsonNode>) {
        nodes.forEach { node ->
            val finalQuestion = finalExerciseQuestionMapper.mapEwaSpeechType(
                node = node,
                finalExercise = finalExercise
            )

            finalExerciseQuestionRepository.save(finalQuestion)
        }
    }
}
