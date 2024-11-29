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

        if (driver.findElements(By.className("exercise-pronunciation")).isEmpty()) {
            // TODO webscrap as note
        } else {

        }


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
    }
}
