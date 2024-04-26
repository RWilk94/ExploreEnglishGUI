package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Triple;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@java.lang.SuppressWarnings({"java:S2142", "java:S112", "java:S1192"})
@Component
public class PictureListeningScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;
  private final ObjectMapper objectMapper;

  public PictureListeningScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                                  final EtutorExerciseItemRepository etutorExerciseItemRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.PICTURES_LISTENING.toString(), false)
//      .subList(0, 5)
//      .forEach(el -> {
//        try {
//          webScrap(el);
//        } catch (JsonProcessingException e) {
//          throw new RuntimeException(e);
//        }
//      });
  }

  public void webScrap(final EtutorExercise etutorExercise) throws JsonProcessingException {
    if (ExerciseType.PICTURES_LISTENING != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }

    final WebDriver driver = super.getDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("image-container")));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("audioIconButton")));
    // close cookie box
    super.closeCookieBox(driver);

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();
    final String currentUrl = driver.getCurrentUrl();
    final AtomicInteger index = new AtomicInteger(0);

    //noinspection ConditionalBreakInInfiniteLoop
    while (true) {
      final WebElement exercise = driver.findElement(By.className("exercise"));

      final List<Triple<String, String, String>> possibleAnswers = extractPossibleAnswers(exercise);

      final EtutorExerciseItem exerciseItem = EtutorExerciseItem.builder()
        .id(null)
        .instruction(extractInstruction(exercise))
        .correctAnswer(objectMapper.writeValueAsString(extractCorrectAnswer(exercise)))
        .firstPossibleAnswer(!possibleAnswers.isEmpty() ? objectMapper.writeValueAsString(possibleAnswers.get(0)) : null)
        .secondPossibleAnswer(possibleAnswers.size() >= 2 ? objectMapper.writeValueAsString(possibleAnswers.get(1)) : null)
        .thirdPossibleAnswer(possibleAnswers.size() >= 3 ? objectMapper.writeValueAsString(possibleAnswers.get(2)) : null)
        .forthPossibleAnswer(possibleAnswers.size() >= 4 ? objectMapper.writeValueAsString(possibleAnswers.get(3)) : null)
        .questionAmericanSound(extractAmericanAudioButton(exercise))
        .questionBritishSound(extractBritishAudioButton(exercise))
        .finalAnswer(extractCorrectAnswer(exercise).getLeft())
        .translation(extractCorrectAnswer(exercise).getRight())
        .description(null)
        .answerAmericanSound(extractAmericanAudioButton(exercise))
        .answerBritishSound(extractBritishAudioButton(exercise))
        .html(exercise.getAttribute("innerHTML"))
        .type(ExerciseItemType.PICTURES_LISTENING.toString())
        .exercise(etutorExercise)
        .build();
      index.incrementAndGet();
      exerciseItems.add(exerciseItem);

      final int correctAnswerIndex = possibleAnswers.indexOf(extractCorrectAnswer(exercise));
      exercise.findElements(By.className("image-container"))
        .stream()
        .map(el -> el.findElement(By.tagName("a")))
        .toList()
        .subList(correctAnswerIndex, correctAnswerIndex + 1).get(0).click();

      try {
        //noinspection BusyWait
        Thread.sleep(3000);
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

  private List<Triple<String, String, String>> extractPossibleAnswers(final WebElement element) {
    return element.findElements(By.className("image-container")).stream()
      .map(this::getTriple)
      .toList();
  }

  private Triple<String, String, String> extractCorrectAnswer(final WebElement element) {
    for (final WebElement el : element.findElements(By.className("image-container"))) {
      final String onClick = el.findElement(By.tagName("a")).getAttribute("onclick");
      if (onClick.contains(", true)")) {
        return getTriple(el);
      }
    }
    throw new UnsupportedOperationException("Empty correct answer");
  }

  private Triple<String, String, String> getTriple(final WebElement element) {
    final String answerText = element.findElement(By.className("answer-text")).getAttribute("innerHTML");
    String imgSource = element.findElement(By.tagName("a")).getAttribute("style");
    imgSource = BASE_URL + imgSource.substring(imgSource.indexOf("(\"") + 2, imgSource.indexOf("\")"));
    final String translation = element.findElement(By.className("picturesNativeWordTermText")).getText();

    return Triple.of(answerText, imgSource, translation);
  }

}
