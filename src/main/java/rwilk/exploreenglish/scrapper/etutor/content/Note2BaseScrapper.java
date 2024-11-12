package rwilk.exploreenglish.scrapper.etutor.content;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class Note2BaseScrapper extends BaseScrapper {

    protected final EtutorExerciseRepository etutorExerciseRepository;
    protected final EtutorNoteRepository etutorNoteRepository;

    public Note2BaseScrapper(EtutorExerciseRepository etutorExerciseRepository, EtutorNoteRepository etutorNoteRepository) {
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorNoteRepository = etutorNoteRepository;
    }

    protected void webScrap(final EtutorExercise etutorExercise) {
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
                .nativeTitle(nativeLessonContent.findElement(By.tagName("h1")).getText())
                .nativeContent(nativeLessonContent.findElement(By.className("contentBox")).getText())
                .nativeHtml(nativeLessonContent.getAttribute("innerHTML"))
                .noteItems(new ArrayList<>())
                .exercise(etutorExercise)
                .build();
        if (!driver.findElements(By.className("foreignTranslationButton")).isEmpty()) {
            final WebElement foreignLessonContent = driver.findElement(By.className("foreignLessonContent"));
            driver.findElement(By.className("foreignTranslationButton")).click();
            etutorNote.setForeignTitle(foreignLessonContent.findElement(By.tagName("h1")).getText());
            etutorNote.setForeignContent(foreignLessonContent.findElement(By.className("contentBox")).getText());
            etutorNote.setForeignHtml(foreignLessonContent.getAttribute("innerHTML"));

            driver.findElement(By.className("nativeTranslationButton")).click();
        }

        final Document jsoupHtml = Jsoup.parse(etutorNote.getNativeHtml());
        final Element rootElement = jsoupHtml.getAllElements().first();
        if (rootElement != null) {
            printLeafNodesRecursive(rootElement, etutorNote.getNoteItems());
        }

        etutorNoteRepository.save(etutorNote);
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
        driver.quit();
    }

    private void printLeafNodesRecursive(final Element element, final List<EtutorNoteItem> noteItems) {
        if (element.children().isEmpty()) {
            System.out.println(element.text());
        } else {
            for (Node child : element.childNodes()) {
                printLeafNodesRecursive(child, noteItems);
            }
        }
    }

    private void printLeafNodesRecursive(final Node node, final List<EtutorNoteItem> noteItems) {
        if (node.childNodes().isEmpty()) {
            if (node instanceof final TextNode textNode) {
                final String text = textNode.text();

                if (StringUtils.isNoneBlank(text)) {
                    noteItems.add(
                            EtutorNoteItem.builder()
                                    .plainText(text)
                                    .example(null)
                                    .britishSound(null)
                                    .americanSound(null)
                                    .primaryStyle(getPrimaryStyle(node))
                                    .secondaryStyle(getSecondaryStyle(node))
                                    .build()
                    );
                } else {
                    noteItems.get(noteItems.size() - 1).setAdditional(textNode.getWholeText());
                }

            } else if (node instanceof final Element element) {
                if (element.tagName().equals("span")) {
                    final String dataAudioUrl = element.attributes().get("data-audio-url");
                    if (StringUtils.isNoneBlank(dataAudioUrl)) {
                        if (dataAudioUrl.contains("en-ame")) {
                            noteItems.get(noteItems.size() - 1).setAmericanSound(dataAudioUrl);
                        } else {
                            noteItems.get(noteItems.size() - 1).setBritishSound(dataAudioUrl);
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException(node.nodeName());
            }
        } else {
            for (final Node child : node.childNodes()) {
                printLeafNodesRecursive(child, noteItems);
            }
        }
    }

    private String getPrimaryStyle(final Node node) {
        if (node.parent() != null && node.parent() instanceof Element e) {
            return e.tagName();
        }
        return null;
    }

    private String getSecondaryStyle(final Node node) {
        if (node.parent() != null && node.parent() instanceof Element e) {
            if (e.parent() != null) {
                return e.parent().tagName();
            }
        }
        return null;
    }
}
