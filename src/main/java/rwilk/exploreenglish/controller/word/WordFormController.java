package rwilk.exploreenglish.controller.word;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import static rwilk.exploreenglish.exception.ExceptionControllerAdvice.NOT_FOUND_WORD_INSTANCE;
import static rwilk.exploreenglish.exception.ExceptionControllerAdvice.NOT_FOUND_WORD_SOUND_INSTANCE;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import rwilk.exploreenglish.custom.ToggleGroup2;
import rwilk.exploreenglish.exception.RequiredFieldsAreEmptyException;
import rwilk.exploreenglish.exception.RequiredObjectNotFoundException;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.Definition;
import rwilk.exploreenglish.model.entity.release.ReleaseSentence;
import rwilk.exploreenglish.model.entity.release.ReleaseWord;
import rwilk.exploreenglish.repository.LessonRepository;
import rwilk.exploreenglish.repository.LessonWordRepository;
import rwilk.exploreenglish.repository.WordRepository;
import rwilk.exploreenglish.repository.DefinitionRepository;
import rwilk.exploreenglish.repository.release.ReleaseSentenceRepository;
import rwilk.exploreenglish.repository.release.ReleaseWordRepository;
import rwilk.exploreenglish.scrapper.longman.LongmanScrapper;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.WordService;
import rwilk.exploreenglish.service.DefinitionService;
import rwilk.exploreenglish.utils.FormUtils;
import rwilk.exploreenglish.utils.SoundUtils;
import rwilk.exploreenglish.utils.WordUtils;

@Slf4j
@Controller
public class WordFormController implements Initializable, CommandLineRunner {

  private WordController wordController;
  @Autowired
  private LongmanScrapper longmanScrapper;
  private final List<Object> controls = new ArrayList<>();

  @FXML private TextField textFieldId;
  @Getter @FXML private ComboBox<Lesson> comboBoxLesson;
  @FXML private ListView<Lesson> listViewLessons;
  @FXML private TextField textFieldEnglishNames;
  @Getter @FXML private TextField textFieldWordSoundId;
  @Getter @FXML private TextField  textFieldEnglishName;
  @Getter @FXML private TextField  textFieldAdditionalInformation;
  @Getter @FXML private ComboBox<WordTypeEnum> comboBoxWordType;
  @Getter @FXML private TextField  textFieldAmericanSound;
  @Getter @FXML private TextField textFieldBritishSound;
  @FXML private ListView<Definition> listViewWordVariants;
  @Getter @FXML private TextField textFieldPolishName;
  @FXML private ToggleButton toggleButtonNoun;
  @FXML private ToggleButton toggleButtonVerb;
  @FXML private ToggleButton toggleButtonAdjective;
  @FXML private ToggleButton toggleButtonAdverb;
  @FXML private ToggleButton toggleButtonPhrasalVerb;
  @FXML private ToggleButton toggleButtonSentence;
  @FXML private ToggleButton toggleButtonIdiom;
  @FXML private ToggleButton toggleButtonNumber;
  @FXML private ToggleButton toggleButtonPronoun;
  @FXML private ToggleButton toggleButtonDeterminer;
  @FXML private ToggleButton toggleButtonOther;
  @FXML private ToggleButton toggleButtonA;
  @FXML private ToggleButton toggleButtonAn;
  @FXML private ToggleButton toggleButtonThe;
  @FXML private ToggleButton toggleButtonA1;
  @FXML private ToggleButton toggleButtonA2;
  @FXML private ToggleButton toggleButtonB1;
  @FXML private ToggleButton toggleButtonB2;
  @FXML private ToggleButton toggleButtonC1;
  @FXML private ToggleButton toggleButtonC2;
  @FXML private ToggleButton toggleButtonNone;
  @FXML private ToggleButton toggleButtonCountable;
  @FXML private ToggleButton toggleButtonUncountable;
  @FXML private ToggleButton toggleButtonCountableAndUncountable;
  @FXML private ToggleButton toggleButtonPlural;
  @FXML private ToggleButton toggleButtonEmpty;
  @Getter private ToggleGroup2 toggleGroupPartOfSpeech;
  @Getter private ToggleGroup2 toggleGroupArticle;
  private ToggleGroup2 toggleGroupLevel;
  @Getter private ToggleGroup2 toggleGroupGrammar;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setToggleGroups();
    controls.addAll(Arrays.asList(textFieldId, textFieldEnglishNames, textFieldWordSoundId, textFieldEnglishName,
                                  textFieldAdditionalInformation, comboBoxWordType, textFieldAmericanSound, listViewWordVariants,
                                  textFieldBritishSound, textFieldPolishName, toggleGroupPartOfSpeech, toggleGroupArticle,
                                  toggleGroupLevel, toggleGroupGrammar, comboBoxLesson, listViewLessons));

    listViewWordVariants.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(Definition item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && WordTypeEnum.WORD == WordTypeEnum.valueOf(item.getType())) {
          this.setText(item.toString());
          if (!wordController.getWordService().getAllByEnglishNames(item.getEnglishName()).isEmpty()) {
            setStyle("-fx-background-color: #ff0000");
          } else if (!wordController.getWordService().getAllByEnglishNamesLike(item.getEnglishName()).isEmpty()) {
            setStyle("-fx-background-color: #ffa200");
          } else {
            setStyle("");
          }
        } if (item != null) {
          this.setText(item.toString());
          setStyle("");
        } else {
          this.setText("");
          setStyle("");
        }
      }
    });

    textFieldEnglishNames.textProperty()
        .addListener((observable, oldValue, newValue) ->
                         wordController.getWordTableController().textFieldFilterByEnName.setText(newValue));

