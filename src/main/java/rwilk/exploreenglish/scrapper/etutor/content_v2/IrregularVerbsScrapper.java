package rwilk.exploreenglish.scrapper.etutor.content_v2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

@Component
public class IrregularVerbsScrapper extends BaseScrapper implements CommandLineRunner {

    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorWordRepository etutorWordRepository;

    public IrregularVerbsScrapper(EtutorExerciseRepository etutorExerciseRepository,
                                  EtutorWordRepository etutorWordRepository,
                                  @Value("${explore-english.autologin-token}") final String autologinToken) {
        super(autologinToken);
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.etutorWordRepository = etutorWordRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("IrregularVerbsScrapper");
    }

    public void webScrap(final EtutorExercise etutorExercise, final WebDriver driver) {
        if (ExerciseType.GRAMMAR_LIST != ExerciseType.valueOf(etutorExercise.getType())
                && ExerciseType.WORDS_LIST != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }

        final WebElement irregularVerbs = driver.findElement(By.className("learningcontents"))
                .findElement(By.className("wordsgroupfirst"))
                .findElement(By.className("irregularverbs"));

        final List<WebElement> rows = irregularVerbs.findElements(By.tagName("tr"));

        final List<WebElement> headers = rows.get(0).findElements(By.tagName("th"));

        final String firstHeader = headers.get(0).getText();
        if (!firstHeader.equals("Infinitive")) {
            throw new RuntimeException("First header is not 'Infinitive'");
        }
        final String secondHeader = headers.get(1).getText();
        if (!secondHeader.equals("Past tense")) {
            throw new RuntimeException("Second header is not 'Past tense'");
        }
        final String thirdHeader = headers.get(2).getText();
        if (!thirdHeader.equals("Past participle")) {
            throw new RuntimeException("Third header is not 'Past participle'");
        }

        final List<EtutorWord> etutorWords = new ArrayList<>();

        for (int i = 1; i < rows.size() - 1; i = i + 2) {
            final EtutorWord etutorWord = EtutorWord.builder()
                    .exercise(etutorExercise)
                    .nativeTranslation(rows.get(i + 1).findElement(By.className("meaning")).getText().trim())
                    .html(rows.get(i + 1).getAttribute("innerHTML"))
                    .definitions(new ArrayList<>())
                    .build();


            final List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
            final String infinitive = cells.get(0).getText();
            final String pastTense = cells.get(1).getText();
            final String pastParticiple = cells.get(2).getText();

            final EtutorDefinition infinitiveDefinition = EtutorDefinition.builder()
                    .word(etutorWord)
                    .foreignTranslation(infinitive)
                    .type(WordTypeEnum.WORD.toString())
                    .primarySound(extractBritishAudioIcon(cells.get(3)))
                    .secondarySound(extractAmericanAudioIcon(cells.get(3)))
                    .build();

            final EtutorDefinition pastTenseDefinition = EtutorDefinition.builder()
                    .word(etutorWord)
                    .foreignTranslation(pastTense)
                    .type(WordTypeEnum.PAST_TENSE.toString())
                    .primarySound(extractBritishAudioIcon(cells.get(3)))
                    .secondarySound(extractAmericanAudioIcon(cells.get(3)))
                    .build();

            final EtutorDefinition pastParticipleDefinition = EtutorDefinition.builder()
                    .word(etutorWord)
                    .foreignTranslation(pastParticiple)
                    .type(WordTypeEnum.PAST_PARTICIPLE.toString())
                    .primarySound(extractBritishAudioIcon(cells.get(3)))
                    .secondarySound(extractAmericanAudioIcon(cells.get(3)))
                    .build();

            etutorWord.getDefinitions().add(infinitiveDefinition);
            etutorWord.getDefinitions().add(pastTenseDefinition);
            etutorWord.getDefinitions().add(pastParticipleDefinition);
            etutorWords.add(etutorWord);
        }

        etutorWords.forEach(w -> w.setExercise(etutorExercise));
        etutorWordRepository.saveAll(etutorWords);
        etutorExercise.setIsReady(true);
        etutorExerciseRepository.save(etutorExercise);
    }
}
