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

import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.repository.etutor.EtutorDialogRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@java.lang.SuppressWarnings({"java:S1192", "java:S2142", "java:S112"})
@Component
public class ComicBookScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorDialogRepository etutorDialogRepository;

  public ComicBookScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                           final EtutorDialogRepository etutorDialogRepository,
                           @Value("${explore-english.autologin-token}") final String autologinToken) {
    super(autologinToken);
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorDialogRepository = etutorDialogRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.COMIC_BOOK.toString(), false)
//      .subList(0, 3)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
    if (ExerciseType.COMIC_BOOK != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(etutorExercise.getHref());
    // and wait for loading
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comicBookImage")));
    // close cookie box
    super.closeCookieBox(driver);

    final List<EtutorDialogItem> etutorDialogItems = new ArrayList<>();

    while (true) {

      try {
        //noinspection BusyWait
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      final WebElement element = driver.findElement(By.id("comicBookContainer"));
      etutorDialogItems.add(
        EtutorDialogItem.builder()
          .id(null)
          .comicImage(extractComicImage(element))
          .dialogNative(extractTranslation(element))
          .audio(extractAudio(element))
          .html(element.getAttribute("innerHTML"))
          .type(ExerciseType.COMIC_BOOK.toString())
          .exercise(etutorExercise)
          .build()
      );

      final WebElement comicBookNext = element.findElement(By.className("comicBookNext"));
      if (comicBookNext != null && !comicBookNext.getAttribute("class").contains("hidden")) {
        comicBookNext.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comicBookImage")));
      } else {
        break;
      }
    }

    etutorDialogRepository.saveAll(etutorDialogItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
  }

  private String extractComicImage(final WebElement element) {
    return element.findElement(By.id("comicBookImage")).findElement(By.tagName("img")).getAttribute("src");
  }

  private String extractAudio(final WebElement element) {
    return element.findElement(By.id("comicBookImage")).findElement(By.tagName("img")).getAttribute("data-audio-url");
  }

  private String extractTranslation(final WebElement element) {
    return element.findElement(By.id("comicBookTranslationHtml")).getText();
  }

}
