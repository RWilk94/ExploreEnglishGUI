package rwilk.exploreenglish.export.generator;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorNote;

@Component
public class EtutorNoteSqlGenerator extends SqlGeneratorAbstract<EtutorNote> {

  private static final Logger logger = LoggerFactory.getLogger(EtutorCourseSqlGenerator.class);
  private static final String TAG = "NOTES";

  @Override
  public void generateSql(final List<EtutorNote> source) {
    logger.info(LOG_PREFIX, TAG);

    final List<List<EtutorNote>> chunks = ListUtils.partition(source, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();

    for (final List<EtutorNote> chunk : chunks) {
      sql.append("INSERT INTO 'notes' ('id', 'foreign_content', 'foreign_html', 'native_content', 'native_html', " +
                 "'foreign_title', 'native_title', 'audio', 'exercise_id') VALUES ");

      for (final EtutorNote note : chunk) {
        sql.append("\n")
          .append("(")
          .append(note.getId()) // COLUMN_ID
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getForeignContent())) // COLUMN FOREIGN_CONTENT
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getForeignHtml())) // COLUMN FOREIGN_HTML
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getNativeContent())) // COLUMN NATIVE_CONTENT
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getNativeHtml())) // COLUMN NATIVE_HTML
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getForeignTitle())) // COLUMN FOREIGN_TITLE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getNativeTitle())) // COLUMN NATIVE_TITLE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(note.getAudio())) // COLUMN AUDIO
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(note.getExercise().getId()) // COLUMN EXERCISE_ID
          .append(getEndLineCharacter(chunk, note));
      }
    }
    exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
  }
}
