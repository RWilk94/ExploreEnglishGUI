package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalExerciseAnswer

@Component
class FinalExerciseAnswerSqlGenerator : SqlGeneratorAbstract<FinalExerciseAnswer>() {

    private val logger = LoggerFactory.getLogger(FinalExerciseAnswerSqlGenerator::class.java)
    private val TAG = "EXERCISE_ANSWERS"

    override fun generateSql(source: List<FinalExerciseAnswer>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `exercise_answers` (`id`, `type`, `answer`, `translation`, `is_correct`, `media_audio_id`, `media_image_id`, `media_video_id`, `exercise_question_id`) VALUES ")

            for (answer in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(answer.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(answer.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(answer.answer)) // COLUMN ANSWER
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(answer.translation)) // COLUMN TRANSLATION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(if (answer.isCorrect == true) "1" else "0") // COLUMN IS_CORRECT
                    .append(PARAM_SEPARATOR)
                    .append(answer.audio?.id ?: "NULL") // MEDIA_AUDIO_ID
                    .append(PARAM_SEPARATOR)
                    .append(answer.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(answer.video?.id ?: "NULL") // MEDIA_VIDEO_ID
                    .append(PARAM_SEPARATOR)
                    .append(answer.question?.id ?: "NULL") // EXERCISE_QUESTION_ID
                    .append(getEndLineCharacter(chunk, answer))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
