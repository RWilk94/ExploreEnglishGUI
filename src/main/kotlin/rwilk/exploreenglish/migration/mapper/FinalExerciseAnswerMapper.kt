package rwilk.exploreenglish.migration.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.Triple
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExerciseAnswer
import rwilk.exploreenglish.migration.entity.FinalExerciseQuestion
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType

@Component
class FinalExerciseAnswerMapper(
    private val objectMapper: ObjectMapper = ObjectMapper()
) {
    fun map(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {

        val type: ExerciseItemType? = ExerciseItemType.fromString(etutorExerciseItem.type)
        return when (type) {
            ExerciseItemType.MATCHING_PAIRS -> mapMatchingPairs(etutorExerciseItem, finalExerciseQuestion)
            ExerciseItemType.CHOICE,
            ExerciseItemType.PICTURES_CHOICE -> mapChoice(etutorExerciseItem, finalExerciseQuestion)

            ExerciseItemType.PICTURES_MASKED_WRITING,
            ExerciseItemType.WRITING -> mapWriting(etutorExerciseItem, finalExerciseQuestion)

            ExerciseItemType.PICTURES_LISTENING -> mapPictureListening(etutorExerciseItem, finalExerciseQuestion)
            ExerciseItemType.CLOZE -> mapCloze(etutorExerciseItem, finalExerciseQuestion)
            null -> listOf()
        }
    }

    // TODO try to map media for answers

    private fun mapMatchingPairs(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {
        return listOf(
            FinalExerciseAnswer(
                type = etutorExerciseItem.type,
                answer = etutorExerciseItem.correctAnswer,
                translation = null,
                isCorrect = true,
                source = SourceEnum.ETUTOR.name,
                sourceId = etutorExerciseItem.id,
                sound = null,
                image = null,
                video = null,
                question = finalExerciseQuestion
            )
        )
    }

    private fun mapChoice(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {
        return listOf(
            etutorExerciseItem.firstPossibleAnswer,
            etutorExerciseItem.secondPossibleAnswer,
            etutorExerciseItem.thirdPossibleAnswer,
            etutorExerciseItem.forthPossibleAnswer
        )
            .filter { StringUtils.isNotBlank(it) }
            .map { answer ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = answer,
                    translation = null,
                    isCorrect = etutorExerciseItem.correctAnswer == answer,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    sound = null,
                    image = null,
                    video = null,
                    question = finalExerciseQuestion
                )
            }
    }

    private fun mapWriting(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {
        return listOf(
            etutorExerciseItem.firstPossibleAnswer,
            etutorExerciseItem.secondPossibleAnswer,
            etutorExerciseItem.thirdPossibleAnswer,
            etutorExerciseItem.forthPossibleAnswer
        )
            .filter { it.isNotBlank() }
            .flatMap { raw ->
                raw.removePrefix("[")
                    .removeSuffix("]")
                    .split(",")
                    .map { word -> "[\"$word\"]" }
            }
            .map { answer ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = answer,
                    translation = null,
                    isCorrect = true,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    sound = null,
                    image = null,
                    video = null,
                    question = finalExerciseQuestion
                )
            }
    }

    private fun mapPictureListening(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {
        return listOf(
            etutorExerciseItem.firstPossibleAnswer,
            etutorExerciseItem.secondPossibleAnswer,
            etutorExerciseItem.thirdPossibleAnswer,
            etutorExerciseItem.forthPossibleAnswer
        )
            .filter { it.isNotBlank() }
            .map { textToTriple(it) }
            .map { triple ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = triple.left,
                    translation = triple.right,
                    isCorrect = textToTriple(etutorExerciseItem.correctAnswer) == triple,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    sound = null,
                    image = null,
                    video = null,
                    question = finalExerciseQuestion
                )
            }
    }

    private fun textToTriple(text: String): Triple<String, String, String> {
        return objectMapper.readValue(
            text,
            object : TypeReference<Triple<String, String, String>>() {})
    }

    private fun mapCloze(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {
        return listOf(
            etutorExerciseItem.firstPossibleAnswer,
            etutorExerciseItem.secondPossibleAnswer,
            etutorExerciseItem.thirdPossibleAnswer,
            etutorExerciseItem.forthPossibleAnswer
        )
            .filter { it.isNotBlank() }
            .map { answer ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = answer,
                    translation = null,
                    isCorrect = true,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    sound = null,
                    image = null,
                    video = null,
                    question = finalExerciseQuestion
                )
            }
    }
}
