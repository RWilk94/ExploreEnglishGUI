package rwilk.exploreenglish.export.generator;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord;

import java.util.List;

@Component
public class EtutorLessonWordSqlGenerator extends SqlGeneratorAbstract<EtutorLessonWord> {

    private static final Logger logger = LoggerFactory.getLogger(EtutorLessonWordSqlGenerator.class);
    private static final String TAG = "LESSON_WORDS";

    @Override
    public void generateSql(final List<EtutorLessonWord> source) {
        logger.info(LOG_PREFIX, TAG);

        final List<List<EtutorLessonWord>> chunks = ListUtils.partition(source, CHUNK_SIZE);
        final StringBuilder sql = new StringBuilder();

        for (final List<EtutorLessonWord> chunk : chunks) {
            sql.append("INSERT INTO 'lesson_words' ('id', 'position', 'exercise_id', 'word_id') VALUES ");

            for (final EtutorLessonWord lessonWord : chunk) {
                sql.append("\n")
                        .append("(")
                        .append(lessonWord.getId()) // COLUMN_ID
                        .append(PARAM_SEPARATOR)
                        .append(lessonWord.getPosition()) // COLUMN POSITION
                        .append(PARAM_SEPARATOR)
                        .append(lessonWord.getExercise().getId()) // COLUMN EXERCISE_ID
                        .append(PARAM_SEPARATOR)
                        .append(lessonWord.getWord().getId()) // COLUMN WORD_ID
                        .append(getEndLineCharacter(chunk, lessonWord));
            }
        }
        exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
    }
}
