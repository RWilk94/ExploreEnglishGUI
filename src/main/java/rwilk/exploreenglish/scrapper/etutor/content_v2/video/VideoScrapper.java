package rwilk.exploreenglish.scrapper.etutor.content_v2.video;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.repository.etutor.EtutorDialogRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoScrapper extends BaseScrapper implements CommandLineRunner {

    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorDialogRepository etutorDialogRepository;

    public VideoScrapper(EtutorExerciseRepository etutorExerciseRepository,
                         EtutorDialogRepository etutorDialogRepository) {
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorDialogRepository = etutorDialogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
    }

    public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
        if (ExerciseType.VIDEO != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }
        final WebDriverWait wait = super.openDefaultPage(driver);

        // open course
        driver.get(etutorExercise.getHref());
        // and wait for loading
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
        // close cookie box
        super.closeCookieBox(driver);

        final List<EtutorDialogItem> etutorDialogItems = new ArrayList<>();

        if (!driver.findElements(By.className("videoScrollboxList")).isEmpty()) {
            final List<WebElement> elements = driver.findElement(By.className("videoScrollboxList"))
                    .findElements(By.tagName("li"));

            for (final WebElement element : elements) {
                etutorDialogItems.add(
                        EtutorDialogItem.builder()
                                .id(null)
                                .dialogForeign(extractDialogEnglish(element))
                                .dialogNative(null)
                                .faceImage(null)
                                .audio(extractVideoUrl(driver))
                                .soundSeekSecond(extractDataSoundSeekSeconds(element))
                                .html(element.getAttribute("innerHTML"))
                                .type(ExerciseType.VIDEO.toString())
                                .exercise(etutorExercise)
                                .build()
                );
            }
        } else {
            etutorDialogItems.add(
                    EtutorDialogItem.builder()
                            .id(null)
                            .dialogForeign(null)
                            .dialogNative(null)
                            .faceImage(null)
                            .audio(extractVideoUrl(driver))
                            .soundSeekSecond(null)
                            .html(driver.findElement(By.className("videoBox")).getAttribute("innerHTML"))
                            .type(ExerciseType.VIDEO.toString())
                            .exercise(etutorExercise)
                            .build()
            );
        }

        etutorDialogRepository.saveAll(etutorDialogItems);
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
    }

    private String extractVideoUrl(final WebDriver driver) {
        final String videoUrl = driver.findElement(By.className("videoPlayerContainer"))
                .findElement(By.tagName("video"))
                .getAttribute("src");

        if (videoUrl.contains(BASE_URL)) {
            return videoUrl;
        } else {
            return BASE_URL + videoUrl;
        }
    }

    private String extractDialogEnglish(final WebElement element) {
        return element.findElement(By.className("songleftonly")).getText();
    }

    private BigDecimal extractDataSoundSeekSeconds(final WebElement element) {
        final String onclick = element.getAttribute("onclick");
        if (StringUtils.isNoneBlank(onclick)) {
            return new BigDecimal(onclick.substring(onclick.indexOf("(") + 1, onclick.indexOf(")")));
        }
        return null;
    }
}
