package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalWord

@Component
class FinalWordSqlGenerator : SqlGeneratorAbstract<FinalWord>() {

    private val logger = LoggerFactory.getLogger(FinalWordSqlGenerator::class.java)
    private val TAG = "WORDS"

    override fun generateSql(source: List<FinalWord>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `words` (`id`, `native_translation`, `additional_information`, `part_of_speech`, `article`, `grammar_type`, `media_image_id`) VALUES ")

            for (word in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(word.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(word.nativeTranslation)) // COLUMN NATIVE_TRANSLATION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(word.additionalInformation)) // COLUMN ADDITIONAL_INFORMATION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(word.partOfSpeech)) // COLUMN PART_OF_SPEECH
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(word.article)) // COLUMN ARTICLE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(word.grammarType)) // COLUMN GRAMMAR_TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(word.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(getEndLineCharacter(chunk, word))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
