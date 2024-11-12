package rwilk.exploreenglish.scrapper.etutor;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseScrapper {

  protected static final String AUTO_LOGIN_TOKEN = "qubBSbpjOngfV5F7u2aKTM8LSgPKeLGHJLQsuEV2";
  protected static final String BASE_URL = "https://www.etutor.pl";
  protected static final String XPATH_CHILDREN = "./child::*";

  protected BaseScrapper() {
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");
  }

  protected ChromeDriver getDriver() {
    final ChromeOptions options = new ChromeOptions();
    options.addArguments("headless");
    options.addArguments("--mute-audio");

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
    return extractAudioIcon(element, "British English");
  }

  protected String extractAmericanAudioIcon(final WebElement element) {
    return extractAudioIcon(element, "American English");
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
