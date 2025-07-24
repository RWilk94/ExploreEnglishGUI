package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalLesson

@Component
class FinalLessonSqlGenerator : SqlGeneratorAbstract<FinalLesson>() {

    private val logger = LoggerFactory.getLogger(FinalLessonSqlGenerator::class.java)
    private val TAG = "LESSONS"

    override fun generateSql(source: List<FinalLesson>, directoryAlias: String) {
        logger.info(LOG_PREFIX, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `lessons` (`id`, `name`, `description`, `media_image_id`, `course_id`) VALUES ")

            for (lesson in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(lesson.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(lesson.name)) // COLUMN NAME
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(lesson.description)) // COLUMN DESCRIPTION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(lesson.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(lesson.course?.id ?: "NULL") // COURSE_ID
                    .append(getEndLineCharacter(chunk, lesson))
            }
        }
        exportFile(sql, "${TAG.lowercase()}.txt", TAG)
    }
}
