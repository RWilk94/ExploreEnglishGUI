package rwilk.exploreenglish.migration.mapper

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExercise
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

    fun mapExplainType(node: JsonNode, finalExercise: FinalExercise): FinalWord {
        return FinalWord(
            id = null,
            nativeTranslation = node.path("content").path("translation").asText(),
            additionalInformation = node.path("content").path("description").asText(),
            partOfSpeech = null,
            article = null,
            grammarType = null,
            image = finalMediaMapper.mapImage(node.path("media").path("video").path("thumbnail").path("s").asText(null)),
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
        ).also { finalWord ->
            finalWord.definitions = listOf(finalDefinitionMapper.map(node, finalWord))
        }
    }

    fun mapExplainWordType(node: JsonNode, finalExercise: FinalExercise): FinalWord {
        return FinalWord(
            id = null,
            nativeTranslation = node.path("word").path("meanings").map { it.asText() }.distinct().joinToString(", "),
            additionalInformation = null,
            partOfSpeech = null,
            article = null,
            grammarType = null,
            image = null,
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
        ).also { finalWord ->
            finalWord.definitions = finalDefinitionMapper.mapExplainType(node, finalWord)
        }
    }

    fun mapLessonWordsType(node: JsonNode, finalExercise: FinalExercise): FinalWord {
        return FinalWord(
            id = null,
            nativeTranslation = node.path("localizedMeanings").map { it.asText() }.distinct().joinToString(", "),
            additionalInformation = null,
            partOfSpeech = null,
            article = null,
            grammarType = null,
            image = null,
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
        ).also { finalWord ->
            finalWord.definitions = finalDefinitionMapper.mapLessonWordsType(node, finalWord)
        }
    }

    fun mapLessonPhrasesType(node: JsonNode, finalExercise: FinalExercise): FinalWord {
        return FinalWord(
            id = null,
            nativeTranslation = node.path("localizedTranslation").asText(),
            additionalInformation = null,
            partOfSpeech = null,
            article = null,
            grammarType = null,
            image = null,
            source = SourceEnum.EWA.name,
            sourceId = finalExercise.sourceId,
        ).also { finalWord ->
            finalWord.definitions = finalDefinitionMapper.mapLessonWordsType(node, finalWord)
        }
    }
}
