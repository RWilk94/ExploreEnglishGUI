package rwilk.exploreenglish.scrapper.etutor.content;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseItemType;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class EtutorExerciseItemScrapper implements CommandLineRunner {

  private static final String BASE_URL = "https://www.etutor.pl";
  private static final String XPATH_CHILDREN = "./child::*";
  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorExerciseItemRepository etutorExerciseItemRepository;

  public EtutorExerciseItemScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                                    final EtutorExerciseItemRepository etutorExerciseItemRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorExerciseItemRepository = etutorExerciseItemRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.EXERCISE.toString(), false)
      .subList(0, 10)
      .forEach(this::webScrapExerciseTypeExercise);
  }

  public void webScrapExerciseTypeExercise(final EtutorExercise etutorExercise) {
    if (ExerciseType.EXERCISE != ExerciseType.valueOf(etutorExercise.getType())) {
      return;
    }
    System.setProperty("webdriver.chrome.driver", "C:\\Corelogic\\TAX\\ExploreEnglishGUI\\chrome_driver\\chromedriver.exe");

    final WebDriver driver = new ChromeDriver();
    final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    final Cookie cookie = new Cookie("autoLoginToken", "BKygYBbhlF7YeJDEJu6wr3peLRtKg3UjZsGNTDHQ");

    driver.get(BASE_URL);
    driver.manage().addCookie(cookie);

    // open course
    driver.get(etutorExercise.getHref());
//    driver.get("https://www.etutor.pl/lessons/en/a1/12/2/39108"); // TODO remove hardcoded
    // and wait for display list of lessons
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));

    final String instruction = extractExerciseInstruction(driver.findElement(By.className("exercise")));

    final List<WebElement> exerciseQuestions = driver.findElements(By.className("exercise-numbered-question"));

    final List<EtutorExerciseItem> exerciseItems = new ArrayList<>();
    exerciseQuestions.forEach(element -> {
      switch (driver.findElement(By.className("exercise")).getAttribute("data-exercise")) {
        case "choice" -> {
          exerciseItems.add(webScrapChoiceExercise(etutorExercise, element, instruction));

          if (!driver.findElements(By.id("nextQuestionButton")).isEmpty()
              && driver.findElement(By.id("nextQuestionButton")).isDisplayed()) {
            driver.findElement(By.id("nextQuestionButton")).click();
          }
        }
        case "masked-writing" -> {
          exerciseItems.add(webScrapMaskedWritingExercise(etutorExercise, element, instruction));

          if (!driver.findElements(By.id("nextQuestionButton")).isEmpty()
              && driver.findElement(By.id("nextQuestionButton")).isDisplayed()) {
            driver.findElement(By.id("nextQuestionButton")).click();
          }
        }
        case "cloze" -> {
          // TODO
        }
      }
    });

    etutorExerciseItemRepository.saveAll(exerciseItems);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
    driver.quit();
  }


  private EtutorExerciseItem webScrapChoiceExercise(final EtutorExercise etutorExercise, final WebElement element,
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
      .questionAmericanSound(extractAmericanVoiceQuestion(element))
      .questionBritishSound(extractBritishVoiceQuestion(element))
      .finalAnswer(extractFinalAnswer(element))
      .translation(extractTranslation(element))
      .description(extractDescription(element))
      .answerAmericanSound(extractVoiceAnswer(element, "/en-ame/"))
      .answerBritishSound(extractVoiceAnswer(element, "/en/"))
      .html(element.getAttribute("innerHTML"))
      .type(ExerciseItemType.CHOICE.toString())
      .exercise(etutorExercise)
      .build();

    validateExerciseItem(exerciseItem);
    return exerciseItem;
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

  private String extractExerciseInstruction(final WebElement element) {
    if (!element.findElements(By.className("exerciseinstruction")).isEmpty()) {
      return element.findElement(By.className("exerciseinstruction")).getText().trim();
    }
    return "";
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

  private String extractBritishVoiceQuestion(final WebElement element) {
    final List<WebElement> audioIconButtons = element.findElement(By.className("examChoiceQuestion"))
      .findElements(By.className("audioIconButton"));

    if (!audioIconButtons.isEmpty()) {
      for (WebElement audioIconButton : audioIconButtons) {
        final String dataAudioUrl = audioIconButton.getAttribute("data-audio-url");
        if (dataAudioUrl.contains("/en/")) {
          return BASE_URL + dataAudioUrl;
        }
      }
    }
    return "";
  }

  private String extractAmericanVoiceQuestion(final WebElement element) {
    final List<WebElement> audioIconButtons = element.findElement(By.className("examChoiceQuestion"))
      .findElements(By.className("audioIconButton"));

    if (!audioIconButtons.isEmpty()) {
      for (WebElement audioIconButton : audioIconButtons) {
        final String dataAudioUrl = audioIconButton.getAttribute("data-audio-url");
        if (dataAudioUrl.contains("/en-ame/")) {
          return BASE_URL + dataAudioUrl;
        }
      }
    }
    return "";
  }

  private String extractVoiceAnswer(final WebElement element, final String language) {
    final List<WebElement> audioIconButtons = element.findElement(By.className("examChoiceQuestion"))
      .findElements(By.className("icon-sound"));

    if (!audioIconButtons.isEmpty()) {
      for (WebElement audioIconButton : audioIconButtons) {
        final String dataAudioUrl = audioIconButton.getAttribute("data-audio-url");
        if (dataAudioUrl.contains(language)) {
          return BASE_URL + dataAudioUrl;
        }
      }
    }
    return "";
  }

  private String extractFinalAnswer(final WebElement element) {
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

  private EtutorExerciseItem webScrapMaskedWritingExercise(final EtutorExercise etutorExercise,
                                                           final WebElement element,
                                                           final String instruction) {

    final List<String> possibleAnswers = extractPossibleAnswers(element);
    if (possibleAnswers.size() > 4) {
      throw new UnsupportedOperationException("possibleAnswers contains more then 4 items");
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
      .questionBritishSound(extractWritingBritishVoiceQuestion(element))
      .questionAmericanSound(extractWritingAmericanVoiceQuestion(element))
      .translation(extractWritingQuestion(element))
      .html(element.getAttribute("innerHTML"))
      .type(ExerciseItemType.WRITING.toString())
      .exercise(etutorExercise)
      .build();

    final WebElement suggestNextLetterButton = element.findElement(By.xpath("../parent::*"))
      .findElement(By.id("suggestNextLetterButton"));
    while (suggestNextLetterButton.isDisplayed() && suggestNextLetterButton.isEnabled()) {
      suggestNextLetterButton.click();
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    final String finalAnswer = element.findElement(By.className("masked-writing-core")).getText()
      .replace("\n", " ")
      .replace(" .", ".")
      .replace("' ", "'")
      .replace(" '", "'");
    final String britishSound = extractBritishSound(element);
    final String americanSound = extractAmericanSound(element);
    final String description = extractWritingDescription(element);

    exerciseItem.setFinalAnswer(finalAnswer);
    exerciseItem.setAnswerBritishSound(britishSound);
    exerciseItem.setAnswerAmericanSound(americanSound);
    exerciseItem.setDescription(description);

    return exerciseItem;
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

  private String extractWritingQuestion(final WebElement element) {
    return element.findElement(By.className("masked-writing-command")).getText();
  }

  private String extractWritingDescription(final WebElement element) {
    return extractTextOfElementByClassName(element, "masked-writing-explonation");
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

  private String extractBritishSound(final WebElement element) {
    return extractSound(element, "British English");
  }

  private String extractAmericanSound(final WebElement element) {
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

  private String extractWritingBritishVoiceQuestion(final WebElement element) {
    final List<WebElement> audioIconButtons = element.findElements(By.className("audioIconButton"));

    if (!audioIconButtons.isEmpty()) {
      for (WebElement audioIconButton : audioIconButtons) {
        final String dataAudioUrl = audioIconButton.getAttribute("data-audio-url");
        if (dataAudioUrl.contains("/en/")) {
          return BASE_URL + dataAudioUrl;
        }
      }
    }
    return "";
  }

  private String extractWritingAmericanVoiceQuestion(final WebElement element) {
    final List<WebElement> audioIconButtons = element.findElements(By.className("audioIconButton"));

    if (!audioIconButtons.isEmpty()) {
      for (WebElement audioIconButton : audioIconButtons) {
        final String dataAudioUrl = audioIconButton.getAttribute("data-audio-url");
        if (dataAudioUrl.contains("/en-ame/")) {
          return BASE_URL + dataAudioUrl;
        }
      }
    }
    return "";
  }

}
