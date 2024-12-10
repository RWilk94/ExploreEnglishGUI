package rwilk.exploreenglish.export.generator;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorLesson;

@Component
public class EtutorLessonSqlGenerator extends SqlGeneratorAbstract<EtutorLesson> {

  private static final Logger logger = LoggerFactory.getLogger(EtutorLessonSqlGenerator.class);
  private static final String TAG = "LESSONS";

  @Override
  public void generateSql(final List<EtutorLesson> source, final String directoryAlias) {
    logger.info(LOG_PREFIX, TAG);

    final List<List<EtutorLesson>> chunks = ListUtils.partition(source, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();

    for (final List<EtutorLesson> chunk : chunks) {
      sql.append("INSERT INTO 'lessons' ('id', 'name', 'description', 'image', 'course_id') VALUES ");

      for (final EtutorLesson lesson : chunk) {
        sql.append("\n")
          .append("(")
          .append(lesson.getId()) // COLUMN_ID
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(lesson.getName())) // COLUMN NAME
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(lesson.getDescription())) // COLUMN DESCRIPTION
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN)
          .append(replaceApostrophe(lesson.getImage())) // COLUMN IMAGE
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(lesson.getCourse().getId()) // COLUMN COURSE_ID
          .append(getEndLineCharacter(chunk, lesson));
      }
    }
    exportFile(sql, directoryAlias + "/" + TAG.toLowerCase() + ".txt", TAG);
  }

}
