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
import rwilk.exploreenglish.scrapper.etutor.content.exercise.Choice;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.Cloze;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.MaskedWriting;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@java.lang.SuppressWarnings("java:S1192")
@Component
public class ExerciseItemScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;

  public ExerciseItemScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                              final EtutorExerciseItemRepository etutorExerciseItemRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
  }

  @Override
  public void run(final String... args) throws Exception {

//    webScrapExerciseTypeExercise(etutorExerciseRepository.findById(10L).get(), super.getDriver());

//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.EXERCISE.toString(), false)
//      .subList(0, 5)
//      .forEach(this::webScrapExerciseTypeExercise);
  }

  public void webScrapExerciseTypeExercise(final EtutorExercise etutorExercise, final WebDriver driver) {
    if (ExerciseType.EXERCISE != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
    // close cookie box
    super.closeCookieBox(driver);

    final String instruction = extractExerciseInstruction(driver.findElement(By.className("exercise")));

    final List<WebElement> exerciseQuestions = driver.findElements(By.className("exercise-numbered-question"));

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();
    exerciseQuestions.forEach(element -> {
      switch (driver.findElement(By.className("exercise")).getAttribute("data-exercise")) {
        case "choice" -> exerciseItems.add(Choice.webScrap(etutorExercise, element, instruction));
        case "masked-writing" -> exerciseItems.add(MaskedWriting.webScrap(etutorExercise, element, instruction, wait));
        case "cloze" -> {
          final EtutorExerciseItem etutorExerciseItem = Cloze.webScrap(etutorExercise, element, instruction);
          clickSolveQuestionButton(driver);
          etutorExerciseItem.setDescription(extractDescription(element));
          exerciseItems.add(etutorExerciseItem);
        }
        default -> throw new UnsupportedOperationException(driver.findElement(By.className("exercise"))
                                                             .getAttribute("data-exercise"));
      }
      clickNextQuestionButton(driver);
    });

    // TODO check because translation and finalAnswer are not on the right place, need to be replaced
    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
  }

  private String extractExerciseInstruction(final WebElement element) {
    if (!element.findElements(By.className("exerciseinstruction")).isEmpty()) {
      return element.findElement(By.className("exerciseinstruction")).getText().trim();
    }
    return null;
  }

  private void clickNextQuestionButton(final WebDriver driver) {
    if (!driver.findElements(By.id("nextQuestionButton")).isEmpty()
        && driver.findElement(By.id("nextQuestionButton")).isDisplayed()) {
      driver.findElement(By.id("nextQuestionButton")).click();
    }
  }

  private void clickSolveQuestionButton(final WebDriver driver) {
    if (!driver.findElements(By.id("solveQuestionButton")).isEmpty()
        && driver.findElement(By.id("solveQuestionButton")).isDisplayed()) {
      driver.findElement(By.id("solveQuestionButton")).click();
    }
  }

  private String extractDescription(final WebElement element) {
    if (element.findElements(By.className("immediateExplanation")).isEmpty()) {
      return null;
    }
    return element.findElement(By.className("immediateExplanation")).getText();
  }

}
