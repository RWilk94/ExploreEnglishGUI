package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord;
import rwilk.exploreenglish.model.entity.etutor.EtutorWord;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorWordRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

// TODO to remove
@Component
public class GrammarListScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorWordRepository etutorWordRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;

  public GrammarListScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                             final EtutorWordRepository etutorWordRepository,
                             final EtutorExerciseItemRepository etutorExerciseItemRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorWordRepository = etutorWordRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findById(526L).ifPresent(this::webScrap);

//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.GRAMMAR_LIST.toString(), false)
//      .subList(0, 10)
//      .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise etutorExercise) {
    if (ExerciseType.GRAMMAR_LIST != ExerciseType.valueOf(etutorExercise.getType())) {
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
    // open dropdown menu
    driver.findElement(By.className("dropdownMenu")).click();
    //
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dropdownMenuOptions")));
    // and select 'Lista elementów'
    driver.findElement(By.className("dropdownMenuOptions")).findElement(By.linkText("Lista elementów")).click();

    // elements are grouped in div without any css class
    final List<WebElement> elements = driver.findElement(By.className("learningcontents"))
      .findElement(By.className("wordsgroup"))
      .findElements(By.tagName("table"));

    final List<EtutorWord> etutorWords = new ArrayList<>();
    final List<EtutorExerciseItem> etutorExerciseItems = new ArrayList<>();

    for (final WebElement element : elements) {
      final List<WebElement> rows = element.findElements(By.tagName("tr"));
      if (rows.size() != 2) {
        throw new UnsupportedOperationException("More elements than question and answer");
      }

      final WebElement questionElement = rows.get(0);
      final WebElement answerElement = rows.get(1);

      final String translation = questionElement.findElement(By.className("sentenceTranslation")).getText().trim();
      final String questionText = questionElement.findElement(By.tagName("td")).getText()
        .replace(translation, "").trim();
      final String answer = answerElement.findElement(By.tagName("td")).getText().trim();

      final EtutorWord etutorWord = EtutorWord.builder()
        .exercise(etutorExercise)
        .nativeTranslation(translation)
        .definitions(new ArrayList<>())
        .html(element.getAttribute("innerHTML"))
        .build();

      final EtutorDefinition etutorDefinition = EtutorDefinition.builder()
        .word(etutorWord)
        .foreignTranslation(answer)
        .primarySound(extractBritishAudioIcon(answerElement))
        .secondarySound(extractAmericanAudioIcon(answerElement))
        .type(WordTypeEnum.WORD.toString())
        .build();

      etutorWord.getDefinitions().add(etutorDefinition);
      etutorWords.add(etutorWord);

      etutorExerciseItems.add(
        EtutorExerciseItem.builder()
          .instruction("Uzupełnij luki poprawnymi wyrazami.")
          .question(translation)
          .questionTemplate(questionText.replace("(...)", "[...]"))
          .correctAnswer(answer)
          .finalAnswer(answer)
          .answerPrimarySound(extractBritishAudioIcon(answerElement))
          .answerSecondarySound(extractAmericanAudioIcon(answerElement))
          .firstPossibleAnswer("[\"" + answerElement.findElement(By.className("cloze")).getText().trim() + "\"]")
          .translation(translation)
          .type(ExerciseType.WRITING.toString())
          .exercise(etutorExercise)
          .build()
      );
    }

    etutorWords
      .forEach(it ->
                 it.setEtutorLessonWords(List.of(
                   EtutorLessonWord.builder()
                     .id(null)
                     .position(null)
                     .exercise(it.getExercise())
                     .word(it)
                     .build()))
      );

    etutorWordRepository.saveAll(etutorWords);
    etutorExerciseItemRepository.saveAll(etutorExerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
    driver.quit();
  }
}
