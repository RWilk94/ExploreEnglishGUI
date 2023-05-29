package rwilk.exploreenglish.scrapper.etutor.content.exercise;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;

@SuppressWarnings({"java:S1192", "java:S2142", "java:S112"})
public class Cloze {

  public static EtutorExerciseItem webScrap(final EtutorExercise etutorExercise, final WebElement element,
                                            final String instruction) {
    return new Cloze().get(etutorExercise, element, instruction);
  }

  private Cloze() {
  }

  private EtutorExerciseItem get(final EtutorExercise etutorExercise, final WebElement element,
                                 final String instruction) {
    final List<String> possibleAnswers = extractPossibleAnswers(element);
    if (possibleAnswers.size() > 4) {
      throw new UnsupportedOperationException("possibleAnswers contains more then 4 items");
    }

    return EtutorExerciseItem.builder()
      .id(null)
      .instruction(instruction)
      .correctAnswer(extractCorrectAnswer(element))
      .firstPossibleAnswer(!possibleAnswers.isEmpty() ? possibleAnswers.get(0) : null)
      .secondPossibleAnswer(possibleAnswers.size() >= 2 ? possibleAnswers.get(1) : null)
      .thirdPossibleAnswer(possibleAnswers.size() >= 3 ? possibleAnswers.get(2) : null)
      .forthPossibleAnswer(possibleAnswers.size() >= 4 ? possibleAnswers.get(3) : null)
      .question(extractWritingQuestion(element))
      .questionTemplate(extractWritingQuestionTemplate(element))
      .translation(extractWritingQuestion(element))
      .html(element.getAttribute("innerHTML"))
      .type(ExerciseItemType.CLOZE.toString())
      .exercise(etutorExercise)
      .build();
  }

  private List<String> extractPossibleAnswers(final WebElement element) {
    return element.findElements(By.className("examClozeInput"))
      .stream()
      .map(el -> el.getAttribute("data-correct-answer"))
      .toList();
  }

  private String extractCorrectAnswer(final WebElement element) {
    final String sentence = element.findElement(By.className("examClozeFillableSentence")).getText();
    final String correctAnswer = element.findElement(By.className("examClozeInput"))
      .getAttribute("data-correct-answer");

    return sentence.concat(correctAnswer);
  }

  private String extractWritingQuestion(final WebElement element) {
    return element.findElement(By.className("examClozeCommand")).getText();
  }

  private String extractWritingQuestionTemplate(final WebElement element) {
    final String sentence = element.findElement(By.className("examClozeFillableSentence")).getText();
    return sentence.concat("[...]");
  }

}
