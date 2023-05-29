package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@SuppressWarnings({"java:S2142", "java:S112", "java:S1192"})
@Component
public class PictureChoiceScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;

  public PictureChoiceScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                               final EtutorExerciseItemRepository etutorExerciseItemRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.PICTURES_CHOICE.toString(), false)
      .subList(0, 5)
      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise) {
    if (ExerciseType.PICTURES_CHOICE != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }

    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("image-container")));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("picturetextanswer")));

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();
    final String currentUrl = driver.getCurrentUrl();
    final AtomicInteger index = new AtomicInteger(0);

    //noinspection ConditionalBreakInInfiniteLoop
    while (true) {
      final WebElement exercise = driver.findElement(By.className("exercise"));

      final List<String> possibleAnswers = extractPossibleAnswers(exercise);

      final EtutorExerciseItem exerciseItem = EtutorExerciseItem.builder()
        .id(null)
        .instruction(extractInstruction(exercise))
        .correctAnswer(extractCorrectAnswer(exercise))
        .firstPossibleAnswer(!possibleAnswers.isEmpty() ? possibleAnswers.get(0) : null)
        .secondPossibleAnswer(possibleAnswers.size() >= 2 ? possibleAnswers.get(1) : null)
        .thirdPossibleAnswer(possibleAnswers.size() >= 3 ? possibleAnswers.get(2) : null)
        .forthPossibleAnswer(possibleAnswers.size() >= 4 ? possibleAnswers.get(3) : null)
        .questionAmericanSound(null)
        .questionBritishSound(null)
        .question(extractQuestion(exercise))
        .finalAnswer(extractCorrectAnswer(exercise))
        .translation(extractTranslation(exercise))
        .description(null)
        .answerAmericanSound(extractCorrectAnswerAudio(exercise, "/en-ame/"))
        .answerBritishSound(extractCorrectAnswerAudio(exercise, "/en/"))
        .html(exercise.getAttribute("innerHTML"))
        .type(ExerciseItemType.PICTURES_CHOICE.toString())
        .exercise(etutorExercise)
        .build();
      index.incrementAndGet();
      exerciseItems.add(exerciseItem);

      exercise.findElements(By.className("picturetextanswer"))
        .stream()
        .filter(el -> el.getText().equals(exerciseItem.getCorrectAnswer()))
        .toList()
        .get(0)
        .click();

      try {
        //noinspection BusyWait
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      if (!driver.getCurrentUrl().equals(currentUrl)) {
        break;
      }
    }

    if (index.get() != exerciseItems.size()) {
      throw new UnsupportedOperationException("invalid list size, possibility of missing element");
    }

    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
    driver.quit();
  }

  private String extractInstruction(final WebElement element) {
    if (!element.findElements(By.className("center")).isEmpty()) {
      return element.findElement(By.className("center")).getText().trim();
    }
    return "";
  }

  private List<String> extractPossibleAnswers(final WebElement element) {
    return element.findElements(By.className("picturetextanswer")).stream()
      .map(WebElement::getText)
      .toList();
  }

  private String extractCorrectAnswer(final WebElement element) {
    for (final WebElement el : element.findElements(By.className("picturetextanswer"))) {
      final String onClick = el.getAttribute("onclick");
      if (onClick.contains(", true,")) {
        return el.getText();
      }
    }
    throw new UnsupportedOperationException("Empty correct answer");
  }

  private String extractTranslation(final WebElement element) {
    return element.findElement(By.className("picturestable1-extra-content")).findElement(By.className("small"))
      .getText();
  }

  private String extractCorrectAnswerAudio(final WebElement element, final String language) {
    final String audio = extractCorrectAnswerAudio(element);
    if (audio.contains(language)) {
      return audio;
    }
    return null;
  }

  private String extractCorrectAnswerAudio(final WebElement element) {
    for (final WebElement el : element.findElements(By.className("picturetextanswer"))) {
      final String onClick = el.getAttribute("onclick");
      if (onClick.contains(", true,")) {
        return BASE_URL + onClick.substring(onClick.indexOf("/images-common"), onClick.indexOf(".mp3") + 4);
      }
    }
    throw new UnsupportedOperationException("Empty correct answer");
  }

  private String extractQuestion(final WebElement element) {
    final String imgSource = element.findElement(By.className("picturesGameNonClickableImgDiv")).getAttribute("style");
    return BASE_URL + imgSource.substring(imgSource.indexOf("(\"") + 2, imgSource.indexOf("\")"));
  }


}
