package rwilk.exploreenglish.scrapper.etutor.content_v2.speaking;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.content_v2.BaseNoteScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpeakingScrapperV2 extends BaseNoteScrapperV2 implements CommandLineRunner {

    private final EtutorExerciseItemRepository etutorExerciseItemRepository;

    public SpeakingScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                              EtutorExerciseItemRepository etutorExerciseItemRepository,
                              EtutorNoteRepository etutorNoteRepository,
                              @Value("${explore-english.autologin-token}") final String autologinToken) {
        super(etutorExerciseRepository, etutorNoteRepository, autologinToken);
        this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    }

    @Override
    public void run(final String... args) throws Exception {
//        etutorExerciseRepository.findById(1835L).ifPresent(etutorExercise -> {
//            final WebDriver driver = super.getDriver();
//            webScrap(etutorExercise, driver);
//        });
    }

    public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
        if (ExerciseType.SPEAKING != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }
        final WebDriverWait wait = super.openDefaultPage(driver);

        // open course
        driver.get(etutorExercise.getHref());
        // and wait for display list of lessons
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonHeader")));
        // close cookie box
        super.closeCookieBox(driver);

        if (!driver.findElements(By.className("exercise-pronunciation")).isEmpty()) {
            final WebElement content = driver.findElement(By.className("exercise-pronunciation"));
            final String instruction = content
                    .findElement(By.className("center"))
                    .getText();

            final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();
            final List<WebElement> elements = content.findElements(By.className("training-example-content"));
            for (final WebElement element : elements) {

                final String question = element
                        .findElement(By.className("text-to-read"))
                        .getAttribute("innerText");

                final String translation = element
                        .findElement(By.className("polish-translation"))
                        .getAttribute("innerText");

                final String britishSound = extractBritishAudioIcon(element);
                final String americanSound = extractAmericanAudioIcon(element);

                exerciseItems.add(
                        EtutorExerciseItem.builder()
                                .exercise(etutorExercise)
                                .instruction(instruction)
                                .question(question)
                                .questionSecondarySound(americanSound)
                                .questionPrimarySound(britishSound)
                                .translation(translation)
                                .build()
                );
            }
            etutorExerciseItemRepository.saveAll(exerciseItems);
            etutorExercise.setIsReady(true);
            etutorExerciseRepository.save(etutorExercise);

        } else {
            final WebElement element = driver.findElement(By.className("lessonhtml"));

            final EtutorNote etutorNote = EtutorNote.builder()
                    .id(null)
                    .nativeContent(extractNativeText(element))
                    .foreignContent(extractForeignText(element))
                    .nativeHtml(extractNativeHtml(element))
                    .foreignHtml(extractForeignHTML(element))
                    .audio(null)
                    .exercise(etutorExercise)
                    .noteItems(new ArrayList<>())
                    .build();

            final Document jsoupHtml = Jsoup.parse(etutorNote.getNativeHtml());
            final Element rootElement = jsoupHtml.getAllElements().first();
            if (rootElement != null) {
                super.printLeafNodesRecursive(rootElement, etutorNote, "native");
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

    private String extractNativeText(final WebElement element) {
        if (element.findElements(By.className("nativeLessonContent")).isEmpty()) {
            return null;
        }
        return element.findElement(By.className("nativeLessonContent")).getText();
    }

    private String extractForeignText(final WebElement element) {
        if (element.findElements(By.className("foreignLessonContent")).isEmpty()) {
            return null;
        }
        return element.findElement(By.className("foreignLessonContent")).getText();
    }

    private String extractNativeHtml(final WebElement element) {
        if (element.findElements(By.className("nativeLessonContent")).isEmpty()) {
            return null;
        }
        return element.findElement(By.className("nativeLessonContent")).getAttribute("innerHTML");
    }

    private String extractForeignHTML(final WebElement element) {
        if (element.findElements(By.className("foreignLessonContent")).isEmpty()) {
            return null;
        }
        return element.findElement(By.className("foreignLessonContent")).getAttribute("innerHTML");
    }
}
