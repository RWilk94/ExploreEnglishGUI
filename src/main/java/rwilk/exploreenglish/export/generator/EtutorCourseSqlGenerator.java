package rwilk.exploreenglish.export.generator;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;

@Component
public class EtutorCourseSqlGenerator extends SqlGeneratorAbstract<EtutorCourse> {

  private static final Logger logger = LoggerFactory.getLogger(EtutorCourseSqlGenerator.class);
  private static final String TAG = "COURSES";


  @Override
  public void generateSql(final List<EtutorCourse> source) {
    logger.info(LOG_PREFIX, TAG);

    final List<List<EtutorCourse>> chunks = ListUtils.partition(source, CHUNK_SIZE);
    final StringBuilder sql = new StringBuilder();

    for (final List<EtutorCourse> chunk : chunks) {
      sql.append("INSERT INTO 'courses' ('id', 'name', 'description', 'image', 'language') VALUES ");

      for (final EtutorCourse course : chunk) {
        sql.append("\n")
          .append("(")
          .append(course.getId()) // COLUMN_ID
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN) // COLUMN NAME
          .append(replaceApostrophe(course.getName()))
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN) // COLUMN DESCRIPTION
          .append(replaceApostrophe(course.getDescription()))
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN) // COLUMN IMAGE
          .append(replaceApostrophe(course.getImage()))
          .append(QUOTE_SIGN)
          .append(PARAM_SEPARATOR)
          .append(QUOTE_SIGN) // COLUMN LANGUAGE
          .append(replaceApostrophe(course.getLanguage()))
          .append(QUOTE_SIGN);
        insertEndLineCharacter(sql, chunk, course);
      }
    }
    exportFile(sql, TAG.toLowerCase() + ".txt", TAG);
  }

}
