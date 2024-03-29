package rwilk.exploreenglish.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import rwilk.exploreenglish.model.PartOfSpeechEnum;

public class WordUtils {

  public static void main(String[] args) {
    System.out.println(WordUtils.trimAndReplaceAndRemoveNonLiteralCharacters(" !@#$%^&*()_good morning ", "+"));
  }

  private static final String REGEX = "[^\\p{IsAlphabetic}\\p{IsDigit}' ]";

  private WordUtils() {
  }

  public static String replaceSpecialText(final String term) {
    return StringUtils.trimToEmpty(StringUtils.trimToEmpty(term)
                                              .replace(" British English", " (British English)")
                                              .replace(" American English", " (American English)")
                                              .replace(" slang", " (slang)")
                                              .replace(" formal", " (formal)")
                                              .replace(" informal", " (informal)")
                                              .replace(" literary", " (literary)")
                                              .replace(" technical", " (technical)")
                                              .replace(" old-fashioned", " (old-fashioned)")
                                              .replace(" old use", " (old use)")
                                              .replace(" spoken", " (spoken)")
                                              .replace(" written", " (written)")
                                              .replace(" not polite", " (not polite)")
                                              .replace(" taboo", " (taboo)")
                                              .replace(" trademark", " (trademark)")
                                              .replace(" dialect", " (dialect)")
                                              .replace(" loan-word", " (loan-word)")
                                              .replace(" humorous", " (humorous)"));
  }

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

  public static String replace(final String term, final String delimiter) {
    return StringUtils.trimToEmpty(term).replaceAll(" ", delimiter);
  }

  public static String removeNonLiteralCharacters(final String term) {
    final Pattern pattern = Pattern.compile(REGEX);
    return pattern.matcher(term).replaceAll("");
  }

  public static String trimAndReplace(String term, String delimiter) {
    return replace(trim(term), delimiter);
  }

  public static String trimAndReplaceAndRemoveNonLiteralCharacters(String term, String delimiter) {
    return replace(removeNonLiteralCharacters(trim(term)), delimiter);
  }

  public static String extractGrammarTag(String term) {
    String[] strings = term.split(Pattern.quote("["));
    for (String s : strings) {
      if (s.contains("grammarTag")) {
        return s.trim().substring(s.trim().indexOf(":") + 1, s.trim().length() - 1).trim();
      }
    }
    return "";
  }

  public static String extractSynonym(String term) {
    String[] strings = term.split(Pattern.quote("["));
    for (String s : strings) {
      if (s.contains("synonim")) {
        return s.trim().substring(s.trim().indexOf(":") + 1, s.trim().length() - 1).trim();
      }
    }
    return "";
  }

  public static String extractOpposite(String term) {
    String[] strings = term.split(Pattern.quote("["));
    for (String s : strings) {
      if (s.contains("przeciwieństw")) {
        return s.trim().substring(s.trim().indexOf(":") + 1, s.trim().length() - 1).trim();
      }
    }
    return "";
  }

  public static String extractPartOfSpeech(final String partOfSpeech) {
    if (partOfSpeech.equals("rzeczownik") || partOfSpeech.equals("rzecz.") || partOfSpeech.equals("noun")) {
      return PartOfSpeechEnum.RZECZOWNIK.getValue();
    } else if (partOfSpeech.equals("czasownik") || partOfSpeech.contains("czas.") || partOfSpeech.contains("verb")) {
      return PartOfSpeechEnum.CZASOWNIK.getValue();
    } else if (partOfSpeech.equals("przymiotnik") || partOfSpeech.equals("przym.") || partOfSpeech.equals("adjective")) {
      return PartOfSpeechEnum.PRZYMIOTNIK.getValue();
    } else if (partOfSpeech.equals("przysłówek") || partOfSpeech.equals("przysł.") || partOfSpeech.equals("adverb")) {
      return PartOfSpeechEnum.PRZYSLOWEK.getValue();
    } else if (partOfSpeech.contains("phrasal verb")) {
      return PartOfSpeechEnum.PHRASAL_VERB.getValue();
    } else if (partOfSpeech.equals("wyrażenie")) {
      return PartOfSpeechEnum.WYRAZENIE.getValue();
    } else if (partOfSpeech.contains("idiom")) {
      return PartOfSpeechEnum.IDIOM.getValue();
    } else if (partOfSpeech.equals("") || partOfSpeech.equals("determiner") || partOfSpeech.equals("conjunction")
               || partOfSpeech.equals("exclamation") || partOfSpeech.equals("wykrz.") || partOfSpeech.equals("przyimek")
               || partOfSpeech.equals("zaim.") || partOfSpeech.equals("zaimek")) {
      return PartOfSpeechEnum.EMPTY.getValue();
    }
    return "";
  }

}
