package rwilk.exploreenglish.scrapper.etutor.content_v2.speaking;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpeakingScrapperV2 extends BaseScrapper implements CommandLineRunner {

    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorExerciseItemRepository etutorExerciseItemRepository;

    public SpeakingScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                              EtutorExerciseItemRepository etutorExerciseItemRepository) {
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    }

    @Override
    public void run(final String... args) throws Exception {
//        etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.SPEAKING.toString(), false)
//                .forEach(this::webScrap);
    }

    public void webScrap(final EtutorExercise etutorExercise) {
        if (ExerciseType.SPEAKING != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }

        final WebDriver driver = super.getDriver();
        final WebDriverWait wait = super.openDefaultPage(driver);

        // open course
        driver.get(etutorExercise.getHref());
        // and wait for display list of lessons
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise-pronunciation")));
        // close cookie box
        super.closeCookieBox(driver);

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
        driver.quit();
    }

    protected String extractBritishAudioIcon(final WebElement element) {
        return extractAudioIcon(element, "British English");
    }

    protected String extractAmericanAudioIcon(final WebElement element) {
        return extractAudioIcon(element, "American English");
    }

    private String extractAudioIcon(final WebElement element, final String title) {
        return element.findElements(By.className("hasRecording")).stream()
                .map(it -> extractDataAudioIconUrlAttribute(it, title))
                .filter(StringUtils::isNoneEmpty)
                .findFirst()
                .orElse(null);
    }

    private String extractDataAudioIconUrlAttribute(final WebElement element, final String title) {
        if (StringUtils.defaultString(element.getAttribute("title"), "").equals(title)) {
            return BASE_URL + element.findElement(By.className("audioIcon"))
                    .getAttribute("data-audio-url");
        }
        return extractDataAudioIconUrlAttributeBackup(element, title);
    }

    private String extractDataAudioIconUrlAttributeBackup(final WebElement element, final String title) {
        if (StringUtils.defaultString(element.getAttribute("oldtitle"), "").equals(title)) {
            return BASE_URL + element.findElement(By.className("audioIcon"))
                    .getAttribute("data-audio-url");
        }
        return "";
    }
}
