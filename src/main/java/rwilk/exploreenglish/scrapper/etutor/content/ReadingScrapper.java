package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.Choice;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@java.lang.SuppressWarnings({"java:S1192", "java:S3776"})
@Component
public class ReadingScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;
  private final EtutorNoteRepository etutorNoteRepository;

  public ReadingScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                         final EtutorExerciseItemRepository etutorExerciseItemRepository,
                         final EtutorNoteRepository etutorNoteRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    this.etutorNoteRepository = etutorNoteRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.READING.toString(), false)
//      .subList(0, 4)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
    if (ExerciseType.READING != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for loading
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
    // close cookie box
    super.closeCookieBox(driver);

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();

    final WebElement element = driver.findElement(By.className("exercise"));
    final EtutorNote etutorNote = EtutorNote.builder()
      .id(null)
      .nativeContent(extractNativeText(element))
      .foreignContent(extractForeignText(element))
      .nativeHtml(extractNativeHtml(element))
      .foreignHtml(extractForeignHTML(element))
      .audio(extractAudio(driver))
      .exercise(etutorExercise)
      .build();

    if (!driver.findElements(By.id("switchToExerciseTestButton")).isEmpty()) {
      final WebElement switchToExerciseTestButton = driver.findElement(By.id("switchToExerciseTestButton"));
      switchToExerciseTestButton.click();

      final WebElement exercise = driver.findElement(By.id("readingExerciseTest"))
        .findElement(By.className("exercise"));
      final String instruction = exercise.findElement(By.className("exerciseinstruction")).getText();

      for (final WebElement ex : exercise.findElements(By.className("exercise-numbered-question"))) {
        final EtutorExerciseItem etutorExerciseItem = Choice.webScrap(etutorExercise, ex, instruction);
        exerciseItems.add(etutorExerciseItem);

        // find and click correct answer
        for (final WebElement answer : ex.findElements(By.className("examChoiceOptionBox"))) {
          if (answer.findElement(By.tagName("input")).getAttribute("value")
            .equals(etutorExerciseItem.getCorrectAnswer())) {
            answer.click();
            // and get to next question
            final WebElement nextQuestionButton = driver.findElement(By.id("nextQuestionButton"));
            if (nextQuestionButton != null && nextQuestionButton.isDisplayed() && !nextQuestionButton.getAttribute("class")
              .contains("hidden")) {
              nextQuestionButton.click();
            }
            break;
          }
        }
      }
    }

    etutorNoteRepository.save(etutorNote);
    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
  }

  private String extractNativeText(final WebElement element) {
    return element.findElement(By.className("readingExerciseNativeText")).getText();
  }

  private String extractForeignText(final WebElement element) {
    return element.findElement(By.className("readingExerciseForeignText")).getText();
  }

  private String extractNativeHtml(final WebElement element) {
    return element.findElement(By.className("readingExerciseNativeText")).getAttribute("innerHTML");
  }

  private String extractForeignHTML(final WebElement element) {
    return element.findElement(By.className("readingExerciseForeignText")).getAttribute("innerHTML");
  }

  private String extractAudio(final WebDriver driver) {
    return driver.findElement(By.id("contentWrapper")).findElements(By.tagName("script"))
      .stream()
      .filter(element -> element.getAttribute("innerHTML").contains(".mp3"))
      .findFirst()
      .map(element -> element.getAttribute("innerHTML"))
      .map(text -> text.substring(text.indexOf("(\"") + 2, text.indexOf("\")")))
      .map(url -> BASE_URL + url)
      .orElse(null);
  }
}
