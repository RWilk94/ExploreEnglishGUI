package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalNoteItem

@Component
class FinalNoteItemSqlGenerator : SqlGeneratorAbstract<FinalNoteItem>() {

    private val logger = LoggerFactory.getLogger(FinalNoteItemSqlGenerator::class.java)
    private val TAG = "NOTE_ITEMS"

    override fun generateSql(source: List<FinalNoteItem>, directoryAlias: String) {
        logger.info(LOG_PREFIX, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `note_items` (`id`, `example`, `plain_text`, `primary_style`, `secondary_style`, `additional`, `language_type`, `media_audio_id`, `media_image_id`, `note_id`) VALUES ")

            for (item in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(item.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.example)) // COLUMN EXAMPLE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.plainText)) // COLUMN PLAIN_TEXT
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.primaryStyle)) // COLUMN PRIMARY_STYLE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.secondaryStyle)) // COLUMN SECONDARY_STYLE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.additional)) // COLUMN ADDITIONAL
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.languageType)) // COLUMN LANGUAGE_TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(item.audio?.id ?: "NULL") // MEDIA_AUDIO_ID
                    .append(PARAM_SEPARATOR)
                    .append(item.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(item.note?.id ?: "NULL") // NOTE_ID
                    .append(getEndLineCharacter(chunk, item))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
