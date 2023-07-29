package rwilk.exploreenglish.scrapper.etutor;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseScrapper {

  protected static final String AUTO_LOGIN_TOKEN = "NVsOV9NtHp7zH2DoOipchNtPvI2ZZIBiBBhi2QnC";
  protected static final String BASE_URL = "https://www.etutor.pl";
  protected static final String XPATH_CHILDREN = "./child::*";

  protected BaseScrapper() {
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");
  }

  protected WebDriverWait openDefaultPage(final WebDriver driver) {
    final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    final Cookie cookie = new Cookie("autoLoginToken", AUTO_LOGIN_TOKEN);

    driver.get(BASE_URL);
    driver.manage().addCookie(cookie);
    return wait;
  }

  protected String extractBritishAudioIcon(final WebElement element) {
    return extractAudioIcon(element, "British English");
  }

  protected String extractAmericanAudioIcon(final WebElement element) {
    return extractAudioIcon(element, "American English");
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
    return "";
  }

  protected String extractBritishAudioButton(final WebElement element) {
    return extractAudioButton(element, "British English");
  }

  protected String extractAmericanAudioButton(final WebElement element) {
    return extractAudioButton(element, "American English");
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
    return "";
  }
}
