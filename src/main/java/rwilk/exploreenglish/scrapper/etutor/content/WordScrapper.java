package rwilk.exploreenglish.scrapper.etutor.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rwilk.exploreenglish.exception.MissingEtutorDefinitionTypeException;
import rwilk.exploreenglish.exception.MissingEtutorLanguageVarietyException;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord;
import rwilk.exploreenglish.model.entity.etutor.EtutorWord;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorWordRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class WordScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final EtutorWordRepository etutorWordRepository;

  public WordScrapper(final EtutorExerciseRepository etutorExerciseRepository,
                      final EtutorWordRepository etutorWordRepository) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.etutorWordRepository = etutorWordRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.WORDS_LIST.toString(), false)
//      .subList(0, 10)
//      .forEach(this::webScrapPicturesWordsListTypeExercise);
  }

  /**
   * ExerciseType.PICTURES_WORDS_LIST
   *
   * @param etutorExercise content
   */
  public void webScrapPicturesWordsListTypeExercise(final EtutorExercise etutorExercise, final WebDriver driver) {
//    Platform.runLater(() -> {
//      final Robot robot = new Robot();
//      robot.mouseMove(RandomUtils.nextInt(0, 2000), RandomUtils.nextInt(0, 2000));
//      robot.mouseMove(RandomUtils.nextInt(0, 2000), RandomUtils.nextInt(0, 2000));
//      robot.mouseMove(RandomUtils.nextInt(0, 2000), RandomUtils.nextInt(0, 2000));
//      robot.mouseMove(RandomUtils.nextInt(0, 2000), RandomUtils.nextInt(0, 2000));
//    });

    if (ExerciseType.PICTURES_WORDS_LIST != ExerciseType.valueOf(etutorExercise.getType())
        && ExerciseType.WORDS_LIST != ExerciseType.valueOf(etutorExercise.getType())) {
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

    // elements are grouped in div without any css class
    final List<WebElement> elements = driver.findElement(By.className("learningcontents"))
      .findElement(By.className("wordsgroup"))
      .findElements(By.tagName("div"))
      .stream()
      .filter(el -> StringUtils.isEmpty(el.getAttribute("class")))
      .toList();

    final List<EtutorWord> etutorWords = new ArrayList<>();

    for (final WebElement element : elements) {
      // get phraseEntity element -> main element of word part
      final WebElement mainTag = element.findElement(By.className("phraseEntity"));
      // and extract its children tags
      final List<WebElement> childrenTags = mainTag.findElements(By.xpath(XPATH_CHILDREN));
      // extract Polish translation
      final String polishName = extractPolishName(element);

      // crate EtutorWord base objects
      final EtutorWord etutorWord = EtutorWord.builder()
        .image(extractImage(element))
        .exercise(etutorExercise)
        .nativeTranslation(polishName)
        .definitions(new ArrayList<>())
        .html(element.getAttribute("innerHTML"))
        .build();
      // match children elements and definition parts
      for (final WebElement childrenTag : childrenTags) {
        final String cssClass = childrenTag.getAttribute("class");
        switch (cssClass) {
          case "hw" -> webScrapTranslations(childrenTag, etutorWord);
          case "synonyms" -> webScrapSynonyms(childrenTag, etutorWord);
          case "noBreakLine" -> {
          }
          case "languageRegister" -> {
            final String additionalInformation = beautify(
              StringUtils.defaultString(etutorWord.getAdditionalInformation(), "")
                .concat(", ")
                .concat(childrenTag.getText().trim()));
            etutorWord.setAdditionalInformation(additionalInformation);
            etutorWord.setNativeTranslation(beautify(etutorWord.getNativeTranslation().replace(additionalInformation, "")));
          }
          case "languageVariety" -> {
            final EtutorDefinition definition = etutorWord.getDefinitions()
              .get(etutorWord.getDefinitions().size() - 1);
            final String additionalInformation = beautify(
              StringUtils.defaultString(definition.getAdditionalInformation(), "")
                .concat(", ")
                .concat(formatLanguageVariety(childrenTag.getText()).trim())
            );
            definition.setAdditionalInformation(additionalInformation);
          }
          default -> throw new MissingEtutorDefinitionTypeException(cssClass);
        }
      }

      // get sentencesul element -> main element of sentence part
      if (!element.findElements(By.className("sentencesul")).isEmpty()) {
        final WebElement sentenceTag = element.findElement(By.className("sentencesul"));
        // and extract its children tags
        final List<WebElement> sentenceChildrenTags = sentenceTag.findElements(By.xpath(XPATH_CHILDREN));
        for (final WebElement childrenTag : sentenceChildrenTags) {
          final List<EtutorDefinition> sentenceDefinitions = extractSentenceDefinition(childrenTag, etutorWord);
          etutorWord.getDefinitions().addAll(sentenceDefinitions);
        }
      }
      etutorWords.add(etutorWord);
    }

    etutorWords.forEach(it -> it.setEtutorLessonWords(
      List.of(
        EtutorLessonWord.builder()
          .id(null)
          .position(null)
          .exercise(it.getExercise())
          .word(it)
          .build()))
    );

    etutorWordRepository.saveAll(etutorWords);
    etutorExercise.setIsReady(true);
    etutorExerciseRepository.save(etutorExercise);
  }

  private String extractPolishName(final WebElement element) {
    final String text = element.findElement(By.className("hws")).getText();
    String polishName = text.substring(text.lastIndexOf("=") + 1).trim();

    final List<WebElement> children = element.findElement(By.className("hws")).findElements(By.xpath(XPATH_CHILDREN));
    for (final WebElement child : children) {
      final String childText = child.getText();
      if (StringUtils.isNoneEmpty(childText) && polishName.contains(childText)) {
        polishName = beautify(polishName.replace(childText, "").trim());
      }
    }
    return polishName;
  }

  private void webScrapTranslations(final WebElement element, final EtutorWord etutorWord) {
    final List<EtutorDefinition> definitions = element.findElements(By.className("phraseEntityLine"))
      .stream()
      .map(this::extractDefinition)
      .flatMap(Collection::stream)
      .map(it -> {
        it.setWord(etutorWord);
        return it;
      })
      .toList();
    etutorWord.getDefinitions().addAll(definitions);
  }

  private void webScrapSynonyms(final WebElement element, final EtutorWord etutorWord) {
    final String text = element.findElement(By.className("foreignTermHeader")).getText();
    if (!text.equals("synonim:") && !text.equals("synonimy:")) {
      throw new UnsupportedOperationException(text);
    }
    final List<EtutorDefinition> definitions = extractAdditionalDefinition(element, WordTypeEnum.SYNONYM);
    definitions.forEach(it -> it.setWord(etutorWord));
    etutorWord.getDefinitions().addAll(definitions);
  }

  private List<EtutorDefinition> extractSentenceDefinition(final WebElement element, final EtutorWord etutorWord) {
    final String additionalInformation = element.findElement(By.className("sentenceTranslation")).getText().trim();
    final String englishName = element.getText().replace(additionalInformation, "").trim();

    return List.of(
      EtutorDefinition.builder()
        .additionalInformation(additionalInformation)
        .secondarySound(extractAmericanAudioIcon(element))
        .primarySound(extractBritishAudioIcon(element))
        .foreignTranslation(beautify(englishName))
        .type(WordTypeEnum.SENTENCE.toString())
        .word(etutorWord)
        .build()
    );
  }

  private String extractImage(final WebElement element) {
    if (!element.findElements(By.tagName("img")).isEmpty()) {
      final String image = element.findElement(By.tagName("img")).getAttribute("src");
      if (!image.contains("/images/icons/note-save.svg") && !image.contains("/images/icons/trash-darkGrey.svg")) {
        return image;
      }
    }
    return null;
  }

  private List<EtutorDefinition> extractDefinition(final WebElement element) {
    final String definitionType = element.getAttribute("class").replace("phraseEntityLine", "").trim();
    switch (definitionType) {
      case "" -> {
        return extractDefaultDefinition(element);
      }
      case "pluralForms" -> {
        return extractPluralFormsDefinition(element);
      }
      case "irregularForms" -> {
        return extractIrregularFormsDefinition(element);
      }
      default -> throw new UnsupportedOperationException(definitionType);
    }
  }

  private List<EtutorDefinition> extractDefaultDefinition(final WebElement element) {
    final String languageVariety = extractLanguageVariety(element);
    final String languageRegister = extractLanguageRegister(element);
    final String englishName = element.getText()
      .replace(StringUtils.defaultString(languageVariety), "")
      .replace(StringUtils.defaultString(languageRegister), "")
      .trim();
    final String additionalInformation = concatAdditionalInformation(languageVariety, languageRegister);

    return List.of(
      EtutorDefinition.builder()
        .id(null)
        .additionalInformation(additionalInformation)
        .secondarySound(extractAmericanAudioIcon(element))
        .primarySound(extractBritishAudioIcon(element))
        .foreignTranslation(beautify(englishName))
        .type(WordTypeEnum.WORD.toString())
        .word(null)
        .build()
    );
  }

  private List<EtutorDefinition> extractPluralFormsDefinition(final WebElement element) {
    return extractAdditionalDefinition(element, WordTypeEnum.PLURAL);
  }

  private List<EtutorDefinition> extractAdditionalDefinition(final WebElement element, final WordTypeEnum wordType) {
    final List<WebElement> childrenTags = element.findElements(By.xpath(XPATH_CHILDREN))
      .stream()
      .filter(el -> !el.getTagName().equals("br"))
      .toList();
    final List<EtutorDefinition> definitions = new ArrayList<>();
    EtutorDefinition definition = null;

    for (final WebElement childTag : childrenTags) {
      final String cssClass = childTag.getAttribute("class");
      switch (cssClass) {
        case "foreignTermText" -> {
          definition = EtutorDefinition.builder()
            .foreignTranslation(beautify(childTag.getText().trim()))
            .type(wordType.toString())
            .build();
          definitions.add(definition);
        }
        case "recordingsAndTranscriptions" -> {
          if (definition != null) {
            definition.setSecondarySound(extractAmericanAudioIcon(childTag));
            definition.setPrimarySound(extractBritishAudioIcon(childTag));
          } else {
            throw new UnsupportedOperationException("recordingsAndTranscriptions on null definition");
          }
        }
        case "foreignTermHeader" -> {
          if (definition != null && childTag.getText().equals("stopień wyższy")) {
            definition.setType(WordTypeEnum.COMPARATIVE.toString());

          } else if (definition != null && childTag.getText().equals("stopień najwyższy")) {
            definition.setType(WordTypeEnum.SUPERLATIVE.toString());

          } else if (childTag.getText().equals("synonim:") || childTag.getText().equals("synonimy:")) {
            // do nothing because definition is null and WordTypeEnum.SYNONYM is passed as param

          } else if (definition != null && childTag.getText().equals("past tense")) {
            definition.setType(WordTypeEnum.PAST_TENSE.toString());

          } else if (definition != null && childTag.getText().contains("past tense")) {
            final String text = childTag.getText();
            final String additional = formatLanguageVariety(text.substring(
              text.indexOf("(") + 1,
              text.indexOf(")")
            ).trim());
            definition.setType(WordTypeEnum.PAST_TENSE.toString());
            definition.setAdditionalInformation(additional);

          } else if (definition != null && childTag.getText().equals("past participle")) {
            definition.setType(WordTypeEnum.PAST_PARTICIPLE.toString());

          } else if (definition != null && childTag.getText().contains("past participle")) {
            final String text = childTag.getText();
            final String additional = formatLanguageVariety(text.substring(
              text.indexOf("(") + 1,
              text.indexOf(")")
            ).trim());
            definition.setType(WordTypeEnum.PAST_PARTICIPLE.toString());
            definition.setAdditionalInformation(additional);

          } else if (definition != null && childTag.getText().equals("liczba mnoga")) {
            definition.setType(WordTypeEnum.PLURAL.toString());

          } else if (definition != null && childTag.getText().equals("present participle")) {
            definition.setType(WordTypeEnum.PRESENT_PARTICIPLE.toString());

          } else {
            throw new UnsupportedOperationException(childTag.getText());
          }

        }
        default -> throw new UnsupportedOperationException(cssClass);
      }
    }
    return definitions;
  }

  private List<EtutorDefinition> extractIrregularFormsDefinition(final WebElement element) {
    return extractAdditionalDefinition(element, WordTypeEnum.COMPARATIVE);
  }

  private String extractLanguageVariety(final WebElement element) {
    if (!element.findElements(By.className("languageVariety")).isEmpty()) {
      return element.findElement(By.className("languageVariety")).getText();
    }
    return null;
  }

  private String extractLanguageRegister(final WebElement element) {
    if (!element.findElements(By.className("languageRegister")).isEmpty()) {
      return element.findElement(By.className("languageRegister")).getText();
    }
    return null;
  }

  private String concatAdditionalInformation(final String languageVariety, final String languageRegister) {
    if (StringUtils.isNoneEmpty(languageVariety, languageRegister)) {
      return formatLanguageVariety(languageVariety)
        .concat(", ")
        .concat(languageRegister.trim());
    } else if (StringUtils.isAllEmpty(languageVariety, languageRegister)) {
      return "";
    } else if (StringUtils.isEmpty(languageRegister)) {
      return formatLanguageVariety(languageVariety);
    } else {
      return languageRegister.trim();
    }
  }

  private String formatLanguageVariety(final String languageVariety) {
    return switch (languageVariety) {
      case "BrE" -> "British English";
      case "AmE" -> "American English";
      case "CanE" -> "Canadian English";
      case "AusE" -> "Australian English";
      case "NZE" -> "New Zealand English";
      case "ScoE" -> "Scottish English";
      case "IrlE" -> "Irish English";
      case "SAE" -> "South African English";
      case "latin" -> "latin ";
      case "AAVE" -> "African American Vernacular English";
      case "InE" -> "Indian English";
      case "Italiano standard" -> "Italian standard";
      default -> throw new MissingEtutorLanguageVarietyException(languageVariety);
    };
  }

  public static String beautify(final String englishName) {
    if (englishName.trim().endsWith(",")) {
      return englishName.trim().substring(0, englishName.length() - 1).trim();
    }
    if (englishName.trim().startsWith(",")) {
      return englishName.trim().substring(1, englishName.length()).trim();
    }
    return englishName;
  }

}
