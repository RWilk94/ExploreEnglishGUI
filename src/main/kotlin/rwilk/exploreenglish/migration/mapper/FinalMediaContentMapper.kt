package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalMediaContent
import rwilk.exploreenglish.migration.model.LanguageVariantEnum
import rwilk.exploreenglish.model.LanguageEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem

@Component
class FinalMediaContentMapper {
    fun map(etutorExerciseItem: EtutorExerciseItem): List<FinalMediaContent> {

        val mediaList = mutableListOf<FinalMediaContent>()

        listOfNotNull(etutorExerciseItem.questionPrimarySound, etutorExerciseItem.answerPrimarySound)
            .forEach { sound ->
                mediaList.add(
                    FinalMediaContent(
                        id = null,
                        url = sound,
                        type = LanguageVariantEnum.BRITISH_ENGLISH.name,
                        language = LanguageEnum.ENGLISH.name
                    )
                )
            }
        listOfNotNull(etutorExerciseItem.questionSecondarySound, etutorExerciseItem.answerSecondarySound)
            .forEach { sound ->
                mediaList.add(
                    FinalMediaContent(
                        id = null,
                        url = sound,
                        type = LanguageVariantEnum.AMERICAN_ENGLISH.name,
                        language = LanguageEnum.ENGLISH.name
                    )
                )
            }

        return mediaList
    }
}
