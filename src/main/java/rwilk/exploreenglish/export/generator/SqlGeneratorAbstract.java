package rwilk.exploreenglish.export.generator;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SqlGeneratorAbstract<T> {

  private static final Logger logger = LoggerFactory.getLogger(SqlGeneratorAbstract.class);
  protected static final String LOG_PREFIX = "START GENERATING {}";
  protected static final Integer CHUNK_SIZE = 1;
  protected static final String PARAM_SEPARATOR = ", ";
  protected static final String QUOTE_SIGN = "'";

  public abstract void generateSql(final List<T> source, final String directoryAlias);

  protected String replaceApostrophe(final String text) {
    return StringUtils.trimToEmpty(StringUtils.defaultString(text)).replace("'", "''");
  }

  protected String replaceApostropheWithoutTrim(final String text) {
    return StringUtils.trimToEmpty(StringUtils.defaultString(text).replace("'", "''").replaceAll("\\n", "\\\\n"));
  }

  protected String getEndLineCharacter(final Object chunk, final Object course) {
    if (((List<?>) chunk).indexOf(course) + 1 == ((List<?>) chunk).size()) {
      return ");\n";
    } else {
      return "),";
    }
  }

  protected void exportFile(final StringBuilder sql, final String fileName, final String tag) {
    if (fileName.contains("/")) {
      final File file = new File("scripts/" + fileName.substring(0, fileName.lastIndexOf("/")));
      file.mkdirs();
    }

    try (PrintWriter out = new PrintWriter("scripts/" + fileName)) {
      out.println(sql.toString());
      logger.info("FINISH GENERATING {}", tag);
    } catch (Exception e) {
      logger.error("ERROR WHILE GENERATING {}: ", tag, e);
    }
  }

}
