package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalExerciseWord

@Component
class FinalExerciseWordSqlGenerator : SqlGeneratorAbstract<FinalExerciseWord>() {

    private val logger = LoggerFactory.getLogger(FinalExerciseWordSqlGenerator::class.java)
    private val TAG = "EXERCISE_WORDS"

    override fun generateSql(source: List<FinalExerciseWord>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `exercise_words` (`id`, `position`, `word_id`, `exercise_id`) VALUES ")

            for (exerciseWord in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(exerciseWord.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(exerciseWord.position ?: "NULL") // COLUMN POSITION
                    .append(PARAM_SEPARATOR)
                    .append(exerciseWord.word?.id ?: "NULL") // WORD_ID
                    .append(PARAM_SEPARATOR)
                    .append(exerciseWord.exercise?.id ?: "NULL") // EXERCISE_ID
                    .append(getEndLineCharacter(chunk, exerciseWord))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
