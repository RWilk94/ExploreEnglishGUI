package rwilk.exploreenglish.export.generator;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;

@Component
public class EtutorExerciseSqlGenerator extends SqlGeneratorAbstract<EtutorExercise> {

  private static final Logger logger = LoggerFactory.getLogger(EtutorCourseSqlGenerator.class);
  private static final String TAG = "EXERCISES";

  @Override
  public void generateSql(final List<EtutorExercise> source, final String directoryAlias) {
    logger.info(LOG_PREFIX, TAG);

    final List<List<EtutorExercise>> chunks = ListUtils.partition(source, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();

    for (final List<EtutorExercise> chunk : chunks) {
      sql.append("INSERT INTO 'exercises' ('id', 'name', 'type', 'image', 'lesson_id') VALUES ");

      for (final EtutorExercise exercise : chunk) {
        sql.append("\n")
          .append("(")
          .append(exercise.getId()) // COLUMN_ID
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(exercise.getName())) // COLUMN NAME
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(exercise.getType())) // COLUMN TYPE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(exercise.getImage())) // COLUMN IMAGE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(exercise.getLesson().getId()) // COLUMN LESSON_ID
          .append(getEndLineCharacter(chunk, exercise));
      }
    }
    exportFile(sql, directoryAlias + "/" + TAG.toLowerCase() + ".txt", TAG);
  }
}
