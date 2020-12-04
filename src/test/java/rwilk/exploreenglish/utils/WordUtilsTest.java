package rwilk.exploreenglish.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

}
