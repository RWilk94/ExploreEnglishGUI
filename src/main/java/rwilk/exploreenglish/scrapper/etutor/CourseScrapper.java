package rwilk.exploreenglish.scrapper.etutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.LanguageEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;

@SuppressWarnings("unused")
@Component
public class CourseScrapper extends BaseScrapper implements CommandLineRunner {

  private static final Map<String, LanguageEnum> courseMap = new HashMap<>();
  private static final List<String> englishCourses = List.of(
    "https://www.etutor.pl/lessons/en/a1",
    "https://www.etutor.pl/lessons/en/a2",
    "https://www.etutor.pl/lessons/en/b1",
    "https://www.etutor.pl/lessons/en/b2",
    "https://www.etutor.pl/lessons/en/c1",
    "https://www.etutor.pl/lessons/en/c2",
    "https://www.etutor.pl/lessons/en/business-b1",
    "https://www.etutor.pl/lessons/en/business-b2",
    "https://www.etutor.pl/lessons/en/business-c",
    "https://www.etutor.pl/lessons/en/business-ext",
    "https://www.etutor.pl/lessons/en/matura-1",
    "https://www.etutor.pl/lessons/en/matura-2",
    "https://www.etutor.pl/lessons/en/matura-3",
    "https://www.etutor.pl/lessons/en/travel");
  private static final List<String> germanCourses = List.of(
    "https://www.etutor.pl/lessons/de/de-a1",
    "https://www.etutor.pl/lessons/de/de-a2",
    "https://www.etutor.pl/lessons/de/de-b1",
    "https://www.etutor.pl/lessons/de/de-b2",
    "https://www.etutor.pl/lessons/de/de-c1",
    "https://www.etutor.pl/lessons/de/reise",
    "https://www.etutor.pl/lessons/de/bde");
  private static final List<String> spanishCourses = List.of(
    "https://www.etutor.pl/lessons/es/es-a1",
    "https://www.etutor.pl/lessons/es/es-a2",
    "https://www.etutor.pl/lessons/es/es-b1",
    "https://www.etutor.pl/lessons/es/es-b2");
  private static final List<String> italianCourses = List.of(
    "https://www.etutor.pl/lessons/it/it-a1",
    "https://www.etutor.pl/lessons/it/it-a2",
    "https://www.etutor.pl/lessons/it/it-b1");
  private static final List<String> frenchCourses = List.of(
    "https://www.etutor.pl/lessons/fr/fr-a1",
    "https://www.etutor.pl/lessons/fr/fr-a2",
    "https://www.etutor.pl/lessons/fr/fr-b1");

  private final EtutorCourseRepository etutorCourseRepository;

  public CourseScrapper(final EtutorCourseRepository etutorCourseRepository) {
    this.etutorCourseRepository = etutorCourseRepository;

    courseMap.putAll(
      englishCourses.stream()
        .collect(Collectors.toMap(String::toString, it -> LanguageEnum.ENGLISH))
    );
    courseMap.putAll(
      germanCourses.stream()
        .collect(Collectors.toMap(String::toString, it -> LanguageEnum.GERMAN))
    );
    courseMap.putAll(
      spanishCourses.stream()
        .collect(Collectors.toMap(String::toString, it -> LanguageEnum.SPANISH))
    );
    courseMap.putAll(
      italianCourses.stream()
        .collect(Collectors.toMap(String::toString, it -> LanguageEnum.ITALIAN))
    );
    courseMap.putAll(
      frenchCourses.stream()
        .collect(Collectors.toMap(String::toString, it -> LanguageEnum.FRENCH))
    );
  }

  @Override
  public void run(final String... args) throws Exception {
    /* call the below method to web scrap etutor courses */
    // webScrapAndSaveCourses();
  }

  public void webScrapAndSaveCourses() {
    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // create a collection for result elements
    final List<EtutorCourse> etutorCourses = new ArrayList<>();

    courseMap.forEach((key, value) -> {

      // open course
      driver.get(key);
      // and wait for display list of lessons
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonsList")));

      etutorCourses.add(
        EtutorCourse.builder()
          .name(driver.findElement(By.className("selectMainLevelBox"))
                  .findElement(By.className("displaySelect"))
                  .getText())
          .description(extractDescription(driver))
          .href(key)
          .image(driver.findElement(By.className("courseImg"))
                   .getAttribute("src"))
          .language(value.toString())
          .build()
      );

    });
    etutorCourseRepository.saveAll(etutorCourses);
    driver.quit();
  }

  private String extractDescription(final WebDriver driver) {
    if (driver.findElements(By.className("selectSubMenuBox")).isEmpty()) {
      return null;
    }

    return driver.findElement(By.className("selectSubMenuBox"))
      .findElement(By.className("displaySelect"))
      .getText();
  }
}
