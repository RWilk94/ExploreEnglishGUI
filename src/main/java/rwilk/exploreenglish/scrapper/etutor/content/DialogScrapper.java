package rwilk.exploreenglish.scrapper.etutor.content;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.repository.etutor.EtutorDialogRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.Choice;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@java.lang.SuppressWarnings({"java:S1192", "java:S3776"})
@Component
public class DialogScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;
  private final EtutorDialogRepository etutorDialogRepository;

  public DialogScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                        final EtutorExerciseItemRepository etutorExerciseItemRepository,
                        final EtutorDialogRepository etutorDialogRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    this.etutorDialogRepository = etutorDialogRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.DIALOGUE.toString(), false)
//      .subList(0, 4)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise) {
    if (ExerciseType.DIALOGUE != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }

    final WebDriver driver = super.getDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for loading
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
    // close cookie box
    super.closeCookieBox(driver);

    final List<EtutorDialogItem> etutorDialogItems = new ArrayList<>();
    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();
    final List<WebElement> dialogRowElements = driver.findElements(By.className("dialogueRow"));

    for (final WebElement element : dialogRowElements) {
      etutorDialogItems.add(
        EtutorDialogItem.builder()
          .id(null)
          .dialogEnglish(extractDialogEnglish(element))
          .dialogPolish(extractDialogPolish(element))
          .faceImage(extractFaceImage(element))
          .audio(extractAudio(driver))
          .soundSeekSecond(extractDataSoundSeekSeconds(element))
          .html(element.getAttribute("innerHTML"))
          .type(ExerciseType.DIALOGUE.toString())
          .exercise(etutorExercise)
          .build()
      );
    }

    if (!driver.findElements(By.id("switchToExerciseTestButton")).isEmpty()) {
      final WebElement switchToExerciseTestButton = driver.findElement(By.id("switchToExerciseTestButton"));
      switchToExerciseTestButton.click();

      final WebElement exercise = driver.findElement(By.id("dialogueExerciseTest"))
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

    etutorDialogRepository.saveAll(etutorDialogItems);
    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
    driver.quit();
  }

  private String extractDialogEnglish(final WebElement element) {
    return element.findElement(By.className("foreignTranscription")).getText();
  }

  private String extractDialogPolish(final WebElement element) {
    return element.findElement(By.className("nativeTranscription")).getText();
  }

  private BigDecimal extractDataSoundSeekSeconds(final WebElement element) {
    final String attribute = element.getAttribute("data-sound-seek-seconds");
    if (StringUtils.isNoneEmpty(attribute) && NumberUtils.isParsable(attribute)) {
      return BigDecimal.valueOf(Double.parseDouble(attribute));
    }
    return null;
  }

  private String extractFaceImage(final WebElement element) {
    final String style = element.findElement(By.className("dialogueFaceIcon")).getAttribute("style");
    return BASE_URL + style.substring(style.indexOf("(\"") + 2, style.indexOf("\")"));
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
