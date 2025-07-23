package rwilk.exploreenglish.migration.mapper

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalDefinition
import rwilk.exploreenglish.migration.entity.FinalWord
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.migration.service.EtutorMigrationService
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition

@Component
class FinalDefinitionMapper(
    private val finalMediaMapper: FinalMediaMapper
) {
    private val logger: Logger = LoggerFactory.getLogger(EtutorMigrationService::class.java)

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
}
