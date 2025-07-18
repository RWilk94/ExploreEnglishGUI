package rwilk.exploreenglish.migration.mapper

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

    private fun nullIfEmpty(value: String?): String? {
        return if (value.isNullOrBlank()) null else value
    }
}
