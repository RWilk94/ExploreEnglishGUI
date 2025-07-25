package rwilk.exploreenglish.migration.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.Triple
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExerciseAnswer
import rwilk.exploreenglish.migration.entity.FinalExerciseQuestion
import rwilk.exploreenglish.migration.entity.FinalMedia
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType

@Component
class FinalExerciseAnswerMapper(
    private val finalMediaMapper: FinalMediaMapper,
) {
    private val objectMapper: ObjectMapper = ObjectMapper()

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

            ExerciseItemType.SPEAKING,
            null -> mapSpeaking(etutorExerciseItem, finalExerciseQuestion)
        }
    }

    private fun mapMatchingPairs(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {
        return listOf(
            FinalExerciseAnswer(
                type = etutorExerciseItem.type,
                answer = nullIfEmpty(etutorExerciseItem.correctAnswer),
                translation = null,
                isCorrect = true,
                source = SourceEnum.ETUTOR.name,
                sourceId = etutorExerciseItem.id,
                audio = null,
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
                    answer = nullIfEmpty(answer),
                    translation = null,
                    isCorrect = etutorExerciseItem.correctAnswer == answer,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    audio = getFinalAudioMedia(
                        isCorrect = etutorExerciseItem.correctAnswer == answer,
                        etutorExerciseItem = etutorExerciseItem
                    ) ?: getFinalAudioMediaBackup(
                        isCorrect = etutorExerciseItem.correctAnswer == answer,
                        etutorExerciseItem = etutorExerciseItem
                    ),
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
            .filter { it.orEmpty().isNotBlank() }
            .flatMap { raw ->
                raw.removePrefix("[")
                    .removeSuffix("]")
                    .split(",")
                    .map { word -> "[\"$word\"]" }
            }
            .map { answer ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = nullIfEmpty(answer),
                    translation = null,
                    isCorrect = true,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    audio = getFinalAudioMedia(
                        isCorrect = true,
                        etutorExerciseItem = etutorExerciseItem
                    ),
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
            .filter { it.orEmpty().isNotBlank() }
            .map { textToTriple(it) }
            .map { triple ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = nullIfEmpty(triple.left),
                    translation = nullIfEmpty(triple.right),
                    isCorrect = textToTriple(etutorExerciseItem.correctAnswer) == triple,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    audio = getFinalAudioMedia(
                        isCorrect = textToTriple(etutorExerciseItem.correctAnswer) == triple,
                        etutorExerciseItem = etutorExerciseItem
                    ),
                    image = getFinalImageMedia(
                        url = triple.middle.ifBlank { null }
                    ),
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
            .filter { it.orEmpty().isNotBlank() }
            .map { answer ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type,
                    answer = nullIfEmpty(answer),
                    translation = null,
                    isCorrect = true,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    audio = getFinalAudioMedia(
                        isCorrect = true,
                        etutorExerciseItem = etutorExerciseItem
                    ),
                    image = null,
                    video = null,
                    question = finalExerciseQuestion
                )
            }
    }

    private fun mapSpeaking(
        etutorExerciseItem: EtutorExerciseItem,
        finalExerciseQuestion: FinalExerciseQuestion
    ): List<FinalExerciseAnswer> {

        return listOf(
            etutorExerciseItem.firstPossibleAnswer,
            etutorExerciseItem.secondPossibleAnswer,
            etutorExerciseItem.thirdPossibleAnswer,
            etutorExerciseItem.forthPossibleAnswer
        )
            .filter { it.orEmpty().isNotBlank() }
            .flatMap { raw ->
                raw.removePrefix("[")
                    .removeSuffix("]")
                    .split(",")
                    .map { word -> "[\"$word\"]" }
            }
            .map { answer ->
                FinalExerciseAnswer(
                    type = etutorExerciseItem.type ?: ExerciseItemType.SPEAKING.name,
                    answer = null,
                    translation = nullIfEmpty(etutorExerciseItem.translation),
                    isCorrect = true,
                    source = SourceEnum.ETUTOR.name,
                    sourceId = etutorExerciseItem.id,
                    audio = getFinalAudioMedia(
                        isCorrect = true,
                        etutorExerciseItem = etutorExerciseItem
                    ) ?: getFinalAudioMediaBackup(
                        isCorrect = true,
                        etutorExerciseItem = etutorExerciseItem
                    ),
                    image = null,
                    video = null,
                    question = finalExerciseQuestion
                )
            }

    }

    private fun getFinalAudioMedia(isCorrect: Boolean, etutorExerciseItem: EtutorExerciseItem): FinalMedia? {
        return when (isCorrect) {
            true -> finalMediaMapper.mapAudio(etutorExerciseItem.answerPrimarySound, etutorExerciseItem.answerSecondarySound)
            else -> null
        }
    }

    private fun getFinalAudioMediaBackup(isCorrect: Boolean, etutorExerciseItem: EtutorExerciseItem): FinalMedia? {
        return when (isCorrect) {
            true -> finalMediaMapper.mapAudio(etutorExerciseItem.questionPrimarySound, etutorExerciseItem.questionSecondarySound)
            else -> null
        }
    }

    private fun getFinalImageMedia(url: String?): FinalMedia? {
        return if (url.isNullOrBlank()) {
            null
        } else {
            finalMediaMapper.mapImage(url.trim())
        }
    }

    private fun nullIfEmpty(value: String?): String? {
        return if (value.isNullOrBlank()) null else value
    }
}
