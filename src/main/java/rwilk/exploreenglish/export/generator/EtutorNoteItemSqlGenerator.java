package rwilk.exploreenglish.export.generator;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;

@Component
public class EtutorNoteItemSqlGenerator extends SqlGeneratorAbstract<EtutorNoteItem> {

  private static final Logger logger = LoggerFactory.getLogger(EtutorCourseSqlGenerator.class);
  private static final String TAG = "NOTES";

  @Override
  public void generateSql(final List<EtutorNoteItem> source) {
    logger.info(LOG_PREFIX, TAG);

    final List<List<EtutorNoteItem>> chunks = ListUtils.partition(source, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();

    for (final List<EtutorNoteItem> chunk : chunks) {
      sql.append("INSERT INTO 'note_items' ('id', 'american_sound', 'british_sound', 'example', 'plain_text', " +
                 "'image', 'note_id') VALUES ");

      for (final EtutorNoteItem noteItem : chunk) {
        sql.append("\n")
          .append("(")
          .append(noteItem.getId()) // COLUMN_ID
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(noteItem.getAmericanSound())) // COLUMN AMERICAN_SOUND
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(noteItem.getBritishSound())) // COLUMN BRITISH_SOUND
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(noteItem.getExample())) // COLUMN EXAMPLE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(noteItem.getPlainText())) // COLUMN PLAIN_TEXT
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(noteItem.getImage())) // COLUMN IMAGE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(noteItem.getNote().getId()) // COLUMN NOTE_ID
          .append(getEndLineCharacter(chunk, noteItem));
      }
    }
    exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
  }
}
