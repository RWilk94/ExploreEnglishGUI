package rwilk.exploreenglish.migration.mapper

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalDefinition
import rwilk.exploreenglish.migration.entity.FinalWord
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.WordTypeEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition
import rwilk.exploreenglish.model.entity.ewa.EwaExerciseItem

@Component
class FinalDefinitionMapper(
    private val finalMediaMapper: FinalMediaMapper
) {

    companion object {
        const val BASE_URL: String = "https://storage.appewa.com/api/v1/files/"
    }

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

    fun map(node: JsonNode, finalWord: FinalWord): FinalDefinition {
        return FinalDefinition(
            id = null,
            type = WordTypeEnum.WORD.toString(),
            foreignTranslation = node.path("content").path("text").asText(),
            additionalInformation = null,
            video = finalMediaMapper.mapVideo(
                url = unwrapVideoPath(node)
            ),
            audio = finalMediaMapper.mapAudio(
                primarySound = unwrapAudioPath(node),
                secondarySound = null
            ),
            source = SourceEnum.EWA.name,
            sourceId = finalWord.sourceId,
            word = finalWord
        )
    }

    fun mapExplainType(node: JsonNode, finalWord: FinalWord): List<FinalDefinition> {
        val definitions = mutableListOf<FinalDefinition>()

        val voiceNode = node.path("media").path("voice")
        if (voiceNode.isObject) {
            voiceNode.fields().forEach { (key, value) ->
                definitions.add(
                    FinalDefinition(
                        id = null,
                        type = when (key == node.path("word").path("origin").asText("")) {
                            true -> WordTypeEnum.WORD.name
                            else -> WordTypeEnum.SENTENCE.name
                        },
                        foreignTranslation = key,
                        additionalInformation = when (key != node.path("word").path("origin").asText("")) {
                            true -> node.path("word").path("example").path("translation").asText()
                            else -> null
                        },
                        video = null,
                        audio = finalMediaMapper.mapAudio(
                            primarySound = value.asText(),
                            secondarySound = null
                        ),
                        source = SourceEnum.EWA.name,
                        sourceId = finalWord.sourceId,
                        word = finalWord
                    )
                )
            }
        }

        return definitions
    }

    private fun unwrapAudioPath(node: JsonNode): String? {
        return try {
            node.path("media").path("voice").first().asText()
        } catch (e: Exception) {
            null
        }
    }

    private fun unwrapVideoPath(node: JsonNode): String? {
        return try {
            node.path("media").path("encodedVideos").path("medium").path("hevc").asText()
        } catch (e: Exception) {
            null
        }
    }
}
