package rwilk.exploreenglish.scrapper.etutor.exercise;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
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
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Slf4j
@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Component
public class ExerciseScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorDefinitionRepository etutorDefinitionRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;
  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorLessonRepository etutorLessonRepository;
  private final EtutorNoteRepository etutorNoteRepository;

  public ExerciseScrapper(final EtutorCourseRepository etutorCourseRepository,
                          final EtutorDefinitionRepository etutorDefinitionRepository,
                          final EtutorExerciseItemRepository etutorExerciseItemRepository,
                          final EtutorExerciseRepository etutorExerciseRepository,
                          final EtutorLessonRepository etutorLessonRepository,
                          final EtutorNoteRepository etutorNoteRepository,
                          @Value("${explore-english.autologin-token}") final String autologinToken) {
    super(autologinToken);
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorDefinitionRepository = etutorDefinitionRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorLessonRepository = etutorLessonRepository;
    this.etutorNoteRepository = etutorNoteRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorLessonRepository.findAllByIsReady(false)
//            .stream()
//            .limit(100)
//            .forEach(this::webScrapContent);
  }

  public void webScrapContent(final EtutorLesson etutorLesson) {
    log.info("Scraping exercises for lesson: {}", etutorLesson.getName());
    final WebDriver driver = super.getDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorLesson.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("singleLessonDetails")));
    // close cookie box
    super.closeCookieBox(driver);
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
        .isReady(false)
        .build()
      )
      .toList();

    etutorExerciseRepository.saveAll(etutorExercises);
    etutorLesson.setIsReady(true);
    etutorLessonRepository.save(etutorLesson);
    log.info("Exercises for lesson: {} have been scraped", etutorLesson.getName());
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
