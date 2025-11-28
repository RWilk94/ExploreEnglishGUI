package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalMediaContent

@Component
class FinalMediaContentSqlGenerator : SqlGeneratorAbstract<FinalMediaContent>() {

    private val logger = LoggerFactory.getLogger(FinalMediaContentSqlGenerator::class.java)
    private val TAG = "MEDIA_CONTENT"

    override fun generateSql(source: List<FinalMediaContent>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `media_content` (`id`, `url`, `type`, `language`, `media_id`) VALUES ")

            for (mediaContent in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(mediaContent.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(mediaContent.url)) // COLUMN URL
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(mediaContent.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(mediaContent.language)) // COLUMN LANGUAGE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(mediaContent.media?.id ?: "NULL") // MEDIA_ID
                    .append(getEndLineCharacter(chunk, mediaContent))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
