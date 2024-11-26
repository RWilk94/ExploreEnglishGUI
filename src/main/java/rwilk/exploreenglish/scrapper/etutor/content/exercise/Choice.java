package rwilk.exploreenglish.scrapper.etutor.content.exercise;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;

@java.lang.SuppressWarnings("java:S1192")
public class Choice extends BaseScrapper {

  private static final String BASE_URL = "https://www.etutor.pl";

  public static EtutorExerciseItem webScrap(final EtutorExercise etutorExercise, final WebElement element,
                                            final String instruction) {
    return new Choice().get(etutorExercise, element, instruction);
  }

  private Choice() {
  }

  private EtutorExerciseItem get(final EtutorExercise etutorExercise, final WebElement element,
                                 final String instruction) {
    final String correctAnswer = extractCorrectAnswer(element);
    final List<String> possibleAnswers = extractPossibleAnswers(element, correctAnswer);

    final EtutorExerciseItem exerciseItem = EtutorExerciseItem.builder()
      .id(null)
      .instruction(instruction)
      .correctAnswer(correctAnswer)
      .firstPossibleAnswer(!possibleAnswers.isEmpty() ? possibleAnswers.get(0) : null)
      .secondPossibleAnswer(possibleAnswers.size() >= 2 ? possibleAnswers.get(1) : null)
      .thirdPossibleAnswer(possibleAnswers.size() >= 3 ? possibleAnswers.get(2) : null)
      .forthPossibleAnswer(possibleAnswers.size() >= 4 ? possibleAnswers.get(3) : null)
      .question(extractQuestion(element))
      .questionSecondarySound(extractAmericanAudioButton(element))
      .questionPrimarySound(extractBritishAudioButton(element))
      .finalAnswer(extractFinalAnswer(element))
      .translation(extractTranslation(element))
      .description(extractDescription(element))
      .answerSecondarySound(extractAmericanAudioIcon(element))
      .answerPrimarySound(extractBritishAudioIcon(element))
      .html(element.getAttribute("innerHTML"))
      .type(ExerciseItemType.CHOICE.toString())
      .exercise(etutorExercise)
      .build();

    validateExerciseItem(exerciseItem);
    return exerciseItem;
  }

  private String extractCorrectAnswer(final WebElement element) {
    return element.findElement(By.className("component")).getAttribute("data-correct-answer").trim();
  }

  private List<String> extractPossibleAnswers(final WebElement element, final String correctAnswer) {
    final List<WebElement> possibleAnswerElements = element.findElement(By.className("examChoiceAnswers"))
      .findElements(By.className("examChoiceOptionBox"));

    return possibleAnswerElements.stream().map(possibleAnswerElement -> {
      final String possibleAnswer = possibleAnswerElement.getText();
      if (possibleAnswer.equals(correctAnswer)) {
        possibleAnswerElement.click();
      }
      return possibleAnswer;
    }).toList();
  }

  private String extractQuestion(final WebElement element) {
    if (element.findElements(By.className("examChoiceQuestion")).isEmpty()) {
      return "";
    }

    final WebElement questionDiv = element.findElement(By.className("examChoiceQuestion"));

    final String questionText = questionDiv.getText();

    if (!questionDiv.findElements(By.className("selectedAnswerBox")).isEmpty()) {
      final String correctAnswerText = questionDiv.findElement(By.className("selectedAnswerBox")).getText();
      if (StringUtils.isNoneEmpty(correctAnswerText)) {
        if (questionText.contains(correctAnswerText.concat(" "))) {
          return questionText.replace(correctAnswerText.concat(" "), "[...] ");
        } else {
          return questionText.replace(correctAnswerText, "[...] ");
        }
      }
    }
    return questionText;
  }

  private String extractFinalAnswer(final WebElement element) {
    if (element.findElements(By.className("examChoiceQuestion")).isEmpty()) {
      return "";
    }

    final String question = element.findElement(By.className("examChoiceQuestion")).getText();

    if (question.equals(extractQuestion(element))) {
      return extractCorrectAnswer(element);
    }
    return question;
  }

  private String extractTranslation(final WebElement element) {
    if (!element.findElements(By.className("immediateTranslation")).isEmpty()) {
      return element.findElement(By.className("immediateTranslation")).getText();
    }
    return "";
  }

  private String extractDescription(final WebElement element) {
    if (!element.findElements(By.className("immediateExplanation")).isEmpty()) {
      return element.findElement(By.className("immediateExplanation")).getText();
    }
    return "";
  }

  private void validateExerciseItem(final EtutorExerciseItem exerciseItem) {
    if (StringUtils.isNoneEmpty(exerciseItem.getFinalAnswer())
        && exerciseItem.getFinalAnswer().contains("(")
        && exerciseItem.getFinalAnswer().contains(")")
        && StringUtils.isEmpty(exerciseItem.getTranslation())) {

      final String finalAnswer = exerciseItem.getFinalAnswer();
      exerciseItem.setTranslation(finalAnswer.substring(finalAnswer.indexOf("(") + 1, finalAnswer.indexOf(")")).trim());
      exerciseItem.setFinalAnswer(finalAnswer.substring(0, finalAnswer.indexOf("(")).trim());

    } else if (StringUtils.isNoneEmpty(exerciseItem.getFinalAnswer())
               && exerciseItem.getFinalAnswer().contains("=")
               && StringUtils.isEmpty(exerciseItem.getTranslation())) {
      final String finalAnswer = exerciseItem.getFinalAnswer();
      exerciseItem.setTranslation(finalAnswer.substring(finalAnswer.indexOf("=") + 1).trim());
      exerciseItem.setFinalAnswer(finalAnswer.substring(0, finalAnswer.indexOf("=")).trim());
    }
  }

}
