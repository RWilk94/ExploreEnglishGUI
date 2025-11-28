package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalCourse

@Component
class FinalCourseSqlGenerator : SqlGeneratorAbstract<FinalCourse>() {

    private val logger = LoggerFactory.getLogger(FinalCourseSqlGenerator::class.java)
    private val TAG = "COURSES"

    override fun generateSql(source: List<FinalCourse>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `courses` (`id`, `name`, `description`, `media_image_id`, `language`) VALUES ")

            for (course in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(course.id) // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(course.name)) // COLUMN NAME
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(course.description)) // COLUMN DESCRIPTION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(course.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(course.language)) // LANGUAGE
                    .append(QUOTE_SIGN)
                    .append(getEndLineCharacter(chunk, course))
            }
        }
        exportFile(sql, directoryAlias + "/${TAG.lowercase()}.txt", TAG)
    }
}
