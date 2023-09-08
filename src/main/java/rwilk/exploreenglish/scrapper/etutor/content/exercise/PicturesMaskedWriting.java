package rwilk.exploreenglish.scrapper.etutor.content.exercise;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;

@SuppressWarnings({"java:S1192", "java:S2142", "java:S112", "StatementWithEmptyBody", "java:S108"})
public class PicturesMaskedWriting {

  private static final String XPATH_CHILDREN = "./child::*";
  private static final String BASE_URL = "https://www.etutor.pl";

  public static EtutorExerciseItem webScrap(final EtutorExercise etutorExercise, final WebElement element,
                                            final String instruction) {
    return new PicturesMaskedWriting().get(etutorExercise, element, instruction);
  }

  private PicturesMaskedWriting() {
  }

  private EtutorExerciseItem get(final EtutorExercise etutorExercise, final WebElement element,
                                 final String instruction) {
    List<String> possibleAnswers = extractPossibleAnswers(element);
    if (possibleAnswers.size() > 4) {
      possibleAnswers = mergePossibleAnswers(possibleAnswers);
      if (possibleAnswers.size() > 4) {
        throw new UnsupportedOperationException("possibleAnswers contains more then 4 items");
      }
    }

    final EtutorExerciseItem exerciseItem = EtutorExerciseItem.builder()
      .id(null)
      .instruction(instruction)
      .correctAnswer(extractWritingCorrectAnswer(element))
      .firstPossibleAnswer(!possibleAnswers.isEmpty() ? possibleAnswers.get(0) : null)
      .secondPossibleAnswer(possibleAnswers.size() >= 2 ? possibleAnswers.get(1) : null)
      .thirdPossibleAnswer(possibleAnswers.size() >= 3 ? possibleAnswers.get(2) : null)
      .forthPossibleAnswer(possibleAnswers.size() >= 4 ? possibleAnswers.get(3) : null)
      .question(extractWritingQuestion(element))
      .questionTemplate(extractWritingQuestionTemplate(element))
      .translation(extractWritingQuestion(element))
      .html(element.getAttribute("innerHTML"))
      .type(ExerciseItemType.WRITING.toString())
      .exercise(etutorExercise)
      .build();

    final WebElement suggestNextLetterButton = element.findElement(By.xpath("../parent::*"))
      .findElement(By.id("suggestNextLetterButton"));

    final int letterSize = element
      .findElements(By.className("writing-mask"))
      .stream()
      .map(it -> it.findElement(By.tagName("input")).getAttribute("size"))
      .map(Integer::parseInt).mapToInt(it -> it)
      .sum();

    for (int i = 0; i < letterSize; i++) {
      try {
        suggestNextLetterButton.click();
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    final String finalAnswer = element.findElement(By.className("masked-writing-core")).getText()
      .replace("\n", " ")
      .replace(" .", ".")
      .replace("' ", "'")
      .replace(" '", "'")
      .replace(" ,", "'")
      .replace(" ?", "?")
      .replace(" !", "!");
    final String britishSound = extractBritishSound(element);
    final String americanSound = extractAmericanSound(element);

    exerciseItem.setFinalAnswer(finalAnswer);
    exerciseItem.setAnswerBritishSound(britishSound);
    exerciseItem.setQuestionBritishSound(britishSound);
    exerciseItem.setAnswerAmericanSound(americanSound);
    exerciseItem.setQuestionAmericanSound(americanSound);

    return exerciseItem;
  }

  private List<String> extractPossibleAnswers(final WebElement element) {
    final List<WebElement> children = element.findElement(By.className("masked-writing-core"))
      .findElements(By.xpath(XPATH_CHILDREN));
    final List<String> possibleAnswers = new ArrayList<>();

    for (final WebElement child : children) {
      if (child.getAttribute("class").equals("masked-element")) {
        final String validValues = child.findElement(By.tagName("input")).getAttribute("data-valid-values");
        if (validValues.contains(",") || validValues.contains(";")) {
          throw new UnsupportedOperationException(validValues);
        }
        possibleAnswers.add(validValues);
      }
    }
    return possibleAnswers;
  }

  private List<String> mergePossibleAnswers(final List<String> possibleAnswers) {
    final List<String> newPossibleAnswers = new ArrayList<>();
    final int mergeCounter = possibleAnswers.size() / 2;

    int index = 0;

    for (int i = 0; i < mergeCounter; i++) {
      final String answer1 = possibleAnswers.get(index);
      final String answer2 = possibleAnswers.get(index + 1);

      newPossibleAnswers.add(
        "[" +
        answer1.substring(answer1.indexOf("[") + 1, answer1.indexOf("]")) +
        "," +
        answer2.substring(answer2.indexOf("[") + 1, answer2.indexOf("]")) +
        "]"
      );

      index += 2;
    }
    return newPossibleAnswers;
  }

  private String extractWritingCorrectAnswer(final WebElement element) {
    final List<WebElement> children = element.findElement(By.className("masked-writing-core"))
      .findElements(By.xpath(XPATH_CHILDREN));

    final StringBuilder sb = new StringBuilder();
    for (final WebElement child : children) {
      if (child.getAttribute("class").equals("wordsBesideMask")) {
        if (!child.getText().startsWith("'")) {
          appendSpaceIfRequired(sb);
        } else if (child.getText().startsWith("'") && sb.toString().endsWith(" ")) {
          sb.setLength(sb.length() - 1);
        }
        sb.append(child.getText());

      } else if (child.getAttribute("class").equals("masked-element")) {
        final String validValues = child.findElement(By.tagName("input")).getAttribute("data-valid-values");
        if (validValues.contains(",") || validValues.contains(";")) {
          throw new UnsupportedOperationException(validValues);
        }
        appendSpaceIfRequired(sb);

        final String preMaskText = extractTextOfElementByClassName(child, "partPrecedingTheMask");
        sb.append(preMaskText);

        appendSpaceIfRequired(sb);
        sb.append(validValues, 2, validValues.indexOf("\"]"));

        appendSpaceIfRequired(sb);
        final String postMaskText = extractTextOfElementByClassName(child, "partFollowingTheMask");
        sb.append(postMaskText);

      } else if (child.getAttribute("class").contains("masked-writing-audio-icon")) {

      } else {
        throw new UnsupportedOperationException(child.getAttribute("class"));
      }
    }
    return sb.toString();
  }

  private void appendSpaceIfRequired(final StringBuilder sb) {
    if (StringUtils.isNoneEmpty(sb.toString()) && !sb.toString().endsWith(" ") && !sb.toString().endsWith("'")) {
      sb.append(" ");
    }
  }

  private String extractTextOfElementByClassName(final WebElement element, final String className) {
    if (!element.findElements(By.className(className)).isEmpty()) {
      return element.findElement(By.className(className)).getText();
    }
    return "";
  }

  private String extractWritingQuestion(final WebElement element) {
    return element.findElement(By.className("picturestable3-extra-content"))
      .findElement(By.className("center"))
      .getText();
  }

  private String extractWritingQuestionTemplate(final WebElement element) {
    final List<WebElement> children = element.findElement(By.className("masked-writing-core"))
      .findElements(By.xpath(XPATH_CHILDREN));

    final StringBuilder sb = new StringBuilder();
    for (final WebElement child : children) {
      if (child.getAttribute("class").equals("wordsBesideMask")) {
        if (!child.getText().startsWith("'")) {
          appendSpaceIfRequired(sb);
        }
        sb.append(child.getText());
      } else if (child.getAttribute("class").equals("masked-element")) {
        appendSpaceIfRequired(sb);

        final String preMaskText = extractTextOfElementByClassName(child, "partPrecedingTheMask");
        sb.append(preMaskText);

        appendSpaceIfRequired(sb);
        sb.append("[...]");

        final String postMaskText = extractTextOfElementByClassName(child, "partFollowingTheMask");
        appendSpaceIfRequired(sb);
        sb.append(postMaskText);
      } else if (child.getAttribute("class").contains("masked-writing-audio-icon")) {

      } else {
        throw new UnsupportedOperationException(child.getAttribute("class"));
      }
    }
    return sb.toString();
  }

  protected String extractBritishSound(final WebElement element) {
    return extractSound(element, "British English");
  }

  protected String extractAmericanSound(final WebElement element) {
    return extractSound(element, "American English");
  }

  private String extractSound(final WebElement element, final String title) {
    return element.findElements(By.className("hasRecording")).stream()
      .map(it -> extractDataAudioUrlAttribute(it, title))
      .filter(StringUtils::isNoneEmpty)
      .findFirst()
      .orElse(null);
  }

  private String extractDataAudioUrlAttribute(final WebElement element, final String title) {
    if (StringUtils.defaultString(element.getAttribute("title"), "").equals(title)) {
      return BASE_URL + element.findElement(By.className("audioIcon"))
        .getAttribute("data-audio-url");
    }
    return "";
  }

}
