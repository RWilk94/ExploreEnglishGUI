package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalWord
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.migration.repository.FinalWordRepository
import rwilk.exploreenglish.model.entity.etutor.EtutorWord
import rwilk.exploreenglish.model.entity.ewa.EwaExerciseItem

@Component
class FinalWordMapper(
    private val finalMediaMapper: FinalMediaMapper,
    private val finalDefinitionMapper: FinalDefinitionMapper,
    private val finalWordRepository: FinalWordRepository
) {
    fun map(etutorWord: EtutorWord): FinalWord {
        return finalWordRepository.findBySourceId(etutorWord.id) ?: FinalWord(
            id = null,
            nativeTranslation = etutorWord.nativeTranslation,
            additionalInformation = etutorWord.additionalInformation,
            partOfSpeech = etutorWord.partOfSpeech,
            article = etutorWord.article,
            grammarType = etutorWord.grammarType,
            image = finalMediaMapper.mapImage(etutorWord.image),
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorWord.id,
        ).also { finalWord ->
            finalWord.definitions = etutorWord.definitions.map { finalDefinitionMapper.map(it, finalWord) }
        }
    }

    fun map(ewaExerciseItem: EwaExerciseItem): FinalWord {
        return FinalWord(
            id = null,
            nativeTranslation = ewaExerciseItem.contentTranslation,
            additionalInformation = ewaExerciseItem.contentDescription,
            partOfSpeech = null,
            article = null,
            grammarType = null,
            image = finalMediaMapper.mapImage(ewaExerciseItem.thumbnailS),
            source = SourceEnum.EWA.name,
            sourceId = ewaExerciseItem.id,
        ).also { finalWord ->
            finalWord.definitions = listOf(finalDefinitionMapper.map(ewaExerciseItem, finalWord))
        }
    }
}
