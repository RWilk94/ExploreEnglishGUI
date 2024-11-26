package rwilk.exploreenglish.scrapper.etutor.content_v2.writing;

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
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.ArrayList;

@Component
public class WritingScrapperV2 extends BaseScrapper implements CommandLineRunner {

    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorNoteRepository etutorNoteRepository;

    public WritingScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                             EtutorNoteRepository etutorNoteRepository) {
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorNoteRepository = etutorNoteRepository;
    }

    @Override
    public void run(final String... args) throws Exception {
//        etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.WRITING.toString(), false)
//                .forEach(this::webScrap);
    }

    public void webScrap(final EtutorExercise etutorExercise) {
        if (ExerciseType.WRITING != ExerciseType.valueOf(etutorExercise.getType())) {
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

        WebElement nativeLessonContent;
        if (!driver.findElements(By.className("nativeLessonContent")).isEmpty()) {
            nativeLessonContent = driver.findElement(By.className("nativeLessonContent"));
        } else {
            nativeLessonContent = driver.findElement(By.className("exercise"));
        }

        final EtutorNote etutorNote = EtutorNote.builder()
                .nativeContent(nativeLessonContent.findElement(By.className("contentBox")).getText())
                .nativeHtml(nativeLessonContent.getAttribute("innerHTML"))
                .noteItems(new ArrayList<>())
                .exercise(etutorExercise)
                .build();
        if (!driver.findElements(By.className("foreignTranslationButton")).isEmpty()) {
            final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
            driver.findElement(By.className("foreignTranslationButton")).click();
            etutorNote.setForeignContent(foreignLessonContent.findElement(By.className("contentBox")).getText());
            etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));

            driver.findElement(By.className("nativeTranslationButton")).click();
        }

        etutorNoteRepository.save(etutorNote);
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
        driver.quit();
    }
}
