package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalExerciseQuestion
import rwilk.exploreenglish.migration.entity.FinalMedia
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.migration.repository.FinalMediaContentRepository
import rwilk.exploreenglish.migration.repository.FinalMediaRepository
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem

@Component
class FinalExerciseQuestionMapper(
    private val finalMediaRepository: FinalMediaRepository,
    private val finalMediaContentRepository: FinalMediaContentRepository,
    private val finalMediaMapper: FinalMediaMapper,
) {
    fun map(etutorExerciseItem: EtutorExerciseItem, finalExercise: FinalExercise): FinalExerciseQuestion {
        val finalMedia = createFinalMedia(
            primarySound = etutorExerciseItem.questionPrimarySound,
            secondarySound = etutorExerciseItem.questionSecondarySound
        )

        val existingMedia = when {
            !etutorExerciseItem.questionPrimarySound.isNullOrBlank() -> finalMediaContentRepository.findByUrl(etutorExerciseItem.questionPrimarySound)?.media
            !etutorExerciseItem.questionSecondarySound.isNullOrBlank() -> finalMediaContentRepository.findByUrl(etutorExerciseItem.questionSecondarySound)?.media
            else -> null
        }

        // add new media contents if they are not already present
        // ex some exercise items have only primary sound, some only secondary sound
        if (finalMedia != null && existingMedia != null && finalMedia.mediaContents.size != existingMedia.mediaContents.size) {
            val existingUrls = existingMedia.mediaContents.mapNotNull { it.url }
            finalMedia.mediaContents
                .filter { it.url != null && it.url !in existingUrls }
                .forEach { mediaContent ->
                    existingMedia.mediaContents.add(mediaContent)
                }
        }

        // set relationships for media contents and save the media
        var mediaToSave = existingMedia ?: finalMedia
        mediaToSave?.let { media ->
            media.mediaContents.forEach { mediaContent ->
                mediaContent.media = media
            }
            mediaToSave = finalMediaRepository.save(media)
        }

        return FinalExerciseQuestion(
            type = etutorExerciseItem.type,
            instruction = etutorExerciseItem.instruction,
            question = etutorExerciseItem.question,
            template = etutorExerciseItem.questionTemplate,
            finalAnswer = etutorExerciseItem.finalAnswer,
            finalAnswerDescription = etutorExerciseItem.description,
            finalAnswerTranslation = etutorExerciseItem.translation,
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorExerciseItem.id,
            audio = mediaToSave,
            image = null,
            video = null,
            exercise = finalExercise,
        )
    }

    private fun createFinalMedia(
        primarySound: String?,
        secondarySound: String?
    ): FinalMedia? {
        return finalMediaMapper.map(primarySound, secondarySound)
    }
}
