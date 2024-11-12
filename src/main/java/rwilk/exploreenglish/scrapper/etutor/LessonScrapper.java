package rwilk.exploreenglish.scrapper.etutor;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.model.entity.etutor.EtutorLesson;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Component
public class LessonScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorLessonRepository etutorLessonRepository;

  public LessonScrapper(final EtutorCourseRepository etutorCourseRepository,
                        final EtutorLessonRepository etutorLessonRepository) {
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorLessonRepository = etutorLessonRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    /* call the below method to web scrap etutor courses */
//    etutorCourseRepository.findAllByIsReady(false)
//      .forEach(this::webScrapAndSaveLessons);
  }

  public void webScrapAndSaveLessons(final EtutorCourse course) {
    final WebDriver driver = super.getDriver();
    final WebDriverWait wait = super.openDefaultPage(driver);

    // open course
    driver.get(course.getHref());
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonsList")));
    // close cookie box
    super.closeCookieBox(driver);
    // get lessons container
    final WebElement lessonsContainer = driver.findElement(By.className("lessonsList"));
    // and extract all single lesson containers to list
    final List<WebElement> lessonElements = lessonsContainer.findElements(By.tagName("li"));
    // the above lessonElement is just a lesson container
    // containing a href to dedicated lesson page
    // then iterate through the items and collect hrefs to a list
    // because of exception during extracting href of non-existing element on the screen
    final List<String> lessonHrefs = lessonElements.stream()
      .map(lesson ->
             lesson.findElement(By.tagName("a")).getAttribute("href"))
      .toList();

    // create a collection for result elements
    final List<EtutorLesson> etutorLessons = new ArrayList<>();

    // iterate through the hrefs and open the dedicated lesson page
    lessonHrefs.forEach(lessonHref -> {
      driver.get(lessonHref);
      // and wait until the page loads
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonDetailPage")));
      // get lesson page as variable to use later to extract lesson details
      final WebElement lessonDetailPage = driver.findElement(By.className("lessonDetailPage"));
      // get all sub-lessons
      final List<WebElement> lessonFrames = driver.findElements(By.className("singleLessonFrame"));
      // and query by them and collect in result collection
      etutorLessons.addAll(
        lessonFrames.stream()
          .map(lessonFrame ->
                 EtutorLesson.builder()
                   .name(lessonDetailPage.findElement(By.tagName("h1")).getText())
                   .description(lessonFrame.findElement(By.className("singleLessonTitle")).getText())
                   .href(lessonFrame.findElement(By.className("singleLessonTopFrame")).getAttribute("href"))
                   .image(lessonDetailPage.findElement(By.className("titleimage")).getAttribute("src"))
                   .course(course)
                   .isReady(false)
                   .build()
          ).toList());
    });

    etutorLessonRepository.saveAll(etutorLessons);
    course.setIsReady(true);
    etutorCourseRepository.save(course);
    driver.quit();
  }
}
