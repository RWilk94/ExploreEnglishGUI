package rwilk.exploreenglish.export.generator;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem;

import java.util.List;

@Component
public class EtutorDialogItemSqlGenerator extends SqlGeneratorAbstract<EtutorDialogItem> {

    private static final Logger logger = LoggerFactory.getLogger(EtutorDialogItemSqlGenerator.class);
    private static final String TAG = "DIALOG_ITEMS";

    @Override
    public void generateSql(final List<EtutorDialogItem> source) {
        logger.info(LOG_PREFIX, TAG);

        final List<List<EtutorDialogItem>> chunks = ListUtils.partition(source, CHUNK_SIZE);
        final StringBuilder sql = new StringBuilder();

        for (final List<EtutorDialogItem> chunk : chunks) {
            sql.append("INSERT INTO 'dialog_items' ('id', 'type', 'dialog_english', 'dialog_polish', 'face_image', " +
                    "'audio', comic_image, 'sound_seek_second', 'exercise_id') VALUES ");

            for (final EtutorDialogItem dialog : chunk) {
                sql.append("\n")
                        .append("(")
                        .append(dialog.getId()) // COLUMN_ID
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(dialog.getType())) // COLUMN TYPE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(dialog.getDialogEnglish())) // COLUMN DIALOG_ENGLISH
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(dialog.getDialogPolish())) // COLUMN DIALOG_POLISH
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(dialog.getFaceImage())) // COLUMN FACE_IMAGE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(dialog.getAudio())) // COLUMN AUDIO
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(QUOTE_SIGN)
                        .append(replaceApostrophe(dialog.getComicImage())) // COLUMN COMIC_IMAGE
                        .append(QUOTE_SIGN)
                        .append(PARAM_SEPARATOR)
                        .append(dialog.getSoundSeekSecond()) // COLUMN SOUND_SEEK_SECOND
                        .append(PARAM_SEPARATOR)
                        .append(dialog.getExercise().getId()) // COLUMN EXERCISE_ID
                        .append(getEndLineCharacter(chunk, dialog));
            }
        }
        exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
    }
}
