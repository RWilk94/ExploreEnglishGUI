package rwilk.exploreenglish.scrapper.etutor;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.exception.MissingEtutorExerciseTypeException;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorLesson;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorDefinitionRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.repository.etutor.EtutorWordRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class EtutorExerciseScrapper implements CommandLineRunner {

  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorDefinitionRepository etutorDefinitionRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;
  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorLessonRepository etutorLessonRepository;
  private final EtutorNoteRepository etutorNoteRepository;
  private final EtutorWordRepository etutorWordRepository;

  public EtutorExerciseScrapper(final EtutorCourseRepository etutorCourseRepository,
                                final EtutorDefinitionRepository etutorDefinitionRepository,
                                final EtutorExerciseItemRepository etutorExerciseItemRepository,
                                final EtutorExerciseRepository etutorExerciseRepository,
                                final EtutorLessonRepository etutorLessonRepository,
                                final EtutorNoteRepository etutorNoteRepository,
                                final EtutorWordRepository etutorWordRepository) {
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorDefinitionRepository = etutorDefinitionRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorLessonRepository = etutorLessonRepository;
    this.etutorNoteRepository = etutorNoteRepository;
    this.etutorWordRepository = etutorWordRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    etutorLessonRepository.findAllByIsReady(false)
      .forEach(this::webScrapContent);
  }

  public void webScrapContent(final EtutorLesson etutorLesson) {
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");

    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    final Cookie cookie = new Cookie("autoLoginToken", "BKygYBbhlF7YeJDEJu6wr3peLRtKg3UjZsGNTDHQ");

    driver.get("https://www.etutor.pl");
    driver.manage().addCookie(cookie);

    // open course
    driver.get(etutorLesson.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("singleLessonDetails")));
    // extract all exercises
    final List<WebElement> exerciseElements = driver.findElement(By.className("lessonPagesList"))
      .findElements(By.tagName("li"));
    // and iterate
    final List<EtutorExercise> etutorExercises = exerciseElements.stream()
      .map(exerciseElement -> EtutorExercise.builder()
        .name(exerciseElement.findElement(By.className("lessonPageTitle")).getText())
        .href(exerciseElement.findElement(By.tagName("a")).getAttribute("href"))
        .image(exerciseElement.findElement(By.tagName("img")).getAttribute("src"))
        .type(getType(exerciseElement.findElement(By.tagName("img")).getAttribute("src")))
        .lesson(etutorLesson)
        .build()
      )
      .toList();

    etutorExerciseRepository.saveAll(etutorExercises);
    etutorLesson.setIsReady(true);
    etutorLessonRepository.save(etutorLesson);
    driver.quit();
  }

  private String getType(final String image) {
    final String imageType = image.substring(image.lastIndexOf("/") + 1, image.lastIndexOf("."));
    final ExerciseType exerciseType = ExerciseType.fromString(imageType);

    if (exerciseType == null) {
      throw new MissingEtutorExerciseTypeException(imageType);
    }
    return exerciseType.toString();
  }
}
