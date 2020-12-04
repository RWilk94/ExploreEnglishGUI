package rwilk.exploreenglish.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class WordUtils {

  private final static String REGEX = "[^\\p{IsAlphabetic}\\p{IsDigit} ]";

  private WordUtils() {}

  public static String trim(String term) {

    return StringUtils.trimToEmpty(StringUtils.trimToEmpty(term)
        .replaceAll(Pattern.quote("(British English)"), "")
        .replaceAll(Pattern.quote("(American English)"), "")
        .replaceAll(Pattern.quote("(slang)"), "")
        .replaceAll(Pattern.quote("(informal)"), "")
        .replaceAll(Pattern.quote("(formal)"), "")
        .replaceAll(Pattern.quote("(literary)"), "")
        .replaceAll(Pattern.quote("(technical)"), "")
        .replaceAll(Pattern.quote("(old-fashioned)"), "")
        .replaceAll(Pattern.quote("(old use)"), "")
        .replaceAll(Pattern.quote("(spoken)"), "")
        .replaceAll(Pattern.quote("(written)"), "")
        .replaceAll(Pattern.quote("(not polite)"), "")
        .replaceAll(Pattern.quote("(taboo)"), "")
        .replaceAll(Pattern.quote("(trademark)"), "")
        .replaceAll(Pattern.quote("(dialect)"), "")
        .replaceAll(Pattern.quote("(loan-word)"), "")
        .replaceAll(Pattern.quote("(humorous)"), ""));
  }

  public static String replace(String term, String delimiter) {
    return StringUtils.trimToEmpty(term).replaceAll(" ", delimiter);
  }

  public static String removeNonLiteralCharacters(String term) {
    return StringUtils.trimToEmpty(term).replaceAll(REGEX, "");
  }

  public static String trimAndReplace(String term, String delimiter) {
    return replace(trim(term), delimiter);
  }

  public static String trimAndReplaceAndRemoveNonLiteralCharacters(String term, String delimiter) {
    return replace(removeNonLiteralCharacters(trim(term)), delimiter);
  }

}
