package rwilk.exploreenglish.scrapper.etutor.content_v2.grammarlist;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorWord;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorWordRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.ArrayList;
import java.util.List;

import static rwilk.exploreenglish.scrapper.etutor.content.WordScrapper.beautify;

@Component
public class GrammarListScrapperV2 extends BaseScrapper implements CommandLineRunner {

    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorWordRepository etutorWordRepository;

    public GrammarListScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                                 EtutorWordRepository etutorWordRepository,
                                 @Value("${explore-english.autologin-token}") final String autologinToken) {
        super(autologinToken);
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorWordRepository = etutorWordRepository;
    }

    @Override
    public void run(final String... args) throws Exception {
//        etutorExerciseRepository.findById(1169L).ifPresent(this::webScrap);
//        etutorExerciseRepository.findById(526L).ifPresent(this::webScrap);
    }

    public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
        if (ExerciseType.GRAMMAR_LIST != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }
        final WebDriverWait wait = super.openDefaultPage(driver);

        // open course
        driver.get(etutorExercise.getHref());
        // and wait for display list of lessons
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
        // close cookie box
        super.closeCookieBox(driver);
        // open dropdown menu
        driver.findElement(By.className("dropdownMenu")).click();
        //
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dropdownMenuOptions")));
        // and select 'Lista elementów'
        driver.findElement(By.className("dropdownMenuOptions")).findElement(By.linkText("Lista elementów")).click();

        final String contentHtml = driver.findElement(By.className("learningcontents"))
                .findElement(By.className("wordsgroupfirst"))
                .getAttribute("innerHTML");

        final Document jsoupDocument = Jsoup.parse(contentHtml);

        final List<EtutorWord> etutorWords = new ArrayList<>();
        for (final Node node : jsoupDocument.child(0).child(1).children()) {

            if (node instanceof final Element e) {
                if (e.tagName().equals("div")) {

                    if (!e.children().isEmpty()
                            && !e.className().equals("noBreakLine")) {
                        final EtutorWord etutorWord = webScrapWordAndDefinition(e);
                        etutorWords.add(etutorWord);
                    }

                } else if (e.tagName().equals("table")) {
                    final EtutorWord etutorWord = webScrapGrammarList(e);
                    etutorWords.add(etutorWord);
                }
            }
        }

        etutorWords.forEach(w -> w.setExercise(etutorExercise));
        etutorWordRepository.saveAll(etutorWords);
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
    }

    private EtutorWord webScrapWordAndDefinition(final Element element) {
        final EtutorWord etutorWord = EtutorWord.builder()
                .definitions(new ArrayList<>())
                .html(element.html())
                .build();

        for (final Node node : element.children()) {
            if (node instanceof final Element e) {
                if (e.tagName().equals("img")) {
                    final String image = webScrapImage(e);
                    etutorWord.setImage(image);

                } else if (e.tagName().equals("div") && e.className().contains("phraseEntity")) {
                    final List<EtutorDefinition> etutorDefinitions = webScrapDefinitions(e);
                    etutorDefinitions.forEach(d -> d.setWord(etutorWord));
                    etutorWord.getDefinitions().addAll(etutorDefinitions);
                    etutorWord.setNativeTranslation(webScrapPolishName(e));

                } else if (e.tagName().equals("ul") && e.className().contains("sentencesul")) {
                    final List<EtutorDefinition> etutorDefinitions = webScrapSentenceDefinition(e);
                    etutorDefinitions.forEach(d -> d.setWord(etutorWord));
                    etutorWord.getDefinitions().addAll(etutorDefinitions);
                }
            }
        }

        return etutorWord;
    }

    private String webScrapImage(final Element element) {
        final String image = BASE_URL + element.attr("src");
        return StringUtils.isBlank(image) ? null : image;
    }

    private List<EtutorDefinition> webScrapDefinitions(final Element html) {
        final List<EtutorDefinition> etutorDefinitions = new ArrayList<>();

        for (final Element el : html
                .select("span.hw")
                .select("span.phraseEntityLine")) {

            final EtutorDefinition etutorDefinition = EtutorDefinition.builder()
                    .foreignTranslation(el.text().trim())
                    .type(WordTypeEnum.WORD.toString())
                    .build();

            final List<Element> audioElements = el.select("span.audioIcon[data-audio-url]");
            for (final Element audioElement : audioElements) {
                final String audioUrl = audioElement.attr("data-audio-url");

                if (SECONDARY_LANGUAGES.stream().anyMatch(audioUrl::contains)) {
                    etutorDefinition.setSecondarySound(BASE_URL + audioUrl);
                } else {
                    etutorDefinition.setPrimarySound(BASE_URL + audioUrl);
                }
            }

            etutorDefinitions.add(etutorDefinition);
        }
        return etutorDefinitions;
    }

    private String webScrapPolishName(final Element html) {
        final String[] parts = html.text().split("=");
        String translation = parts.length > 1 ? parts[1].trim() : "";

        translation = translation
                .replace("Wpisz treść notatki...", "")
                .replace("àèéìòóù", "")
                .trim();

        return beautify(translation);
    }

    private List<EtutorDefinition> webScrapSentenceDefinition(final Element html) {
        final List<EtutorDefinition> etutorDefinitions = new ArrayList<>();
        for (Element element : html.select("li")) {

            final EtutorDefinition etutorDefinition = EtutorDefinition.builder()
                    .type(WordTypeEnum.SENTENCE.toString())
                    .build();

            final List<Element> audioElements = element.select("span.audioIcon[data-audio-url]");
            for (final Element audioElement : audioElements) {
                final String audioUrl = audioElement.attr("data-audio-url");

                if (SECONDARY_LANGUAGES.stream().anyMatch(audioUrl::contains)) {
                    etutorDefinition.setSecondarySound(BASE_URL + audioUrl);
                } else {
                    etutorDefinition.setPrimarySound(BASE_URL + audioUrl);
                }
            }

            final String sentenceTranslation = beautify(element.select("span.sentenceTranslation").text().trim());
            etutorDefinition.setAdditionalInformation(sentenceTranslation);
            final String sentenceText = beautify(element.text()
                    .replace(sentenceTranslation, "")
                    .replace("Wpisz treść notatki...", "")
                    .trim());
            etutorDefinition.setForeignTranslation(sentenceText);

            etutorDefinitions.add(etutorDefinition);
        }
        return etutorDefinitions;
    }

    private EtutorWord webScrapGrammarList(final Element element) {
        final EtutorWord etutorWord = EtutorWord.builder()
                .definitions(new ArrayList<>())
                .html(element.html())
                .build();
        final EtutorDefinition etutorDefinition = EtutorDefinition.builder()
                .word(etutorWord)
                .type(WordTypeEnum.WORD.toString())
                .build();

        final Element translationElement = element.selectFirst("div.sentenceTranslation");
        final String translation = translationElement != null ? translationElement.text().trim() : "";
        etutorWord.setNativeTranslation(translation);

        final Element phraseElement = element.selectFirst("span.phraseEntityLine");
        final String phrase = phraseElement != null ? phraseElement.text().trim() : "";
        etutorDefinition.setForeignTranslation(phrase);

        final List<Element> audioElements = element.select("span.audioIcon[data-audio-url]");
        for (final Element audioElement : audioElements) {
            final String audioUrl = audioElement.attr("data-audio-url");

            if (SECONDARY_LANGUAGES.stream().anyMatch(audioUrl::contains)) {
                etutorDefinition.setSecondarySound(BASE_URL + audioUrl);
            } else {
                etutorDefinition.setPrimarySound(BASE_URL + audioUrl);
            }
        }

        etutorWord.getDefinitions().add(etutorDefinition);
        return etutorWord;
    }
}