//    textFieldPolishName.textProperty().addListener((observable, oldValue, newValue) ->
//        wordController.getWordTableController().textFieldFilterByPlName.setText(newValue));
  }

  public void init(WordController wordController) {
    this.wordController = wordController;
    initializeLessonComboBox();
    initializeWordTypeComboBox();
    setLessonWordForm();
  }

  public void buttonDeleteOnAction() {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      wordController.getWordService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(word -> wordController.getWordService().delete(word));
      buttonClearOnAction();
      wordController.refreshTableView();
      wordController.refreshChildTableView();
    }
  }

  public void buttonClearOnAction() {
    FormUtils.clear(controls);
  }

  public void buttonEditOnAction() {
    wordController.getWordService().getById(Long.valueOf(textFieldId.getText()))
        .ifPresent(word -> {
          word.setDefinitions(listViewWordVariants.getItems().stream()
                                                  .filter(Objects::nonNull)
                                                  .toList());
          word.setPolishName(trimToEmpty(textFieldPolishName.getText()));
          word.setPartOfSpeech(trimToEmpty(getSelectedToggleText(toggleGroupPartOfSpeech)));
          word.setArticle(trimToEmpty(getSelectedToggleText(toggleGroupArticle)));
          word.setLevel(trimToEmpty(getSelectedToggleText(toggleGroupLevel)));
          word.setGrammarType(trimToEmpty(getSelectedToggleText(toggleGroupGrammar)));

          word = wordController.getWordService().save(word);

          setWordForm(word);
          wordController.refreshTableView();
          wordController.refreshChildComboBoxes();
        });
  }

  public void buttonAddOnAction() {
      Word word = Word.builder()
          .id(null)
          .definitions(ListUtils.emptyIfNull(listViewWordVariants.getItems()).stream()
                                    .filter(Objects::nonNull)
                                    .toList())
          .polishName(trimToEmpty(textFieldPolishName.getText()))
          .partOfSpeech(trimToEmpty(getSelectedToggleText(toggleGroupPartOfSpeech)))
          .article(trimToEmpty(getSelectedToggleText(toggleGroupArticle)))
          .level(trimToEmpty(getSelectedToggleText(toggleGroupLevel)))
          .grammarType(trimToEmpty(getSelectedToggleText(toggleGroupGrammar)))
          .build();
      word = wordController.getWordService().save(word);
      setWordForm(word);

      if (comboBoxLesson.getSelectionModel().getSelectedItem() != null) {
        final LessonWord lessonWord = LessonWord.builder()
            .id(null)
            .lesson(comboBoxLesson.getSelectionModel().getSelectedItem())
            .position(wordController.getLessonWordService().getCountByLesson(comboBoxLesson.getSelectionModel().getSelectedItem()))
            .word(word)
            .build();
        wordController.getLessonWordService().save(lessonWord);
        setLessonWordForm();
      }

      wordController.refreshTableView();
      wordController.refreshChildComboBoxes();
  }

  public void setWordForm(final Word word) {
    textFieldId.setText(trimToEmpty(word.getId().toString()));
    setLessonWordForm();
    setWordForm(word.getPolishName(), word.getDefinitions());

    toggleGroupPartOfSpeech.selectToggle(toggleGroupPartOfSpeech.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(trimToEmpty(word.getPartOfSpeech())))
        .findFirst()
        .orElse(null));
    toggleGroupArticle.selectToggle(toggleGroupArticle.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(trimToEmpty(word.getArticle())))
        .findFirst()
        .orElse(null));
    toggleGroupLevel.selectToggle(toggleGroupLevel.getToggles().stream()
                                                      .filter(toggle -> toggle.getUserData().toString().equals(
                                                          trimToEmpty(word.getLevel())))
                                                      .findFirst()
                                                      .orElse(null));
    toggleGroupGrammar.selectToggle(toggleGroupGrammar.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(trimToEmpty(word.getGrammarType())))
        .findFirst()
        .orElse(null));
  }

  public void setWordForm(final Term term) {
    textFieldId.clear();

    toggleGroupPartOfSpeech.selectToggle(toggleGroupPartOfSpeech.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(trimToEmpty(term.getPartOfSpeech())))
        .findFirst()
        .orElse(null));
    toggleGroupArticle.selectToggle(null);
    toggleGroupLevel.selectToggle(null);
    toggleGroupGrammar.selectToggle(null);

    String englishNames = (StringUtils.isNoneEmpty(term.getEnglishName()) ? term.getEnglishName() + "; " : "")
        .concat((StringUtils.isNoneEmpty(term.getAmericanName()) ? term.getAmericanName() + "; " : ""))
        .concat((StringUtils.isNoneEmpty(term.getOtherName()) ? term.getOtherName() + "; " : ""))
        .trim();
    englishNames = englishNames.endsWith(";")
        ? englishNames.substring(0, englishNames.length() - 1)
        : englishNames;
    if (englishNames.startsWith("a ")) {
      toggleGroupArticle.selectToggle(toggleButtonA);
      toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
      toggleGroupGrammar.selectToggle(toggleButtonCountable);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    } else if (englishNames.startsWith("an ")) {
      toggleGroupArticle.selectToggle(toggleButtonAn);
      toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
      toggleGroupGrammar.selectToggle(toggleButtonCountable);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    } else if (englishNames.startsWith("the ")) {
      toggleGroupArticle.selectToggle(toggleButtonThe);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    } else if (englishNames.startsWith("to ")) {
      toggleGroupPartOfSpeech.selectToggle(toggleButtonVerb);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    }

    final List<Definition> definitions = new ArrayList<>(wordSoundFromTerm(WordTypeEnum.WORD, englishNames));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.COMPARATIVE, term.getComparative())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SUPERLATIVE, term.getSuperlative())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_TENSE, term.getPastTense())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_PARTICIPLE, term.getPastParticiple())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PLURAL, term.getPlural())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SYNONYM, term.getSynonym())));

    listViewLessons.setItems(null);
    setWordForm(term.getPolishName(), definitions);
  }

  private List<Definition> wordSoundFromTerm(final WordTypeEnum type, final String value) {
    if (StringUtils.isBlank(value)) {
      return List.of();
    }

    return Arrays.stream(value.split(";"))
        .map(StringUtils::trim)
        .map(text -> {
          String enName = WordUtils.replaceSpecialText(text);
          String additional = "";
          if (enName.contains("(") && enName.contains(")")) {
            additional = enName.substring(enName.indexOf("(") + 1, enName.indexOf(")"));
            enName = enName.substring(0, enName.indexOf("("));
          }
          return Definition.builder()
                           .englishName(enName)
                           .additionalInformation(additional)
                           .type(type.toString())
                           .build();
        })
        .toList();
  }

  private List<Definition> wordSoundFromTerm(final WordTypeEnum type, final String value, final String sound) {
    if (StringUtils.isBlank(value)) {
      return List.of();
    }

    return Arrays.stream(value.split(";"))
                 .map(StringUtils::trim)
                 .map(text -> {
                   String enName = WordUtils.replaceSpecialText(text);
                   String additional = "";
                   if (enName.contains("(") && enName.contains(")")) {
                     additional = enName.substring(enName.indexOf("(") + 1, enName.indexOf(")"));
                     enName = enName.substring(0, enName.indexOf("("));
                   }
                   return Definition.builder()
                                    .englishName(enName)
                                    .additionalInformation(additional)
                                    .type(type.toString())
                                    .britishSound(sound)
                                    .build();
                 })
                 .toList();
  }

  private void setWordForm(final String polishName, final List<Definition> definitions) {
    textFieldPolishName.setText(trimToEmpty(polishName));
    listViewWordVariants.setItems(FXCollections.observableArrayList(definitions));
    if (!CollectionUtils.isEmpty(definitions)) {
      definitions.stream()
                 .filter(wordSound -> wordSound.getType().equals(WordTypeEnum.WORD.toString()))
                 .findFirst()
                 .ifPresent(this::setWordSoundForm);
    } else {
      buttonClearWordSoundOnAction();
    }
  }

  public void initializeLessonComboBox() {
    new Thread(() -> {
      List<Lesson> lessons = wordController.getLessonService().getAll();
      Platform.runLater(() -> comboBoxLesson.setItems(FXCollections.observableArrayList(lessons)));
    }).start();
  }

  public void initializeWordTypeComboBox() {
    new Thread(() -> {
      List<WordTypeEnum> wordTypes = List.of(WordTypeEnum.values());
      Platform.runLater(() -> comboBoxWordType.setItems(FXCollections.observableArrayList(wordTypes)));
    }).start();
  }

  public void buttonTranslateOnAction() {
    translate(true);
  }

  public void translate(final boolean changeTab) {
    final String textToTranslate = StringUtils.defaultIfBlank(trimToEmpty(textFieldEnglishNames.getText()), trimToEmpty(textFieldEnglishName.getText()));
    if (StringUtils.isNoneBlank(textToTranslate)) {
      wordController.getInjectService().getScrapperController()
          .webScrap(Arrays.stream(textToTranslate.split(";")).toList(), changeTab);
      if (changeTab) {
        wordController.getTabPane().getSelectionModel().select(1);
      }
    }
  }

  public void buttonAddLessonOnAction() {
    if (comboBoxLesson.getSelectionModel().getSelectedItem() != null
        && StringUtils.isNoneEmpty(textFieldId.getText())) {
      final Lesson lesson = comboBoxLesson.getSelectionModel().getSelectedItem();

      final Word word = getWordById();
      wordController.getLessonWordService().save(
          LessonWord.builder()
              .word(word)
              .lesson(lesson)
              .position(wordController.getLessonWordService().getCountByLesson(comboBoxLesson.getSelectionModel().getSelectedItem()))
              .build());
      setLessonWordForm();
      wordController.refreshTableView();
      wordController.refreshChildComboBoxes();
    }
  }

  public void buttonRemoveLessonOnAction() {
    if (comboBoxLesson.getSelectionModel().getSelectedItem() != null && StringUtils.isNoneEmpty(textFieldId.getText())) {
      final Lesson lesson = comboBoxLesson.getSelectionModel().getSelectedItem();
      final Long wordId = Long.parseLong(textFieldId.getText());

      wordController.getLessonWordService().getByLessonIdAndWordId(lesson.getId(), wordId)
          .ifPresent(lessonWord -> wordController.getLessonWordService().deleteById(lessonWord.getId()));
      setLessonWordForm();
      wordController.refreshTableView();
      wordController.refreshChildComboBoxes();
    }
  }

  public void listViewLessonsOnMouseClicked() {
    final Lesson selectedItem = listViewLessons.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      comboBoxLesson.getSelectionModel().select(selectedItem);
    }
  }

  public void listViewWordVariantsOnMouseClicked() {
    final Definition selectedItem = listViewWordVariants.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      setWordSoundForm(selectedItem);
    }
  }

  public void buttonAmePlaySoundOnAction() {
    SoundUtils.playSound(textFieldAmericanSound);
  }

  public void buttonBrePlaySoundOnAction() {
    SoundUtils.playSound(textFieldBritishSound);
  }

  public void buttonClearWordSoundOnAction() {
    textFieldWordSoundId.clear();
    textFieldEnglishName.clear();
    textFieldAdditionalInformation.clear();
    textFieldAmericanSound.clear();
    textFieldBritishSound.clear();
    comboBoxWordType.getSelectionModel().select(null);
  }

  public void buttonDeleteWordSoundOnAction() {
    if (isBlank(textFieldWordSoundId.getText())) {
      throw new RequiredFieldsAreEmptyException("Cannot delete WORD_SOUND due to empty required fields.");
    }
    final Definition definition = getWordSoundById();

    wordController.getDefinitionService().delete(definition);

    refreshListViewWordVariants(definition.getWord().getId());
    wordController.refreshTableView();
    wordController.refreshChildComboBoxes();
  }

  public void buttonEditWordSoundOnAction() {
    if (isAnyBlank(textFieldEnglishName.getText(), textFieldWordSoundId.getText())
        || comboBoxWordType.getSelectionModel().getSelectedItem() == null) {
      throw new RequiredFieldsAreEmptyException("Cannot edit WORD_SOUND due to empty required fields.");
    }

    final Definition definition = getWordSoundById();
    definition.setEnglishName(trimToEmpty(textFieldEnglishName.getText()));
    definition.setAdditionalInformation(trimToEmpty(textFieldAdditionalInformation.getText()));
    definition.setType(comboBoxWordType.getSelectionModel().getSelectedItem().toString());
    definition.setAmericanSound(trimToEmpty(textFieldAmericanSound.getText()));
    definition.setBritishSound(trimToEmpty(textFieldBritishSound.getText()));

    final Definition updated = wordController.getDefinitionService().save(definition);

    setWordSoundForm(updated);
    refreshListViewWordVariants(updated.getWord().getId());
    wordController.refreshTableView();
    wordController.refreshChildComboBoxes();
  }

  public void buttonAddWordSoundOnAction() {
    if (isBlank(textFieldEnglishName.getText())
        || comboBoxWordType.getSelectionModel().getSelectedItem() == null) {
      throw new RequiredFieldsAreEmptyException("Cannot add WORD_SOUND due to empty required fields.");
    } else if (isBlank(textFieldId.getText())) {
      buttonAddOnAction();
    }
    final List<Definition> definitions = ListUtils.emptyIfNull(listViewWordVariants.getItems());

    final Word word = getWordById();
    final Definition definition = Definition.builder()
                                            .id(null)
                                            .englishName(trimToEmpty(textFieldEnglishName.getText()))
                                            .additionalInformation(trimToEmpty(textFieldAdditionalInformation.getText()))
                                            .type(comboBoxWordType.getSelectionModel().getSelectedItem().toString())
                                            .americanSound(trimToEmpty(textFieldAmericanSound.getText()))
                                            .britishSound(trimToEmpty(textFieldBritishSound.getText()))
                                            .word(word)
                                            .build();

    final Definition updated = wordController.getDefinitionService().save(definition);

    setWordSoundForm(updated);
    refreshListViewWordVariants(updated.getWord().getId());

    // add WordSounds removed by refresh
    final List<Definition> actualDefinitions = ListUtils.emptyIfNull(listViewWordVariants.getItems());
    if (definitions.size() > actualDefinitions.size()) {
      definitions.forEach(ws -> {
        if (actualDefinitions.stream().noneMatch(aws -> aws.getEnglishName().equals(ws.getEnglishName()) && aws.getType().equals(ws.getType()))) {
          listViewWordVariants.getItems().add(ws);
        }
      });
    }
    wordController.refreshTableView();
    wordController.refreshChildComboBoxes();
  }

  public void toggleButtonAOnAction() {
    toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
    toggleGroupGrammar.selectToggle(toggleButtonCountable);
  }

  public void toggleButtonPluralOnAction() {
    toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
    toggleGroupArticle.selectToggle(toggleButtonNone);
  }

  public void toggleButtonPartOfSpeechOnMouseClicked() {
    if (toggleGroupArticle.getSelectedToggle() == null && toggleGroupGrammar.getSelectedToggle() == null
        || toggleGroupPartOfSpeech.getSelectedToggle() != toggleButtonNoun) {
      toggleGroupArticle.selectToggle(toggleButtonNone);
      toggleGroupGrammar.selectToggle(toggleButtonEmpty);
    }
  }

  public void toggleButtonCountableAndUncountableOnAction() {
    if (toggleGroupPartOfSpeech.getSelectedToggle() == null && toggleGroupArticle.getSelectedToggle() == null) {
      toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
      toggleGroupArticle.selectToggle(toggleButtonNone);
    }
  }

  private Word getWordById() {
    return wordController.getWordService().getById(Long.valueOf(textFieldId.getText()))
        .orElseThrow(() -> new RequiredObjectNotFoundException(NOT_FOUND_WORD_INSTANCE));
  }

  private Definition getWordSoundById() {
    return wordController.getDefinitionService().getById(Long.valueOf(textFieldWordSoundId.getText()))
                         .orElseThrow(() -> new RequiredObjectNotFoundException(NOT_FOUND_WORD_SOUND_INSTANCE));
  }

  private void refreshListViewWordVariants(final Long wordId) {
    final List<Definition> definitions = wordController.getDefinitionService()
                                                       .getAllByWordId(wordId);
    listViewWordVariants.setItems(FXCollections.observableArrayList(definitions));
  }

  private void setWordSoundForm(final Definition definition) {
    textFieldWordSoundId.setText(String.valueOf(definition.getId()));
    textFieldEnglishName.setText(definition.getEnglishName());
    textFieldAdditionalInformation.setText(definition.getAdditionalInformation());
    comboBoxWordType.getSelectionModel().select(WordTypeEnum.valueOf(definition.getType()));
    textFieldAmericanSound.setText(definition.getAmericanSound());
    textFieldBritishSound.setText(definition.getBritishSound());
  }

  private void setLessonWordForm() {
    if (StringUtils.isNotBlank(textFieldId.getText())) {
      final Word word = wordController.getWordService().getById(Long.parseLong(textFieldId.getText()))
          .orElseThrow(() -> new RequiredObjectNotFoundException(NOT_FOUND_WORD_INSTANCE));
      List<LessonWord> lessonWords = wordController.getLessonWordService().getAllByWord(word);
      listViewLessons.setItems(null);
      listViewLessons.setItems(FXCollections.observableArrayList(lessonWords.stream()
                                                                     .map(LessonWord::getLesson)
                                                                     .toList()));
      refreshListViewWordVariants(word.getId());
    }
  }

  private void setToggleGroups() {
    toggleGroupPartOfSpeech = new ToggleGroup2("toggleGroupPartOfSpeech");
    setToggleGroup(List.of(toggleButtonNoun, toggleButtonVerb, toggleButtonAdjective, toggleButtonAdverb,
                           toggleButtonPhrasalVerb, toggleButtonSentence, toggleButtonIdiom, toggleButtonNumber,
                           toggleButtonPronoun, toggleButtonDeterminer, toggleButtonOther),
                   toggleGroupPartOfSpeech);

    toggleGroupArticle = new ToggleGroup2("toggleGroupArticle");
    setToggleGroup(Arrays.asList(toggleButtonA, toggleButtonAn, toggleButtonThe, toggleButtonNone), toggleGroupArticle);

    toggleGroupLevel = new ToggleGroup2("toggleGroupLevel");
    setToggleGroup(Arrays.asList(toggleButtonA1, toggleButtonA2, toggleButtonB1, toggleButtonB2, toggleButtonC1, toggleButtonC2), toggleGroupLevel);

    toggleGroupGrammar = new ToggleGroup2("toggleGroupGrammar");
    setToggleGroup(Arrays.asList(toggleButtonCountable, toggleButtonUncountable, toggleButtonCountableAndUncountable, toggleButtonPlural, toggleButtonEmpty), toggleGroupGrammar);
  }

  private void setToggleGroup(List<ToggleButton> toggleButtons, ToggleGroup toggleGroup) {
    toggleButtons.forEach(toggleButton -> toggleButton.setToggleGroup(toggleGroup));
  }

  private String getSelectedToggleText(final ToggleGroup2 toggleGroup2) {
    if (toggleGroup2.getSelectedToggle() != null && toggleGroup2.getSelectedToggle().isSelected()) {
      return ((ToggleButton) toggleGroup2.getSelectedToggle()).getUserData().toString();
    }
    return StringUtils.EMPTY;
  }

  public void migrateReleaseWords() {
    final WordRepository wordRepository = wordController.getWordRepository();
    final LessonWordRepository lessonWordRepository = wordController.getLessonWordRepository();
    final DefinitionRepository definitionRepository = wordController.getDefinitionRepository();
    final LessonRepository lessonRepository = wordController.getLessonRepository();

    final ReleaseWordRepository releaseWordRepository = wordController.getReleaseWordRepository();
    final ReleaseSentenceRepository releaseSentenceRepository = wordController.getReleaseSentenceRepository();

    List<ReleaseWord> objectsToMigrate = releaseWordRepository.findAllByPartOfSpeechIsNull();
    log.info("FOUND [{}] items to migrate.", objectsToMigrate.size());
    if (objectsToMigrate.size() > 1000) {
      objectsToMigrate = objectsToMigrate.subList(0, 1000);
    }

    final List<List<ReleaseWord>> chunks = ListUtils.partition(objectsToMigrate, 100);
    log.info("AGGREGATE them into [{}] chunks.", chunks.size());

    final AtomicInteger index = new AtomicInteger(0);

    new ArrayList<>(chunks).forEach(chunk -> {
      final List<LessonWord> lessonWords = new ArrayList<>();
      final List<Word> words = new ArrayList<>();

      chunk.forEach(target -> {
        log.info("WORKING ON INDEX [{}], [{}]", index.getAndIncrement(), target.getEnName());
        String englishNames = (StringUtils.isNoneEmpty(target.getEnName()) ? target.getEnName() + "; " : "")
          .concat((StringUtils.isNoneEmpty(target.getUsName()) ? target.getUsName() + "; " : ""))
          .concat((StringUtils.isNoneEmpty(target.getOtherNames()) ? target.getOtherNames() + "; " : ""))
          .trim();
        englishNames = englishNames.endsWith(";")
                       ? englishNames.substring(0, englishNames.length() - 1)
                       : englishNames;
        if (englishNames.startsWith("a ")) {
          toggleGroupArticle.selectToggle(toggleButtonA);
          toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
          toggleGroupGrammar.selectToggle(toggleButtonCountable);
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        } else if (englishNames.startsWith("an ")) {
          toggleGroupArticle.selectToggle(toggleButtonAn);
          toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
          toggleGroupGrammar.selectToggle(toggleButtonCountable);
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        } else if (englishNames.startsWith("the ")) {
          toggleGroupArticle.selectToggle(toggleButtonThe);
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        } else if (englishNames.startsWith("to ")) {
          toggleGroupPartOfSpeech.selectToggle(toggleButtonVerb);
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        }

        final List<Definition> definitions = new ArrayList<>(wordSoundFromTerm(WordTypeEnum.WORD, englishNames, target.getSound()));
        definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.COMPARATIVE, target.getComparative())));
        definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SUPERLATIVE, target.getSuperlative())));
        definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_TENSE, target.getPastTense())));
        definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_PARTICIPLE, target.getPastParticiple())));
        definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PLURAL, target.getPlural())));
        definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SYNONYM, target.getSynonym())));
        final List<ReleaseSentence> sentences = releaseSentenceRepository.findAllByWord_Id(target.getId());
        if (CollectionUtils.isNotEmpty(sentences)) {
          final List<Definition> definitionSentences = sentences.stream()
                                                                .map(sentence -> Definition.builder()
                                                                                           .englishName(sentence.getEnSentence())
                                                                                           .additionalInformation(sentence.getPlSentence())
                                                                                           .britishSound(sentence.getSound())
                                                                                           .type(WordTypeEnum.SENTENCE.toString())
                                                                                           .build())
                                                                .toList();
          definitions.addAll(definitionSentences);
        }

        final Word word = Word.builder()
                              .id(target.getId())
                              .polishName(target.getPlName())
                              .partOfSpeech(target.getPartOfSpeech())
                              .article(target.getArticle())
                              .grammarType(target.getGrammarType())
                              .level(target.getLevel())
                              .definitions(definitions)
                              .partOfSpeech("")
                              .build();

