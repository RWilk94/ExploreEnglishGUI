package rwilk.exploreenglish.scrapper.etutor.content_v2.reading;

import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.Choice;
import rwilk.exploreenglish.scrapper.etutor.content.exercise.Cloze;
import rwilk.exploreenglish.scrapper.etutor.content_v2.BaseNoteScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReadingScrapperV2 extends BaseNoteScrapperV2 implements CommandLineRunner {

    private final EtutorExerciseItemRepository etutorExerciseItemRepository;

    public ReadingScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                             EtutorNoteRepository etutorNoteRepository,
                             EtutorExerciseItemRepository etutorExerciseItemRepository,
                             @Value("${explore-english.autologin-token}") final String autologinToken) {
        super(etutorExerciseRepository, etutorNoteRepository, autologinToken);
        this.etutorExerciseItemRepository = etutorExerciseItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        final WebDriver driver = super.getDriver();
//        webScrap(etutorExerciseRepository.findById(6177L).get(), driver);
    }

    @Transactional
    public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
        if (ExerciseType.READING != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }
        final WebDriverWait wait = super.openDefaultPage(driver);

        // open course
        driver.get(etutorExercise.getHref());
        // and wait for loading
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
        // close cookie box
        super.closeCookieBox(driver);

        final WebElement element = driver.findElement(By.className("exercise"));
        final EtutorNote etutorNote = EtutorNote.builder()
                .id(null)
                .nativeContent(extractNativeText(element))
                .foreignContent(extractForeignText(element))
                .nativeHtml(extractNativeHtml(element))
                .foreignHtml(extractForeignHTML(element))
                .audio(extractAudio(driver))
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

        final List<EtutorExerciseItem> exerciseItems = webScrapTestExerciseItems(driver, etutorExercise);

        etutorNoteRepository.save(etutorNote);
        if (CollectionUtils.isNotEmpty(exerciseItems)) {
            etutorExerciseItemRepository.saveAll(exerciseItems);
        }
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
    }

    private String extractNativeText(final WebElement element) {
        return element.findElement(By.className("readingExerciseNativeText")).getText();
    }

    private String extractForeignText(final WebElement element) {
        return element.findElement(By.className("readingExerciseForeignText")).getText();
    }

    private String extractNativeHtml(final WebElement element) {
        return element.findElement(By.className("readingExerciseNativeText")).getAttribute("innerHTML");
    }

    private String extractForeignHTML(final WebElement element) {
        return element.findElement(By.className("readingExerciseForeignText")).getAttribute("innerHTML");
    }

    private String extractAudio(final WebDriver driver) {
        return driver.findElement(By.id("contentWrapper")).findElements(By.tagName("script"))
                .stream()
                .filter(element -> element.getAttribute("innerHTML").contains(".mp3"))
                .findFirst()
                .map(element -> element.getAttribute("innerHTML"))
                .map(text -> text.substring(text.indexOf("(\"") + 2, text.indexOf("\")")))
                .map(url -> BASE_URL + url)
                .orElse(null);
    }

    private List<EtutorExerciseItem> webScrapTestExerciseItems(final WebDriver driver, final EtutorExercise etutorExercise) {
        final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();

        if (!driver.findElements(By.id("switchToExerciseTestButton")).isEmpty()) {
            final WebElement switchToExerciseTestButton = driver.findElement(By.id("switchToExerciseTestButton"));
            scrollToElement(driver, switchToExerciseTestButton);
            switchToExerciseTestButton.click();

            final WebElement exercise = driver.findElement(By.id("readingExerciseTest"))
                    .findElement(By.className("exercise"));
            final String instruction = exercise.findElement(By.className("exerciseinstruction")).getText();

            for (final WebElement ex : exercise.findElements(By.className("exercise-numbered-question"))) {

                final WebElement component = ex.findElement(By.className("component"));
                final String type = component.getAttribute("data-component");

                if (type.equalsIgnoreCase("choice")) {
                    final EtutorExerciseItem etutorExerciseItem = Choice.webScrap(etutorExercise, ex, instruction, autologinToken);
                    exerciseItems.add(etutorExerciseItem);

                    // find and click correct answer
                    for (final WebElement answer : ex.findElements(By.className("examChoiceOptionBox"))) {
                        if (answer.findElement(By.tagName("input")).getAttribute("value")
                                .equals(etutorExerciseItem.getCorrectAnswer())) {
                            answer.click();
                            // and get to next question
                            final WebElement nextQuestionButton = driver.findElement(By.id("nextQuestionButton"));
                            if (nextQuestionButton != null && nextQuestionButton.isDisplayed() && !nextQuestionButton.getAttribute("class")
                                    .contains("hidden")) {
                                nextQuestionButton.click();
                            }
                            break;
                        }
                    }

                } else if (type.equalsIgnoreCase("cloze")) {
                    final EtutorExerciseItem etutorExerciseItem = Cloze.webScrap(etutorExercise, ex, instruction);
                    exerciseItems.add(etutorExerciseItem);

                    final WebElement solveQuestionButton = driver.findElement(By.id("solveQuestionButton"));
                    if (solveQuestionButton != null && solveQuestionButton.isDisplayed() && !solveQuestionButton.getAttribute("class")
                            .contains("hidden")) {
                        solveQuestionButton.click();
                    }

                    super.sleep(300);

                    final WebElement nextQuestionButton = driver.findElement(By.id("nextQuestionButton"));
                    if (nextQuestionButton != null && nextQuestionButton.isDisplayed() && !nextQuestionButton.getAttribute("class")
                            .contains("hidden")) {
                        nextQuestionButton.click();
                    }
                }
            }
        }
        return exerciseItems;
    }

}
