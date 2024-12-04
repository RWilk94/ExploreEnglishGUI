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
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.PicturesMaskedWriting;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class PicturesMaskedWritingScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;

  public PicturesMaskedWritingScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                                       final EtutorExerciseItemRepository etutorExerciseItemRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findById(501L).ifPresent(this::webScrap);

//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.PICTURES_MASKED_WRITING.toString(), false)
//      .subList(0, 5)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
    if (ExerciseType.PICTURES_MASKED_WRITING != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("image-container")));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
    // close cookie box
    super.closeCookieBox(driver);

    final WebElement exercise = driver.findElement(By.className("exercise"));
    final String instruction = extractExerciseInstruction(exercise);

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();

    while (!driver.findElements(By.id("suggestNextLetterButton")).isEmpty()) {
      final WebElement exercise2 = driver.findElement(By.className("exercise"));
      if (!exercise2.getAttribute("data-exercise").equals("pictures-masked-writing")) {
        break;
      }
      exerciseItems.add(PicturesMaskedWriting.webScrap(etutorExercise, exercise2, instruction));
      try {
        Thread.sleep(3000 + (countLetterSize(exercise2) * 100));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    exerciseItems.forEach(it -> it.setType(ExerciseType.PICTURES_MASKED_WRITING.toString()));
    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
  }

  private String extractExerciseInstruction(final WebElement element) {
    if (!element.findElements(By.className("center")).isEmpty()) {
      return element.findElement(By.className("center")).getText().trim();
    }
    return null;
  }

  private int countLetterSize(final WebElement element) {
    return element
      .findElements(By.className("writing-mask"))
      .stream()
      .map(it -> it.findElement(By.tagName("input")).getAttribute("size"))
      .map(Integer::parseInt).mapToInt(it -> it)
      .sum();
  }

}
