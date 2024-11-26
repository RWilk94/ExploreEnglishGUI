package rwilk.exploreenglish.scrapper.etutor;

import java.time.Duration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseScrapper {

  protected static final String AUTO_LOGIN_TOKEN = "03rW8A7qetBrsxbJzOyy94UZ3cuCokvJs0qTunfM";
  protected static final String BASE_URL = "https://www.etutor.pl";
  protected static final String XPATH_CHILDREN = "./child::*";
  private static final String BRITISH_ENGLISH = "/en/";
  private static final String AMERICAN_ENGLISH = "/en-ame/";
  private static final String ITALIAN = "/it/";
  private static final String FRENCH = "/fr/";
  private static final String SPANISH = "/es/";
  private static final String LATIN = "/es-latin-america/";
  private static final String GERMAN = "/de/";
  protected static final List<String> PRIMARY_LANGUAGES = List.of(BRITISH_ENGLISH, ITALIAN, FRENCH, SPANISH, GERMAN);
  protected static final List<String> SECONDARY_LANGUAGES = List.of(AMERICAN_ENGLISH, LATIN);
  private static final List<String> primaryLanguageTitles = List.of(
          "British English",
          "Italiano standard",
          "Français standard",
          "Español ibérico",
          "Germany");
  private static final List<String> secondaryLanguageTitles = List.of(
          "American English",
          "Español América Latina"
  );


  protected BaseScrapper() {
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");
  }

  protected ChromeDriver getDriver() {
    final ChromeOptions options = new ChromeOptions();
    // options.addArguments("headless");
    options.addArguments("--mute-audio");
    options.addArguments("use-fake-ui-for-media-stream");

    return new ChromeDriver(options);
  }

  protected WebDriverWait openDefaultPage(final WebDriver driver) {
    final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    final Cookie cookie = new Cookie("autoLoginToken", AUTO_LOGIN_TOKEN);

    driver.get(BASE_URL);
    driver.manage().addCookie(cookie);
    return wait;
  }

  protected String extractBritishAudioIcon(final WebElement element) {
    return primaryLanguageTitles.stream()
            .map(it -> extractAudioIcon(element, it))
            .filter(StringUtils::isNoneBlank)
            .findFirst()
            .orElse("");
  }

  protected String extractAmericanAudioIcon(final WebElement element) {
    return secondaryLanguageTitles.stream()
            .map(it -> extractAudioIcon(element, it))
            .filter(StringUtils::isNoneBlank)
            .findFirst()
            .orElse("");
  }

  protected void closeCookieBox(final WebDriver driver) {
    if (!driver.findElements(By.id("CybotCookiebotDialog")).isEmpty()) {
      driver.findElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll")).click();
    }
  }

  private String extractAudioIcon(final WebElement element, final String title) {
    return element.findElements(By.className("hasRecording")).stream()
      .map(it -> extractDataAudioIconUrlAttribute(it, title))
      .filter(StringUtils::isNoneEmpty)
      .findFirst()
      .orElse(null);
  }

  private String extractDataAudioIconUrlAttribute(final WebElement element, final String title) {
    if (StringUtils.defaultString(element.getAttribute("title"), "").equals(title)) {
      return BASE_URL + element.findElement(By.className("audioIcon"))
        .getAttribute("data-audio-url");
    }
    return extractDataAudioIconUrlAttributeBackup(element, title);
  }

  private String extractDataAudioIconUrlAttributeBackup(final WebElement element, final String title) {
    if (StringUtils.defaultString(element.getAttribute("oldtitle"), "").equals(title)) {
      return BASE_URL + element.findElement(By.className("audioIcon"))
              .getAttribute("data-audio-url");
    }
    return "";
  }

  protected String extractBritishAudioButton(final WebElement element) {
    return primaryLanguageTitles.stream()
            .map(it -> extractAudioButton(element, it))
            .filter(StringUtils::isNoneBlank)
            .findFirst()
            .orElse("");
  }

  protected String extractAmericanAudioButton(final WebElement element) {
    return secondaryLanguageTitles.stream()
            .map(it -> extractAudioButton(element, it))
            .filter(StringUtils::isNoneBlank)
            .findFirst()
            .orElse("");
  }

  private String extractAudioButton(final WebElement element, final String title) {
    return element.findElements(By.className("hasRecording")).stream()
      .map(it -> extractDataAudioButtonUrlAttribute(it, title))
      .filter(StringUtils::isNoneEmpty)
      .findFirst()
      .orElse(null);
  }

  private String extractDataAudioButtonUrlAttribute(final WebElement element, final String title) {
    if (StringUtils.defaultString(element.getAttribute("title"), "").equals(title)) {
      return BASE_URL + element.findElement(By.className("audioIconButton"))
        .getAttribute("data-audio-url");
    }
    return extractDataAudioButtonUrlAttributeBackup(element, title);
  }

  private String extractDataAudioButtonUrlAttributeBackup(final WebElement element, final String title) {
    if (StringUtils.defaultString(element.getAttribute("oldtitle"), "").equals(title)) {
      return BASE_URL + element.findElement(By.className("audioIconButton"))
              .getAttribute("data-audio-url");
    }
    return "";
  }
}
