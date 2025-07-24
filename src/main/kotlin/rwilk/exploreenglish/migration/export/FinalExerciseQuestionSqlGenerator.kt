package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalExerciseQuestion

@Component
class FinalExerciseQuestionSqlGenerator : SqlGeneratorAbstract<FinalExerciseQuestion>() {

    private val logger = LoggerFactory.getLogger(FinalExerciseQuestionSqlGenerator::class.java)
    private val TAG = "EXERCISE_QUESTIONS"

    override fun generateSql(source: List<FinalExerciseQuestion>, directoryAlias: String) {
        logger.info(LOG_PREFIX, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `exercise_questions` (`id`, `type`, `instruction`, `question`, `template`, `final_answer`, `final_answer_description`, `final_answer_translation`, `media_audio_id`, `media_image_id`, `media_video_id`, `exercise_id`) VALUES ")

            for (question in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(question.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.instruction)) // COLUMN INSTRUCTION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.question)) // COLUMN QUESTION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.template)) // COLUMN TEMPLATE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.finalAnswer)) // COLUMN FINAL_ANSWER
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.finalAnswerDescription)) // COLUMN FINAL_ANSWER_DESCRIPTION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(question.finalAnswerTranslation)) // COLUMN FINAL_ANSWER_TRANSLATION
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(question.audio?.id ?: "NULL") // MEDIA_AUDIO_ID
                    .append(PARAM_SEPARATOR)
                    .append(question.image?.id ?: "NULL") // MEDIA_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(question.video?.id ?: "NULL") // MEDIA_VIDEO_ID
                    .append(PARAM_SEPARATOR)
                    .append(question.exercise?.id ?: "NULL") // EXERCISE_ID
                    .append(getEndLineCharacter(chunk, question))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
