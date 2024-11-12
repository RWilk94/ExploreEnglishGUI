package rwilk.exploreenglish.export.generator;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;

import java.util.List;

@Component
public class EtutorDefinitionSqlGenerator extends SqlGeneratorAbstract<EtutorDefinition> {

    private static final Logger logger = LoggerFactory.getLogger(EtutorDefinitionSqlGenerator.class);
    private static final String TAG = "DEFINITIONS";

    @Override
    public void generateSql(final List<EtutorDefinition> source) {
        logger.info(LOG_PREFIX, TAG);

        final List<List<EtutorDefinition>> chunks = ListUtils.partition(source, CHUNK_SIZE);
        final StringBuilder sql = new StringBuilder();

        for (final List<EtutorDefinition> chunk : chunks) {
            sql.append("INSERT INTO 'definitions' ('id', 'type', 'english_name', 'additional_information', " +
                    "'british_sound', 'american_sound', 'word_id') VALUES ");

            for (final EtutorDefinition definition : chunk) {
                sql.append("\n")
                        .append("(")
                        .append(definition.getId()) // COLUMN_ID
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(definition.getType())) // COLUMN TYPE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(definition.getEnglishName())) // COLUMN ENGLISH_NAME
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(definition.getAdditionalInformation())) // COLUMN ADDITIONAL_INFORMATION
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(definition.getBritishSound())) // COLUMN BRITISH_SOUND
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(definition.getAmericanSound())) // COLUMN AMERICAN_SOUND
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(definition.getWord().getId()) // COLUMN WORD_ID
                        .append(getEndLineCharacter(chunk, definition));
            }
        }
        exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
    }
}