package rwilk.exploreenglish.export.generator;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;

import java.util.List;

@Component
public class EtutorExerciseItemSqlGenerator extends SqlGeneratorAbstract<EtutorExerciseItem> {

    private static final Logger logger = LoggerFactory.getLogger(EtutorExerciseItemSqlGenerator.class);
    private static final String TAG = "EXERCISE_ITEMS";

    @Override
    public void generateSql(List<EtutorExerciseItem> source) {
        logger.info(LOG_PREFIX, TAG);

        final List<List<EtutorExerciseItem>> chunks = ListUtils.partition(source, CHUNK_SIZE);
        final StringBuilder sql = new StringBuilder();

        for (final List<EtutorExerciseItem> chunk : chunks) {
            sql.append("INSERT INTO 'exercise_items' ('id', 'instruction', 'question', 'question_template', " +
                    "'question_british_sound', 'question_american_sound', 'first_possible_answer', " +
                    "'second_possible_answer', 'third_possible_answer', 'forth_possible_answer', 'correct_answer', " +
                    "'answer_british_sound', 'answer_american_sound', 'final_answer', 'translation', 'description', " +
                    "'type', 'image', 'exercise_id') VALUES ");

            for (final EtutorExerciseItem item : chunk) {
                sql.append("\n")
                        .append("(")
                        .append(item.getId()) // COLUMN_ID
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getInstruction())) // COLUMN INSTRUCTION
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getQuestion())) // COLUMN QUESTION
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getQuestionTemplate())) // COLUMN QUESTION_TEMPLATE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getQuestionPrimarySound())) // COLUMN QUESTION_BRITISH_SOUND
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getQuestionSecondarySound())) // COLUMN QUESTION_AMERICAN_SOUND
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getFirstPossibleAnswer())) // COLUMN FIRST_POSSIBLE_ANSWER
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getSecondPossibleAnswer())) // COLUMN SECOND_POSSIBLE_ANSWER
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getThirdPossibleAnswer())) // COLUMN THIRD_POSSIBLE_ANSWER
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getForthPossibleAnswer())) // COLUMN FORTH_POSSIBLE_ANSWER
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getCorrectAnswer())) // COLUMN CORRECT_ANSWER
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getAnswerPrimarySound())) // COLUMN ANSWER_BRITISH_SOUND
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getAnswerSecondarySound())) // COLUMN ANSWER_AMERICAN_SOUND
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getFinalAnswer())) // COLUMN FINAL_ANSWER
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getTranslation())) // COLUMN TRANSLATION
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getDescription())) // COLUMN DESCRIPTION
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getType())) // COLUMN TYPE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(item.getImage())) // COLUMN IMAGE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(item.getExercise().getId()) // COLUMN EXERCISE_ID
                        .append(getEndLineCharacter(chunk, item));
            }
        }
        exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
    }
}
