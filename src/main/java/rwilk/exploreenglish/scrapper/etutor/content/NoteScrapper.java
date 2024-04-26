package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.NoteItem;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@SuppressWarnings({"java:S1192", "DuplicatedCode"})
@Component
public class NoteScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorNoteRepository etutorNoteRepository;

  public NoteScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                      final EtutorNoteRepository etutorNoteRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorNoteRepository = etutorNoteRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.SCREEN.toString(), false)
//      .subList(0, 10)
      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise) {
    if (ExerciseType.SCREEN != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    final WebDriver driver = super.getDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
    // close cookie box
    super.closeCookieBox(driver);

    WebElement nativeLessonContent = null;
    if (!driver.findElements(By.className("nativeLessonContent")).isEmpty()) {
      nativeLessonContent = driver.findElement(By.className("nativeLessonContent"));
    } else {
      nativeLessonContent = driver.findElement(By.className("exercise"));
    }

    final EtutorNote etutorNote = EtutorNote.builder()
      .nativeTitle(nativeLessonContent.findElement(By.tagName("h1")).getText())
      .nativeContent(nativeLessonContent.findElement(By.className("contentBox")).getText())
      .nativeHtml(nativeLessonContent.getAttribute("innerHTML"))
      .noteItems(new ArrayList<>())
      .exercise(etutorExercise)
      .build();
    if (!driver.findElements(By.className("foreignTranslationButton")).isEmpty()) {
      final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
      driver.findElement(By.className("foreignTranslationButton")).click();
      etutorNote.setForeignTitle(foreignLessonContent.findElement(By.tagName("h1")).getText());
      etutorNote.setForeignContent(foreignLessonContent.findElement(By.className("contentBox")).getText());
      etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));

      driver.findElement(By.className("nativeTranslationButton")).click();
    }

    final List<WebElement> noteRows = nativeLessonContent.findElement(By.className("contentBox"))
      .findElements(By.xpath(XPATH_CHILDREN));

    for (final WebElement row : noteRows) {
      switch (row.getTagName()) {
        case "h2", "h3", "h4" -> etutorNote.getNoteItems().add(buildEtutorNoteFromHeading(etutorNote, row));
        case "p" -> {
          if (!StringUtils.isEmpty(row.getAttribute("innerHTML"))) {
            final List<EtutorNoteItem> etutorNoteItems = NoteItem.webScrap(etutorNote, row.getAttribute("innerHTML"));
            etutorNote.getNoteItems().addAll(etutorNoteItems);
          }
        }
        case "div" -> {
          if (row.getAttribute("class").equals("lessonImportantNote")) {
            final String innerHTML = row.getAttribute("innerHTML").replace("→", "=");
            final List<EtutorNoteItem> etutorNoteItems = NoteItem.webScrap(etutorNote, innerHTML);
            etutorNote.getNoteItems().addAll(etutorNoteItems);
          }
        }
        case "blockquote" -> {
          final WebElement paragraph = row.findElement(By.tagName("p"));
          if (!StringUtils.isEmpty(paragraph.getAttribute("innerHTML"))) {
            final List<EtutorNoteItem> etutorNoteItems = NoteItem.webScrap(etutorNote, paragraph.getAttribute("innerHTML"));
            etutorNote.getNoteItems().addAll(etutorNoteItems);
          }
        }
        case "ul" -> {
          if (!row.findElements(By.tagName("li")).isEmpty()) {
            final WebElement li = row.findElement(By.tagName("li"));
            final List<EtutorNoteItem> etutorNoteItems = NoteItem.webScrap(etutorNote, li.getAttribute("innerHTML"));
            etutorNote.getNoteItems().addAll(etutorNoteItems);
          }
        }
        case "table" -> {
          if (!row.findElements(By.tagName("thead")).isEmpty()) {
            final List<WebElement> headerColumns = row.findElement(By.tagName("thead")).findElements(By.tagName("th"));
            final List<EtutorNoteItem> etutorNoteHeaderItems = headerColumns.stream()
              .map(it -> NoteItem.webScrap(etutorNote, it.getAttribute("innerHTML")))
              .flatMap(Collection::stream)
              .toList();
            etutorNote.getNoteItems().addAll(etutorNoteHeaderItems);
          }

          if (!row.findElements(By.tagName("tbody")).isEmpty()) {
            final List<WebElement> bodyColumns = row.findElement(By.tagName("tbody")).findElements(By.tagName("td"));
            final List<EtutorNoteItem> etutorNoteBodyItems = bodyColumns.stream()
              .map(it -> NoteItem.webScrap(etutorNote, it.getAttribute("innerHTML")))
              .flatMap(Collection::stream)
              .toList();
            etutorNote.getNoteItems().addAll(etutorNoteBodyItems);
          }
        }
        case "ol" -> {
          final List<EtutorNoteItem> etutorNoteItems = row.findElements(By.xpath(XPATH_CHILDREN)).stream()
            .map(it -> NoteItem.webScrap(etutorNote, it.getAttribute("innerHTML")))
            .flatMap(Collection::stream)
            .toList();
          etutorNote.getNoteItems().addAll(etutorNoteItems);
        }
        case "style" -> {

        }
        default -> throw new UnsupportedOperationException(row.getTagName());
      }
    }

    etutorNoteRepository.save(etutorNote);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
    driver.quit();
  }

  private EtutorNoteItem buildEtutorNoteFromHeading(final EtutorNote etutorNote, final WebElement row) {
    return EtutorNoteItem.builder()
      .id(null)
      .plainText(row.getAttribute("innerHTML"))
      .note(etutorNote)
      .build();
  }

}
