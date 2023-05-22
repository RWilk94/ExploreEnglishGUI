package rwilk.exploreenglish.scrapper.etutor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;

@Component
public class EtutorCourseScrapper implements CommandLineRunner {

  private static final List<String> courseHrefs = List.of(
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
    "https://www.etutor.pl/lessons/en/matura-3");
  private final EtutorCourseRepository etutorCourseRepository;

  public EtutorCourseScrapper(final EtutorCourseRepository etutorCourseRepository) {
    this.etutorCourseRepository = etutorCourseRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    /* call the below method to web scrap etutor courses */
  }

  public void webScrapAndSaveCourses() {
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");

    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    final Cookie cookie = new Cookie("autoLoginToken", "BKygYBbhlF7YeJDEJu6wr3peLRtKg3UjZsGNTDHQ");
    
    driver.get("https://www.etutor.pl");
    driver.manage().addCookie(cookie);

    // create a collection for result elements
    final List<EtutorCourse> etutorCourses = new ArrayList<>();

    courseHrefs.forEach(href -> {

      // open course
      driver.get(href);
      // and wait for display list of lessons
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonsList")));

      etutorCourses.add(
        EtutorCourse.builder()
          .name(driver.findElement(By.className("selectMainLevelBox"))
                  .findElement(By.className("displaySelect"))
                  .getText())
          .description(driver.findElement(By.className("selectSubMenuBox"))
                         .findElement(By.className("displaySelect"))
                         .getText())
          .href(href)
          .image(driver.findElement(By.className("courseImg"))
                   .getAttribute("src"))
          .build()
      );

    });
    etutorCourseRepository.saveAll(etutorCourses);
    driver.quit();
  }
}
