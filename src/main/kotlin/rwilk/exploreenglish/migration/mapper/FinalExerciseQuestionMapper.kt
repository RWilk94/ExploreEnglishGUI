package rwilk.exploreenglish.migration.mapper

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalExerciseQuestion
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType

@Component
class FinalExerciseQuestionMapper(
    private val finalMediaMapper: FinalMediaMapper,
) {
    fun map(etutorExerciseItem: EtutorExerciseItem, finalExercise: FinalExercise): FinalExerciseQuestion {
        val audioMedia = finalMediaMapper.mapAudio(
            primarySound = etutorExerciseItem.questionPrimarySound,
            secondarySound = etutorExerciseItem.questionSecondarySound
        )

        val imageMedia = if (etutorExerciseItem.question.orEmpty().trim().startsWith("https://")) {
            finalMediaMapper.mapImage(etutorExerciseItem.question.orEmpty().trim())
        } else if (!etutorExerciseItem.image.isNullOrBlank()) {
            finalMediaMapper.mapImage(etutorExerciseItem.image.orEmpty().trim())
        } else {
            null
        }

        return FinalExerciseQuestion(
            type = etutorExerciseItem.type ?: ExerciseItemType.SPEAKING.name,
            instruction = nullIfEmpty(etutorExerciseItem.instruction),
            question = nullIfEmpty(etutorExerciseItem.question),
            template = nullIfEmpty(etutorExerciseItem.questionTemplate),
            finalAnswer = nullIfEmpty(etutorExerciseItem.finalAnswer),
            finalAnswerDescription = nullIfEmpty(etutorExerciseItem.description),
            finalAnswerTranslation = nullIfEmpty(etutorExerciseItem.translation),
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorExerciseItem.id,
            audio = audioMedia,
            image = imageMedia,
            video = null,
            exercise = finalExercise,
        )
    }

    fun map(node: JsonNode, finalExercise: FinalExercise): FinalExerciseQuestion {
        val finalAnswer = node.path("content").path("answers").path("correct").asText(null)

        val videoMedia = finalMediaMapper.mapVideo(
            url = "https://storage.appewa.com/api/v1/files/${node.path("media")
                .path("encodedVideos")
                .path("medium")
                .path("hevc")
                .asText(null)}"
        )

        val imageMedia = finalMediaMapper.mapImage(
            url = node.path("media")
                .path("video")
                .path("thumbnail")
                .path("s")
                .asText(null)
        )

        val audioMedia = finalMediaMapper.mapAudio(
            primarySound = node.path("content")
                .path("voice")
                .path(finalAnswer)
                .asText(null),
            secondarySound = null
        )

        return FinalExerciseQuestion(
            type = node.path("type").asText(null),
            instruction = null,
            question = node.path("content").path("text").asText(null)
                ?: node.path("content").path("hint").asText(null),
            template = null,
            finalAnswer = finalAnswer,
            finalAnswerDescription = null,
            finalAnswerTranslation = null,
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
            audio = audioMedia,
            image = imageMedia,
            video = videoMedia,
            exercise = finalExercise,
        )
    }

    fun mapEwaDialogType(node: JsonNode, dialogNode: JsonNode, finalExercise: FinalExercise): FinalExerciseQuestion {
        val imageMedia = finalMediaMapper.mapImage(
            url = node.path("media")
                .path("avatars")
                .path("mate")
                .path("s")
                .asText(null)
        )

        return FinalExerciseQuestion(
            type = node.path("type").asText(null),
            instruction = dialogNode.path("answers").path("hint").asText(),
            question = dialogNode.path("answers").path("question").asText(),
            template = null,
            finalAnswer = dialogNode.path("answers").path("correct").asText(),
            finalAnswerDescription = null,
            finalAnswerTranslation = null,
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
            audio = null,
            image = imageMedia,
            video = null,
            exercise = finalExercise,
        )
    }

    fun mapEwaSpeechType(node: JsonNode, finalExercise: FinalExercise): FinalExerciseQuestion {
        return FinalExerciseQuestion(
            type = node.path("type").asText(null),
            instruction = node.path("content").path("hint").asText(null),
            question = node.path("content").path("text").asText(null),
            template = null,
            finalAnswer = node.path("content").path("answers").path("correct").asText(null),
            finalAnswerDescription = null,
            finalAnswerTranslation = null,
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
            audio = null,
            image = null,
            video = null,
            exercise = finalExercise,
        )
    }

    private fun nullIfEmpty(value: String?): String? {
        return if (value.isNullOrBlank()) null else value
    }
}
