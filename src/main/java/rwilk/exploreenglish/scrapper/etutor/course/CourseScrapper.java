package rwilk.exploreenglish.scrapper.etutor.course;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.LanguageEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;

@SuppressWarnings("unused")
@Component
public class CourseScrapper extends BaseScrapper implements CommandLineRunner {
    private static final Map<LanguageEnum, List<String>> englishCourses = Map.of(
            LanguageEnum.ENGLISH, List.of(
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
                    "https://www.etutor.pl/lessons/en/travel")
    );
    private static final Map<LanguageEnum, List<String>> germanCourses = Map.of(
            LanguageEnum.GERMAN, List.of(
                    "https://www.etutor.pl/lessons/de/de-a1",
                    "https://www.etutor.pl/lessons/de/de-a2",
                    "https://www.etutor.pl/lessons/de/de-b1",
                    "https://www.etutor.pl/lessons/de/de-b2",
                    "https://www.etutor.pl/lessons/de/de-c1",
                    "https://www.etutor.pl/lessons/de/reise",
                    "https://www.etutor.pl/lessons/de/bde")
    );
    private static final Map<LanguageEnum, List<String>> spanishCourses = Map.of(
            LanguageEnum.SPANISH, List.of(
                    "https://www.etutor.pl/lessons/es/es-a1",
                    "https://www.etutor.pl/lessons/es/es-a2",
                    "https://www.etutor.pl/lessons/es/es-b1",
                    "https://www.etutor.pl/lessons/es/es-b2")
    );
    private static final Map<LanguageEnum, List<String>> italianCourses = Map.of(
            LanguageEnum.ITALIAN, List.of(
                    "https://www.etutor.pl/lessons/it/it-a1",
                    "https://www.etutor.pl/lessons/it/it-a2",
                    "https://www.etutor.pl/lessons/it/it-b1",
                    "https://www.etutor.pl/lessons/it/it-b2")
    );
    private static final Map<LanguageEnum, List<String>> frenchCourses = Map.of(
            LanguageEnum.FRENCH, List.of(
                    "https://www.etutor.pl/lessons/fr/fr-a1",
                    "https://www.etutor.pl/lessons/fr/fr-a2",
                    "https://www.etutor.pl/lessons/fr/fr-b1",
                    "https://www.etutor.pl/lessons/fr/fr-b2")
    );

    private final EtutorCourseRepository etutorCourseRepository;

    public CourseScrapper(final EtutorCourseRepository etutorCourseRepository) {
        this.etutorCourseRepository = etutorCourseRepository;
    }

    @Override
    public void run(final String... args) throws Exception {
        /* call the below method to web scrap etutor courses */
        // webScrapAndSaveCourses();
    }

    public void webScrapAndSaveCourses() {
        webScrap(englishCourses);
        webScrap(germanCourses);
        webScrap(spanishCourses);
        webScrap(italianCourses);
        webScrap(frenchCourses);
    }

    public int countCourses() {
        return englishCourses.values().stream().mapToInt(List::size).sum() +
                germanCourses.values().stream().mapToInt(List::size).sum() +
                spanishCourses.values().stream().mapToInt(List::size).sum() +
                italianCourses.values().stream().mapToInt(List::size).sum() +
                frenchCourses.values().stream().mapToInt(List::size).sum();
    }

    public List<EtutorCourse> findAllCoursesToWebScrap() {
        return etutorCourseRepository.findAllByIsReady(false);
    }

    private void webScrap(final Map<LanguageEnum, List<String>> courses) {
        final LanguageEnum language = courses.keySet().stream().findFirst().orElse(null);
        final List<String> courseList = courses.values().stream().findFirst().orElseThrow(() -> new IllegalStateException("Course list is empty"));

        courseList.forEach(course -> {
            final WebDriver driver = super.getDriver();
            final WebDriverWait wait = super.openDefaultPage(driver);

            // open course
            driver.get(course);
            // and wait for display list of lessons
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonsList")));
            // close cookie box
            super.closeCookieBox(driver);

            final EtutorCourse etutorCourse = EtutorCourse.builder()
                    .name(driver
                            .findElement(By.className("lessonListHeaderSelectors"))
                            .findElement(By.className("selectMainLevelBox"))
                            .findElement(By.className("displaySelect"))
                            .getText())
                    .description(extractDescription(driver))
                    .href(course)
                    .image(driver.findElement(By.className("courseImg"))
                            .getAttribute("src"))
                    .language(language.name())
                    .isReady(false)
                    .build();

            etutorCourseRepository.save(etutorCourse);
            driver.quit();
        });
    }

    private String extractDescription(final WebDriver driver) {
        if (driver.findElements(By.className("selectSubLevelBox")).isEmpty()) {
            return null;
        }

        return driver
                .findElement(By.className("lessonListHeaderSelectors"))
                .findElement(By.className("selectSubLevelBox"))
                .findElement(By.className("displaySelect"))
                .getText();
    }
}
