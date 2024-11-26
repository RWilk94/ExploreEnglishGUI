package rwilk.exploreenglish.export.generator;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorWord;

import java.util.List;

@Component
public class EtutorWordSqlGenerator extends SqlGeneratorAbstract<EtutorWord> {

    private static final Logger logger = LoggerFactory.getLogger(EtutorWordSqlGenerator.class);
    private static final String TAG = "WORDS";

    @Override
    public void generateSql(final List<EtutorWord> source) {
        logger.info(LOG_PREFIX, TAG);

        final List<List<EtutorWord>> chunks = ListUtils.partition(source, CHUNK_SIZE);
        final StringBuilder sql = new StringBuilder();

        for (final List<EtutorWord> chunk : chunks) {
            sql.append("INSERT INTO 'words' ('id', 'polish_name', 'additional_information', 'part_of_speech', 'article', " +
                    "'grammar_type', 'image', 'exercise_id') VALUES");

            for (final EtutorWord word : chunk) {
                sql.append("\n")
                        .append("(")
                        .append(word.getId()) // COLUMN_ID
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(word.getNativeTranslation())) // COLUMN POLISH_NAME
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(word.getAdditionalInformation())) // COLUMN ADDITIONAL_INFORMATION
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(word.getPartOfSpeech())) // COLUMN PART_OF_SPEECH
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(word.getArticle())) // COLUMN ARTICLE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(word.getGrammarType())) // COLUMN GRAMMAR_TYPE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(word.getImage())) // COLUMN IMAGE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(word.getExercise().getId()) // COLUMN EXERCISE_ID
                        .append(getEndLineCharacter(chunk, word));
            }
        }
        exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
    }
}
