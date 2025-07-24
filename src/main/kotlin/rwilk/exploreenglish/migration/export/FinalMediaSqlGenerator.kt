package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalMedia

@Component
class FinalMediaSqlGenerator : SqlGeneratorAbstract<FinalMedia>() {

    private val logger = LoggerFactory.getLogger(FinalMediaSqlGenerator::class.java)
    private val TAG = "MEDIA"

    override fun generateSql(source: List<FinalMedia>, directoryAlias: String) {
        logger.info(LOG_PREFIX, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `media` (`id`, `text`, `type`) VALUES ")

            for (media in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(media.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(media.text)) // COLUMN TEXT
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(media.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(getEndLineCharacter(chunk, media))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
