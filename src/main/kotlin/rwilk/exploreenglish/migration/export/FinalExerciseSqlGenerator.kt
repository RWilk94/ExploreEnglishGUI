package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalExercise

@Component
class FinalExerciseSqlGenerator : SqlGeneratorAbstract<FinalExercise>() {

    private val logger = LoggerFactory.getLogger(FinalExerciseSqlGenerator::class.java)
    private val TAG = "EXERCISES"

    override fun generateSql(source: List<FinalExercise>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `exercises` (`id`, `name`, `description`, `media_image_id`, `type`, `lesson_id`) VALUES ")

            for (exercise in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(exercise.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(exercise.name)) // COLUMN NAME
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(exercise.description)) // COLUMN DESCRIPTION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(exercise.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(exercise.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(exercise.lesson?.id ?: "NULL") // LESSON_ID
                    .append(getEndLineCharacter(chunk, exercise))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