//        log.info("SAVING WORD [{}]", word.toString());
        final Word savedWord = wordRepository.save(word);

//        log.info("SAVING WORD_SOUNDS [{}]", wordSounds.size());
        if (CollectionUtils.isNotEmpty(definitions)) {
          definitions.forEach(wordSound -> wordSound.setWord(savedWord));
          definitionRepository.saveAll(definitions);
        }
        final LessonWord lessonWord = LessonWord.builder()
                                           .lesson(lessonRepository.findById(target.getLessonId()).get())
                                           .word(savedWord)
                                           .position(Long.valueOf(lessonWordRepository.countAllByLesson(target.getLessonId())).intValue() + 1)
                                           .build();
//        log.info("SAVING LESSON_WORD [{}]", lessonWord);
        lessonWordRepository.save(lessonWord);
        target.setPartOfSpeech("MIGRATED");
        releaseWordRepository.save(target);
      });
    });

  }


//    final NoteRepository repository = wordController.getNoteRepository();
//    final LessonRepository lessonRepository = wordController.getLessonRepository();
//    final ReleaseNoteRepository releaseRepository = wordController.getReleaseNoteRepository();
//    final List<ReleaseNote> objectsToMigrate = releaseRepository.findAll();
//
//    log.info("FOUND [{}] items to migrate.", objectsToMigrate.size());
//    final List<Note> migratedObjects = new ArrayList<>();
//
//    objectsToMigrate.forEach(target -> {
//      final Note migrateObject = Note.builder()
//                                     .id(target.getId())
//                                     .note(target.getNote())
//                                     .position(target.getPosition().intValue())
//                                     .lesson(lessonRepository.findById(target.getLesson().getId()).get())
//                                     .build();
//      migratedObjects.add(migrateObject);
//    });
//    log.info("SAVING...");
//    repository.saveAll(migratedObjects);
//    log.info("...SAVED");

    /*AtomicInteger i = new AtomicInteger(0);
    final ReleaseWordService releaseWordService = wordController.getReleaseWordService();
    final List<ReleaseWord> releaseWords = releaseWordService.getAll(); // .subList(0, 500);

    releaseWords
      .forEach(releaseWord -> {
        log.info("START {}, {}", i.getAndIncrement(), releaseWord.getEnName());
        try {
          setWordForm(releaseWord);
          buttonAddOnAction();

          final List<WordSound> wordSounds = listViewWordVariants.getItems();

          wordSounds.forEach(wordSound -> {
            setWordSoundForm(wordSound);
            buttonAddWordSoundOnAction();
          });

          releaseWord.setSource("ETUTOR");
          releaseWordService.save(releaseWord);
//          log.info("FINISH {}", releaseWord);
        } catch (Exception e) {
          log.error("An ERROR OCCURED FOR {}", releaseWord);
        }

      });
    log.warn("PROCESS FINISHED");*/
