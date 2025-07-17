package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalMedia
import rwilk.exploreenglish.migration.entity.FinalMediaContent
import rwilk.exploreenglish.migration.model.LanguageVariantEnum
import rwilk.exploreenglish.migration.model.MediaTypeEnum
import rwilk.exploreenglish.model.LanguageEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem

@Component
class FinalMediaMapper {
    fun map(etutorExerciseItem: EtutorExerciseItem): List<FinalMedia> {
        val mediaList = mutableListOf<FinalMedia>()

        listOfNotNull(etutorExerciseItem.questionPrimarySound, etutorExerciseItem.answerPrimarySound)
            .forEach { sound ->
                mediaList.add(
                    FinalMedia(
                        id = null,
                        text = null,
                        type = LanguageVariantEnum.BRITISH_ENGLISH.name,
                    )
                )
            }
        listOfNotNull(etutorExerciseItem.questionSecondarySound, etutorExerciseItem.answerSecondarySound)
            .forEach { sound ->
                mediaList.add(
                    FinalMedia(
                        id = null,
                        text = null,
                        type = LanguageVariantEnum.AMERICAN_ENGLISH.name,
                    )
                )
            }

        return mediaList
    }

    fun map(primarySound: String?, secondarySound: String?): FinalMedia? {
        if (primarySound.isNullOrBlank() && secondarySound.isNullOrBlank()) {
            return null
        }

        val finalMediaContentList = mutableListOf<FinalMediaContent>()

        primarySound?.let {
            finalMediaContentList.add(
                FinalMediaContent(
                    id = null,
                    url = it,
                    type = LanguageVariantEnum.BRITISH_ENGLISH.name,
                    language = LanguageEnum.ENGLISH.name
                )
            )
        }

        secondarySound?.let {
            finalMediaContentList.add(
                FinalMediaContent(
                    id = null,
                    url = it,
                    type = LanguageVariantEnum.AMERICAN_ENGLISH.name,
                    language = LanguageEnum.ENGLISH.name
                )
            )
        }

        return FinalMedia(
            id = null,
            text = null,
            type = MediaTypeEnum.AUDIO.name,
            mediaContents = finalMediaContentList
        )
    }
}
