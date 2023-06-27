package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
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
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class SpeakingScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorNoteRepository etutorNoteRepository;

  public SpeakingScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                          final EtutorNoteRepository etutorNoteRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorNoteRepository = etutorNoteRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.SPEAKING.toString(), false)
//      .subList(0, 3)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise) {
    if (ExerciseType.SPEAKING != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonhtml")));

    final WebElement nativeLessonContent = driver.findElement(By.className("nativeLessonContent"));

    final EtutorNote etutorNote = EtutorNote.builder()
      .nativeTitle(driver.findElement(By.className("lessonhtml")).getAttribute("data-exercise-title"))
      .nativeContent(extractContent(nativeLessonContent.findElement(By.className("contentBox"))))
      .nativeHtml(nativeLessonContent.getAttribute("innerHTML"))
      .noteItems(new ArrayList<>())
      .exercise(etutorExercise)
      .build();
    if (!driver.findElements(By.className("foreignTranslationButton")).isEmpty()) {
      final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
      driver.findElement(By.className("foreignTranslationButton")).click();
      etutorNote.setForeignTitle(driver.findElement(By.className("lessonhtml"))
                                   .getAttribute("data-exercise-title"));
      etutorNote.setForeignContent(extractContent(foreignLessonContent.findElement(By.className("contentBox"))));
      etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));

      driver.findElement(By.className("nativeTranslationButton")).click();
    }

    etutorNoteRepository.save(etutorNote);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
    driver.quit();
  }

  private String extractContent(final WebElement element) {
    return element.findElements(By.tagName("p"))
      .stream().map(WebElement::getText)
      .collect(Collectors.joining("\n\n"));
  }

}