//  }

  public void setWordForm(final ReleaseWord releaseWord) {
    textFieldId.clear();

    toggleGroupPartOfSpeech.selectToggle(toggleGroupPartOfSpeech.getToggles().stream()
                                                                .filter(toggle -> toggle.getUserData().toString().equals(trimToEmpty(releaseWord.getPartOfSpeech())))
                                                                .findFirst()
                                                                .orElse(null));
    toggleGroupArticle.selectToggle(null);
    toggleGroupLevel.selectToggle(null);
    toggleGroupGrammar.selectToggle(null);

    String englishNames = (StringUtils.isNoneEmpty(releaseWord.getEnName()) ? releaseWord.getEnName() + "; " : "")
      .concat((StringUtils.isNoneEmpty(releaseWord.getUsName()) ? releaseWord.getUsName() + "; " : ""))
      .concat((StringUtils.isNoneEmpty(releaseWord.getOtherNames()) ? releaseWord.getOtherNames() + "; " : ""))
      .trim();
    englishNames = englishNames.endsWith(";")
                   ? englishNames.substring(0, englishNames.length() - 1)
                   : englishNames;
    if (englishNames.startsWith("a ")) {
      toggleGroupArticle.selectToggle(toggleButtonA);
      toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
      toggleGroupGrammar.selectToggle(toggleButtonCountable);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    } else if (englishNames.startsWith("an ")) {
      toggleGroupArticle.selectToggle(toggleButtonAn);
      toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
      toggleGroupGrammar.selectToggle(toggleButtonCountable);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    } else if (englishNames.startsWith("the ")) {
      toggleGroupArticle.selectToggle(toggleButtonThe);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    } else if (englishNames.startsWith("to ")) {
      toggleGroupPartOfSpeech.selectToggle(toggleButtonVerb);
      englishNames = englishNames.substring(englishNames.indexOf(" "));
    }

    final List<Definition> definitions = new ArrayList<>(wordSoundFromTerm(WordTypeEnum.WORD, englishNames));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.COMPARATIVE, releaseWord.getComparative())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SUPERLATIVE, releaseWord.getSuperlative())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_TENSE, releaseWord.getPastTense())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_PARTICIPLE, releaseWord.getPastParticiple())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PLURAL, releaseWord.getPlural())));
    definitions.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SYNONYM, releaseWord.getSynonym())));

    listViewLessons.setItems(null);
    setWordForm(releaseWord.getPlName(), definitions);
  }

  public void buttonCheckSoundsOnAction() {
    final String longmanAme = "https://www.ldoceonline.com/media/english/ameProns/";
    final String longmanBre = "https://www.ldoceonline.com/media/english/breProns/";

    final String dikiBre = "https://www.diki.pl/images-common/en/mp3/";
    final String dikiAme = "https://www.diki.pl/images-common/en-ame/mp3/";

    String sound = textFieldBritishSound.getText();

    final ClipboardContent content = new ClipboardContent();
    content.putString(sound);
    Clipboard.getSystemClipboard().setContent(content);

    checkSoundMultipleDefinitions(sound);

    String englishSound = "";
    String americanSound = "";
    if (sound.contains("https://www.ldoceonline.com/media/english/ameProns")
        || sound.contains("https://www.ldoceonline.com/media/english/breProns")) {

      if (sound.contains(longmanBre)) {
        englishSound = sound.substring(sound.indexOf(longmanBre));
        englishSound = englishSound.substring(0, englishSound.indexOf("?"));
      }
      if (sound.contains(longmanAme)) {
        americanSound = sound.substring(sound.indexOf(longmanAme));
        americanSound = americanSound.substring(0, americanSound.indexOf("?"));
      }
    }
    if (StringUtils.isNoneEmpty(americanSound)) {
      textFieldAmericanSound.setText(americanSound);
    } else if (StringUtils.isNoneBlank(sound)) {
      final String expectedSound = sound.substring(sound.indexOf("=") + 1, sound.indexOf("]"));
      checkSound(longmanAme + expectedSound, dikiAme + expectedSound, textFieldAmericanSound);
    }
    if (StringUtils.isNoneEmpty(englishSound)) {
      textFieldBritishSound.setText(englishSound);
    } else if (StringUtils.isNoneBlank(sound)) {
      final String expectedSound = sound.substring(sound.indexOf("=") + 1, sound.indexOf("]"));
      checkSound(longmanBre + expectedSound, dikiBre + expectedSound, textFieldBritishSound);
    }
    if (StringUtils.isNoneEmpty(textFieldAmericanSound.getText())
        && StringUtils.isNoneEmpty(textFieldBritishSound.getText())
        && textFieldBritishSound.getText().contains("[")
        && textFieldBritishSound.getText().contains("]")
        && textFieldBritishSound.getText().contains("=")) {
      textFieldBritishSound.clear();
    }

/*    if (StringUtils.isNoneBlank(sound)) {
      sound = sound.substring(sound.indexOf("=") + 1, sound.indexOf("]"));
      checkSound(longmanBre + sound, dikiBre + sound, textFieldBritishSound);
      checkSound(longmanAme + sound, dikiAme + sound, textFieldAmericanSound);
    }*/
  }

  private void checkSoundMultipleDefinitions(final String input) {
    final List<Definition> definitions = listViewWordVariants.getItems().stream()
                                                             .filter(definition -> !WordTypeEnum.WORD.toString().equals(definition.getType()))
                                                             .filter(definition -> !WordTypeEnum.SENTENCE.toString().equals(definition.getType()))
                                                             .toList();

    if (CollectionUtils.isNotEmpty(definitions)) {
      final List<String> sounds = Arrays.stream(input.split(";"))
                                         .filter(StringUtils::isNoneBlank)
                                         .map(StringUtils::trim)
                                         .toList();

      for (final String sound : sounds) {
        definitions.stream()
                   .filter(definition -> definition.getEnglishName().equals(sound.substring(sound.indexOf("[") + 1, sound.indexOf("="))))
          .findFirst()
          .ifPresent(definition -> {
            if (StringUtils.isAllEmpty(definition.getBritishSound(), definition.getAmericanSound())) {
              final String longmanAme = "https://www.ldoceonline.com/media/english/ameProns/";
              final String longmanBre = "https://www.ldoceonline.com/media/english/breProns/";
              final String dikiBre = "https://www.diki.pl/images-common/en/mp3/";
              final String dikiAme = "https://www.diki.pl/images-common/en-ame/mp3/";

              final String expectedSound = sound.substring(sound.indexOf("=") + 1, sound.indexOf("]"));

              final String americanSound = checkSound(longmanAme + expectedSound, dikiAme + expectedSound);
              final String britishSound = checkSound(longmanBre + expectedSound, dikiBre + expectedSound);

              if (StringUtils.isNoneEmpty(americanSound)) {
                definition.setAmericanSound(americanSound);
              }
              if (StringUtils.isNoneEmpty(britishSound)) {
                definition.setBritishSound(britishSound);
              }
              wordController.getDefinitionService().save(definition);
            }
          });
      }
    }
  }

  private void checkSound(final String url, final String backupUrl, final TextField textField) {
    final String finalUrl = SoundUtils.checkIfFileExists(url);
    if (StringUtils.isNoneEmpty(finalUrl)) {
      textField.setText(finalUrl);
    } else {
      final String finalBackupUrl = SoundUtils.checkIfFileExists(backupUrl);
      if (StringUtils.isNoneEmpty(finalBackupUrl)) {
        textField.setText(finalBackupUrl);
      }
    }
  }

  private String checkSound(final String url, final String backupUrl) {
    final String finalUrl = SoundUtils.checkIfFileExists(url);
    if (StringUtils.isNoneEmpty(finalUrl)) {
      return finalUrl;
    } else {
      final String finalBackupUrl = SoundUtils.checkIfFileExists(backupUrl);
      if (StringUtils.isNoneEmpty(finalBackupUrl)) {
        return finalBackupUrl;
      }
    }
    return "";
  }

  private String checkSound2(final String url, final String backupUrl) {
//    final String finalUrl = SoundUtils.checkIfFileExists(url);
//    if (StringUtils.isNoneEmpty(finalUrl)) {
//      return finalUrl;
//    } else {
      final String finalBackupUrl = SoundUtils.checkIfFileExists(backupUrl);
      if (StringUtils.isNoneEmpty(finalBackupUrl)) {
        return finalBackupUrl;
      }
//    }
    return "";
  }

  public void removeDuplicates() {
    final WordService wordService = wordController.getWordService();
    final LessonWordService lessonWordService = wordController.getLessonWordService();
    final DefinitionService definitionService = wordController.getDefinitionService();
    // 62, 9018, 17492
/*    final List<Word> input = List.of(
      wordService.getById(62L).get(),
      wordService.getById(9018L).get(),
      wordService.getById(17492L).get());*/


    final Map<String, Map<String, List<Word>>> wordsGroupedByName =
      wordService.getAll()
//    input
                 .stream()
                 .collect(Collectors.groupingBy(Word::getPolishName,
                                                Collectors.groupingBy(word -> word.getDefinitions()
                                                                                  .stream()
                                                                                  .filter(wordSound -> WordTypeEnum.WORD.toString().equals(wordSound.getType()))
                                                                                  .map(Definition::getEnglishName)
                                                                                  .findFirst()
                                                                                  .orElse("")
                                                                     )));

    log.info("[REMOVE_DUPLICATES] FOUND [{}] group", wordsGroupedByName.size());

    final List<Map<String, List<Word>>> duplicates = wordsGroupedByName.values()
                                                                       .stream()
                                                                       .filter(item -> item.values()
                                                                                           .stream()
                                                                                           .findFirst()
                                                                                           .orElse(List.of())
                                                                                           .size() > 1)
                                                                       .toList();

    log.info("[REMOVE_DUPLICATES] FOUND [{}] duplicates", duplicates.size());

    final AtomicInteger index = new AtomicInteger(0);

    for (Map<String, List<Word>> duplicate : duplicates) {

      for (Map.Entry<String, List<Word>> entry : duplicate.entrySet()) {

        final String key = entry.getKey();
        log.info("[REMOVE_DUPLICATES] FIXING [{}] at index [{}]", key, index.getAndIncrement());
        final List<Word> values = entry.getValue();

        final List<Word> wordsToRemove = new ArrayList<>();
        final Word firstWord = values.get(0);
        values.remove(0);

        for (Word word : values) {
          for (LessonWord lessonWord : word.getLessonWords()) {
            lessonWord.setWord(firstWord);
            firstWord.getLessonWords().add(lessonWord);
          }

          for (Definition definition : word.getDefinitions()) {
            if (firstWord.getDefinitions()
                     .stream()
              .filter(item -> item.getType().equals(definition.getType()))
              .filter(item -> StringUtils.equals(item.getEnglishName(), definition.getEnglishName()))
              .filter(item -> StringUtils.equals(item.getAdditionalInformation(), definition.getAdditionalInformation()))
              .filter(item -> StringUtils.equals(item.getBritishSound(), definition.getBritishSound()))
              .filter(item -> StringUtils.equals(item.getAmericanSound(), definition.getAmericanSound()))
              .count() == 0) {
              definition.setWord(firstWord);
              firstWord.getDefinitions().add(definition);
            }
          }
          wordsToRemove.add(word);
        }
        firstWord.getLessonWords().forEach(lessonWordService::save);
        firstWord.getDefinitions().forEach(definitionService::save);
        wordService.save(firstWord);

        wordsToRemove.forEach(wordService::delete);
      }
    }
  }

  public void getLongmanSounds() {

    // longmanScrapper.webScrap(englishTerm, forceTranslate)

  }

  public void checkSentences() {
    final WordService wordService = wordController.getWordService();
    final LessonWordService lessonWordService = wordController.getLessonWordService();
    final DefinitionService definitionService = wordController.getDefinitionService();

    final List<Word> words = wordService.getAll()
                                        .stream()
                                        .filter(word -> word.getDefinitions()
                                                            .stream()
                                                            .anyMatch(wordSound -> WordTypeEnum.SENTENCE.toString().equals(wordSound.getType())))
                                        .toList();

    for (final Word word : words) {
      for (final Definition definition : word.getDefinitions()) {
        log.info("CHECKING [{}]", definition);

        if (WordTypeEnum.SENTENCE.toString().equals(definition.getType())) {
          final String longmanAme = "https://www.ldoceonline.com/media/english/ameProns/";
          final String longmanBre = "https://www.ldoceonline.com/media/english/breProns/";

          final String dikiBre = "https://www.diki.pl/images-common/en/mp3/";
          final String dikiAme = "https://www.diki.pl/images-common/en-ame/mp3/";

          String sound = definition.getBritishSound();
          if (StringUtils.isNoneBlank(sound) && sound.contains("=") && sound.contains("]")) {
            sound = sound.substring(sound.indexOf("=") + 1, sound.indexOf("]"));
            final String british = checkSound2(longmanBre + sound, dikiBre + sound);
            final String american = checkSound2(longmanAme + sound, dikiAme + sound);
            if (StringUtils.isNoneEmpty(british) || StringUtils.isNoneEmpty(american)) {
              definition.setBritishSound(null);
              definition.setAmericanSound(null);
              if (StringUtils.isNoneEmpty(british)) {
                definition.setBritishSound(british);
              }
              if (StringUtils.isNoneEmpty(american)) {
                definition.setAmericanSound(american);
              }
              log.info("SAVING [{}]", wordService);
              definitionService.save(definition);
            }
          }
        }
      }

    }

  }

  @Override
  public void run(final String... args) throws Exception {
//    final List<Word> words = wordController.getWordService().getAll();
//    for (final Word word : words) {
//
//      for (final Definition definition : word.getDefinitions().stream()
//                                             .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
//                                             .filter(definition -> definition.getBritishSound().contains("[")
//                                                                   && definition.getBritishSound().contains("]")
//                                                                   && definition.getBritishSound().contains("="))
//                                             .toList()) {
//        final List<Term> terms = longmanScrapper.webScrap(definition.getEnglishName(), true);
//
//        final List<String> sounds = new ArrayList<>();
//        sounds.addAll(terms.stream()
//                            .map(Term::getEnglishName)
//                            .filter(text -> text.contains("www.ldoceonline.com") && text.contains(".mp3"))
//                            .toList());
//        sounds.addAll(terms.stream()
//                            .map(Term::getAmericanName)
//                            .filter(text -> text.contains("www.ldoceonline.com") && text.contains(".mp3"))
//                            .toList());
//        sounds.addAll(terms.stream()
//                            .map(Term::getOtherName)
//                            .filter(text -> text.contains("www.ldoceonline.com") && text.contains(".mp3"))
//                            .toList());
//
//
//
//        definition.setBritishSound(definition.getBritishSound()
//                                             .concat(String.join("; ", sounds)));
//        wordController.getWordSoundService().save(definition);
//      }
//    }
  }
}
