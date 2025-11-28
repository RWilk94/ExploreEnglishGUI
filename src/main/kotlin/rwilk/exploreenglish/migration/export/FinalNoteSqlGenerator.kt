package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalNote

@Component
class FinalNoteSqlGenerator : SqlGeneratorAbstract<FinalNote>() {

    private val logger = LoggerFactory.getLogger(FinalNoteSqlGenerator::class.java)
    private val TAG = "NOTES"

    override fun generateSql(source: List<FinalNote>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `notes` (`id`, `native_title`, `native_content`, `foreign_title`, `foreign_content`, `media_audio_id`, `exercise_id`) VALUES ")

            for (note in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(note.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(note.nativeTitle)) // COLUMN NATIVE_TITLE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(note.nativeContent)) // COLUMN NATIVE_CONTENT
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(note.foreignTitle)) // COLUMN FOREIGN_TITLE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(note.foreignContent)) // COLUMN FOREIGN_CONTENT
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(note.audio?.id ?: "NULL") // MEDIA_AUDIO_ID
                    .append(PARAM_SEPARATOR)
                    .append(note.exercise?.id ?: "NULL") // EXERCISE_ID
                    .append(getEndLineCharacter(chunk, note))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
