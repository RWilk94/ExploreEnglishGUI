package rwilk.exploreenglish.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Word Trimmer Test should")
class WordUtilsTest {

  @Test
  @DisplayName("trim terms")
  void trim() {
    assertEquals("dog", WordUtils.trim("dog (American English)"));
    assertEquals("dog", WordUtils.trim("dog (American English)(British English)"));
    assertEquals("dog", WordUtils.trim(" dog (formal) "));
    assertEquals("dog", WordUtils.trim(" dog (formal)(formal) "));
  }

  @Test
  @DisplayName("replace spaces to pluses")
  void replace() {
    assertEquals("good+morning", WordUtils.replace("good morning", "+"));
    assertEquals("good-morning", WordUtils.replace("good morning", "-"));
    assertEquals("good+morning", WordUtils.replace(" good morning ", "+"));
    assertEquals("good-morning", WordUtils.replace(" good morning ", "-"));
  }

  @Test
  @DisplayName("remove non literal characters")
  void removeNonLiteralCharacters() {
    assertEquals("good morning", WordUtils.removeNonLiteralCharacters("@#$%^&*^()good morning!"));
  }

  @Test
  @DisplayName("trim and replace")
  void trimAndReplace() {
    assertEquals("good+morning", WordUtils.trimAndReplace(" good morning ", "+"));
    assertEquals("good-morning", WordUtils.trimAndReplace(" good morning ", "-"));
  }

  @Test
  @DisplayName("trim and replace and remove non literal characters")
  void trimAndReplaceAndRemoveNonLiteralCharacters() {
    assertEquals("good+morning", WordUtils.trimAndReplaceAndRemoveNonLiteralCharacters(" !@#$%^&*()_good morning ", "+"));
    assertEquals("good-morning", WordUtils.trimAndReplaceAndRemoveNonLiteralCharacters(" !@#$%^&*()_good morning ", "-"));
  }

  @Test
  @DisplayName("extract GrammarTag")
  void extractGrammarTag() {
    assertEquals("COUNTABLE", WordUtils.extractGrammarTag("pies [synonim: psiak][grammarTag: COUNTABLE]"));
    assertEquals("UNCOUNTABLE", WordUtils.extractGrammarTag("pies [grammarTag: UNCOUNTABLE]"));
  }

  @Test
  @DisplayName("extract Synonym")
  void extractSynonym() {
    assertEquals("psiak", WordUtils.extractSynonym("pies [synonim: psiak][grammarTag: COUNTABLE]"));
    assertEquals("psiak", WordUtils.extractSynonym("pies[synonim: psiak]"));

  }

  @Test
  @DisplayName("extract Opposite")
  void extractOpposite() {
    assertEquals("przeciwieństwo", WordUtils.extractOpposite("pies [synonim: psiak][przeciwieństwo: przeciwieństwo][grammarTag: COUNTABLE]"));
    assertEquals("przeciwieństwa", WordUtils.extractOpposite("pies[synonim: psiak][przeciwieństwa: przeciwieństwa]"));
  }

}
