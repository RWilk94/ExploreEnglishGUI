package rwilk.exploreenglish.scrapper.etutor.content.exercise;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;

import java.util.List;

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
    final String correctAnswer = element.findElement(By.className("examClozeInput")).getAttribute("data-correct-answer");;
    return extractWritingQuestionTemplate(element).replace("[...]", correctAnswer);
  }

  private String extractWritingQuestion(final WebElement element) {
    return element.findElement(By.className("examClozeCommand")).getText();
  }

  private String extractWritingQuestionTemplate(final WebElement element) {
    final WebElement webElement = element.findElement(By.className("examClozeFillableSentence"));
    final Document document = Jsoup.parse(webElement.getAttribute("innerHTML"));

    final StringBuilder questionTemplate = new StringBuilder();

    for (Node child : document.childNodes()) {
      printLeafNodesRecursive(child, questionTemplate);
    }

    final String hint = extractWritingQuestionHint(element);

    return questionTemplate.toString().trim()
            .concat(StringUtils.isNoneEmpty(hint) ? hint : "");
  }

  // TODO webscrap all cloze exercises again after this method is implemented
  private String extractWritingQuestionHint(final WebElement element) {
    final WebElement webElement = element.findElement(By.className("examClozeHint"));
    final Document document = Jsoup.parse(webElement.getAttribute("innerHTML"));

    final StringBuilder questionHint = new StringBuilder();

    for (Node child : document.childNodes()) {
      printLeafNodesRecursive(child, questionHint);
    }
    return questionHint.toString().trim();
  }

  private void printLeafNodesRecursive(final Node node, final StringBuilder stringBuilder) {
    if (node.childNodes().isEmpty()) {
      if (node instanceof final TextNode textNode) {
        final String text = textNode.text();
        stringBuilder.append(" ").append(text.trim());
      } else if (node instanceof final Element element) {
        if (element.tagName().equals("input")) {
          stringBuilder.append(" ").append("[...]");
        }
      }
    } else {
      for (Node child : node.childNodes()) {
        printLeafNodesRecursive(child, stringBuilder);
      }
    }
  }

}
