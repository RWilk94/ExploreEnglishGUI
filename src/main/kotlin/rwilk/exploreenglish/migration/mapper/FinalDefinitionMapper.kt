package rwilk.exploreenglish.migration.mapper

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalDefinition
import rwilk.exploreenglish.migration.entity.FinalWord
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.migration.service.EtutorMigrationService
import rwilk.exploreenglish.model.WordTypeEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition
import rwilk.exploreenglish.model.entity.ewa.EwaExerciseItem

@Component
class FinalDefinitionMapper(
    private val finalMediaMapper: FinalMediaMapper
) {
    private val logger: Logger = LoggerFactory.getLogger(EtutorMigrationService::class.java)
    companion object {
        const val BASE_URL: String = "https://storage.appewa.com/api/v1/files/"
    }

    fun map(etutorDefinition: EtutorDefinition, finalWord: FinalWord): FinalDefinition {
//        logger.info(finalWord.toString())
//        logger.info(etutorDefinition.toString())

        return FinalDefinition(
            id = null,
            type = etutorDefinition.type,
            foreignTranslation = etutorDefinition.foreignTranslation,
            additionalInformation = etutorDefinition.additionalInformation,
            audio = finalMediaMapper.mapAudio(etutorDefinition.primarySound, etutorDefinition.secondarySound),
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorDefinition.id,
            word = finalWord,
        )
    }

    fun map(ewaExerciseItem: EwaExerciseItem, finalWord: FinalWord): FinalDefinition {
        return FinalDefinition(
            id = null,
            type = WordTypeEnum.WORD.toString(),
            foreignTranslation = ewaExerciseItem.contentText,
            additionalInformation = null,
            audio = finalMediaMapper.mapAudio(ewaExerciseItem.voiceUrl, null),
            video = finalMediaMapper.mapVideo(BASE_URL + ewaExerciseItem.videoHevc),
            source = SourceEnum.EWA.name,
            sourceId = ewaExerciseItem.id,
            word = finalWord
        )
    }
}
