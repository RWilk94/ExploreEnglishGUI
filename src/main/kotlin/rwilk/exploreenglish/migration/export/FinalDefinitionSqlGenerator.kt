package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalDefinition

@Component
class FinalDefinitionSqlGenerator : SqlGeneratorAbstract<FinalDefinition>() {

    private val logger = LoggerFactory.getLogger(FinalDefinitionSqlGenerator::class.java)
    private val TAG = "DEFINITIONS"

    override fun generateSql(source: List<FinalDefinition>, directoryAlias: String) {
        logger.info(LOG_PREFIX, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `definitions` (`id`, `type`, `foreign_translation`, `additional_information`, `media_audio_id`, `media_video_id`, `word_id`) VALUES ")

            for (definition in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(definition.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(definition.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(definition.foreignTranslation)) // COLUMN FOREIGN_TRANSLATION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(definition.additionalInformation)) // COLUMN ADDITIONAL_INFORMATION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(definition.audio?.id ?: "NULL") // MEDIA_AUDIO_ID
                    .append(PARAM_SEPARATOR)
                    .append(definition.video?.id ?: "NULL") // MEDIA_VIDEO_ID
                    .append(PARAM_SEPARATOR)
                    .append(definition.word?.id ?: "NULL") // WORD_ID
                    .append(getEndLineCharacter(chunk, definition))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
