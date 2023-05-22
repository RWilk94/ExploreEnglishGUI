package rwilk.exploreenglish.scrapper.etutor.content;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class EtutorNoteScrapper implements CommandLineRunner {

  private static final String BASE_URL = "https://www.etutor.pl";
  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorNoteRepository etutorNoteRepository;

  public EtutorNoteScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                            final EtutorNoteRepository etutorNoteRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorNoteRepository = etutorNoteRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
/*    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.SCREEN.toString(), false)
      .subList(0, 100)
      .forEach(this::webScrapScreenTypeExercise);*/
  }

  /**
   * ExerciseType.SCREEN
   *
   * @param etutorExercise content
   */
  public void webScrapScreenTypeExercise(final EtutorExercise etutorExercise) {
    if (ExerciseType.SCREEN != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");

    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    final Cookie cookie = new Cookie("autoLoginToken", "BKygYBbhlF7YeJDEJu6wr3peLRtKg3UjZsGNTDHQ");

    driver.get(BASE_URL);
    driver.manage().addCookie(cookie);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));

    final WebElement nativeLessonContent = driver.findElement(By.className("nativeLessonContent"));

    final EtutorNote etutorNote = EtutorNote.builder()
      .nativeTitle(nativeLessonContent.findElement(By.tagName("h1")).getText())
      .nativeContent(nativeLessonContent.findElement(By.className("contentBox")).getText())
      .nativeHtml(nativeLessonContent.getAttribute("innerHTML"))
      .exercise(etutorExercise)
      .build();

    if (!driver.findElements(By.className("foreignTranslationButton")).isEmpty()) {
      final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
      driver.findElement(By.className("foreignTranslationButton")).click();
      etutorNote.setForeignTitle(foreignLessonContent.findElement(By.tagName("h1")).getText());
      etutorNote.setForeignContent(foreignLessonContent.findElement(By.className("contentBox")).getText());
      etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));
    }

    etutorNoteRepository.save(etutorNote);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);

    driver.quit();
  }
}
