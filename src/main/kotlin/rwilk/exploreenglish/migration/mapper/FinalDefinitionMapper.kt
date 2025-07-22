package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalDefinition
import rwilk.exploreenglish.migration.entity.FinalWord
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition

@Component
class FinalDefinitionMapper(
    private val finalMediaMapper: FinalMediaMapper
) {
    fun map(etutorDefinition: EtutorDefinition, finalWord: FinalWord): FinalDefinition {
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
