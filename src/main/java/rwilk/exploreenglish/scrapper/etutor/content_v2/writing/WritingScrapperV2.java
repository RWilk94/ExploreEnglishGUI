package rwilk.exploreenglish.scrapper.etutor.content_v2.writing;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.content_v2.BaseNoteScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.ArrayList;

@Component
public class WritingScrapperV2 extends BaseNoteScrapperV2 implements CommandLineRunner {

    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorNoteRepository etutorNoteRepository;

    public WritingScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                             EtutorNoteRepository etutorNoteRepository) {
        super(etutorExerciseRepository, etutorNoteRepository);
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorNoteRepository = etutorNoteRepository;
    }

    @Override
    public void run(final String... args) throws Exception {
//        etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.WRITING.toString(), false)
//                .forEach(it -> {
//                    final WebDriver driver = super.getDriver();
//                    webScrap(it, driver);
//                });
    }

    public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
        if (ExerciseType.WRITING != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }
        final WebDriverWait wait = super.openDefaultPage(driver);

        // open course
        driver.get(etutorExercise.getHref());
        // and wait for display list of lessons
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
        // close cookie box
        super.closeCookieBox(driver);

        final EtutorNote etutorNote = EtutorNote.builder()
                .noteItems(new ArrayList<>())
                .exercise(etutorExercise)
                .build();

        if (!driver.findElements(By.className("nativeLessonContent")).isEmpty()) {
            final WebElement nativeLessonContent = driver.findElement(By.className("nativeLessonContent"));
            etutorNote.setNativeContent(nativeLessonContent.findElement(By.className("contentBox")).getText());
            etutorNote.setNativeHtml(nativeLessonContent.getAttribute("innerHTML"));
        }

        if (!driver.findElements(By.className("foreignTranslationButton")).isEmpty()) {
            final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
            driver.findElement(By.className("foreignTranslationButton")).click();

            etutorNote.setForeignContent(foreignLessonContent.findElement(By.className("contentBox")).getText());
            etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));

            driver.findElement(By.className("nativeTranslationButton")).click();

        } else if (!driver.findElements(By.className("foreignLessonContent")).isEmpty()) {
            final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
            etutorNote.setForeignContent(foreignLessonContent.findElement(By.className("contentBox")).getText());
            etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));
        }

        if (StringUtils.isNoneBlank(etutorNote.getNativeContent())) {
            final Document jsoupHtml = Jsoup.parse(etutorNote.getNativeHtml());
            final Element rootElement = jsoupHtml.getAllElements().first();
            if (rootElement != null) {
                super.printLeafNodesRecursive(rootElement, etutorNote, "native");
            }
        }

        if (StringUtils.isNoneBlank(etutorNote.getForeignHtml())) {
            final Document jsoupForeignHtml = Jsoup.parse(etutorNote.getForeignHtml());
            final Element rootForeignElement = jsoupForeignHtml.getAllElements().first();
            if (rootForeignElement != null) {
                super.printLeafNodesRecursive(rootForeignElement, etutorNote, "foreign");
            }
        }

        etutorNoteRepository.save(etutorNote);
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
    }
}
