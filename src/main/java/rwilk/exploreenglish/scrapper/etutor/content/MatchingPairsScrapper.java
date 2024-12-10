package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class MatchingPairsScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;

  public MatchingPairsScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                               final EtutorExerciseItemRepository etutorExerciseItemRepository,
                               @Value("${explore-english.autologin-token}") final String autologinToken) {
    super(autologinToken);
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.MATCHING_PAIRS.toString(), false)
//      .subList(0, 3)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
    if (!List.of(
            ExerciseType.MATCHING_PAIRS_GRAMMAR,
            ExerciseType.MATCHING_PAIRS).contains(ExerciseType.valueOf(etutorExercise.getType()))) {
      return;
    }
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("matching-pairs")));
    // close cookie box
    super.closeCookieBox(driver);

    final WebElement content = driver.findElement(By.className("matching-pairs"));

    final String instruction = content.findElement(By.tagName("p")).getText();

    final List<WebElement> foreignElements = content.findElement(By.id("pairsLeft"))
      .findElements(By.className("pairElement"));
    final List<WebElement> nativeElements = content.findElement(By.id("pairsRight"))
      .findElements(By.className("pairElement"));

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();

    foreignElements.forEach(element1 -> nativeElements.forEach(element2 -> {
      if (element1.getAttribute("data-pairhash").equals(element2.getAttribute("data-pairhash"))) {
        exerciseItems.add(
          EtutorExerciseItem.builder()
            .id(null)
            .instruction(instruction)
            .correctAnswer(element1.getText())
            .question(element2.getText())
            .html(content.getAttribute("innerHTML"))
            .type(ExerciseType.MATCHING_PAIRS.toString())
            .exercise(etutorExercise)
            .build()
        );
      }
    }));

    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
  }
}
