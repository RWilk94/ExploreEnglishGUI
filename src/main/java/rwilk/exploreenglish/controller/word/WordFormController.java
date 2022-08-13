package rwilk.exploreenglish.controller.word;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.custom.ToggleGroup2;
import rwilk.exploreenglish.exception.RequiredFieldsAreEmptyException;
import rwilk.exploreenglish.exception.RequiredObjectNotFoundException;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.release.ReleaseCourse;
import rwilk.exploreenglish.model.entity.release.ReleaseExercise;
import rwilk.exploreenglish.model.entity.release.ReleaseExerciseRow;
import rwilk.exploreenglish.model.entity.release.ReleaseLesson;
import rwilk.exploreenglish.model.entity.release.ReleaseNote;
import rwilk.exploreenglish.model.entity.release.ReleaseSentence;
import rwilk.exploreenglish.model.entity.release.ReleaseWord;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSound;
import rwilk.exploreenglish.repository.CourseRepository;
import rwilk.exploreenglish.repository.ExerciseItemRepository;
import rwilk.exploreenglish.repository.ExerciseRepository;
import rwilk.exploreenglish.repository.LessonRepository;
import rwilk.exploreenglish.repository.LessonWordRepository;
import rwilk.exploreenglish.repository.NoteRepository;
import rwilk.exploreenglish.repository.WordRepository;
import rwilk.exploreenglish.repository.WordSoundRepository;
import rwilk.exploreenglish.repository.release.ReleaseCourseRepository;
import rwilk.exploreenglish.repository.release.ReleaseExerciseRepository;
import rwilk.exploreenglish.repository.release.ReleaseExerciseRowRepository;
import rwilk.exploreenglish.repository.release.ReleaseLessonRepository;
import rwilk.exploreenglish.repository.release.ReleaseNoteRepository;
import rwilk.exploreenglish.repository.release.ReleaseSentenceRepository;
import rwilk.exploreenglish.repository.release.ReleaseWordRepository;
import rwilk.exploreenglish.utils.FormUtils;
import rwilk.exploreenglish.utils.SoundUtils;
import rwilk.exploreenglish.utils.WordUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static rwilk.exploreenglish.exception.ExceptionControllerAdvice.*;

@Slf4j
@Controller
public class WordFormController implements Initializable {

  private WordController wordController;
  private final List<Object> controls = new ArrayList<>();

  @FXML private TextField textFieldId;
  @Getter @FXML private ComboBox<Lesson> comboBoxLesson;
  @FXML private ListView<Lesson> listViewLessons;
  @FXML private TextField textFieldEnglishNames;
  @FXML private TextField textFieldWordSoundId;
  @FXML private TextField  textFieldEnglishName;
  @FXML private TextField  textFieldAdditionalInformation;
  @FXML private ComboBox<WordTypeEnum> comboBoxWordType;
  @FXML private TextField  textFieldAmericanSound;
  @FXML private TextField textFieldBritishSound;
  @FXML private ListView<WordSound> listViewWordVariants;
  @Getter @FXML private TextField textFieldPolishName;
  @FXML private ToggleButton toggleButtonNoun;
  @FXML private ToggleButton toggleButtonVerb;
  @FXML private ToggleButton toggleButtonAdjective;
  @FXML private ToggleButton toggleButtonAdverb;
  @FXML private ToggleButton toggleButtonPhrasalVerb;
  @FXML private ToggleButton toggleButtonSentence;
  @FXML private ToggleButton toggleButtonIdiom;
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
      protected void updateItem(WordSound item, boolean empty) {
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
          word.setEnglishNames(listViewWordVariants.getItems().stream()
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
          .englishNames(ListUtils.emptyIfNull(listViewWordVariants.getItems()).stream()
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
    setWordForm(word.getPolishName(), word.getEnglishNames());

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

    final List<WordSound> wordSounds = new ArrayList<>(wordSoundFromTerm(WordTypeEnum.WORD, englishNames));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.COMPARATIVE, term.getComparative())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SUPERLATIVE, term.getSuperlative())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_TENSE, term.getPastTense())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_PARTICIPLE, term.getPastParticiple())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PLURAL, term.getPlural())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SYNONYM, term.getSynonym())));

    listViewLessons.setItems(null);
    setWordForm(term.getPolishName(), wordSounds);
  }

  private List<WordSound> wordSoundFromTerm(final WordTypeEnum type, final String value) {
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
          return WordSound.builder()
              .englishName(enName)
              .additionalInformation(additional)
              .type(type.toString())
              .build();
        })
        .toList();
  }

  private List<WordSound> wordSoundFromTerm(final WordTypeEnum type, final String value, final String sound) {
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
                   return WordSound.builder()
                                   .englishName(enName)
                                   .additionalInformation(additional)
                                   .type(type.toString())
                                   .britishSound(sound)
                                   .build();
                 })
                 .toList();
  }

  private void setWordForm(final String polishName, final List<WordSound> wordSounds) {
    textFieldPolishName.setText(trimToEmpty(polishName));
    listViewWordVariants.setItems(FXCollections.observableArrayList(wordSounds));
    if (!CollectionUtils.isEmpty(wordSounds)) {
      wordSounds.stream()
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
    final WordSound selectedItem = listViewWordVariants.getSelectionModel().getSelectedItem();
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
    final WordSound wordSound = getWordSoundById();

    wordController.getWordSoundService().delete(wordSound);

    refreshListViewWordVariants(wordSound.getWord().getId());
    wordController.refreshTableView();
    wordController.refreshChildComboBoxes();
  }

  public void buttonEditWordSoundOnAction() {
    if (isAnyBlank(textFieldEnglishName.getText(), textFieldWordSoundId.getText())
        || comboBoxWordType.getSelectionModel().getSelectedItem() == null) {
      throw new RequiredFieldsAreEmptyException("Cannot edit WORD_SOUND due to empty required fields.");
    }

    final WordSound wordSound = getWordSoundById();
    wordSound.setEnglishName(trimToEmpty(textFieldEnglishName.getText()));
    wordSound.setAdditionalInformation(trimToEmpty(textFieldAdditionalInformation.getText()));
    wordSound.setType(comboBoxWordType.getSelectionModel().getSelectedItem().toString());
    wordSound.setAmericanSound(trimToEmpty(textFieldAmericanSound.getText()));
    wordSound.setBritishSound(trimToEmpty(textFieldBritishSound.getText()));

    final WordSound updated = wordController.getWordSoundService().save(wordSound);

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
    final List<WordSound> wordSounds = ListUtils.emptyIfNull(listViewWordVariants.getItems());

    final Word word = getWordById();
    final WordSound wordSound = WordSound.builder()
        .id(null)
        .englishName(trimToEmpty(textFieldEnglishName.getText()))
        .additionalInformation(trimToEmpty(textFieldAdditionalInformation.getText()))
        .type(comboBoxWordType.getSelectionModel().getSelectedItem().toString())
        .americanSound(trimToEmpty(textFieldAmericanSound.getText()))
        .britishSound(trimToEmpty(textFieldBritishSound.getText()))
        .word(word)
        .build();

    final WordSound updated = wordController.getWordSoundService().save(wordSound);

    setWordSoundForm(updated);
    refreshListViewWordVariants(updated.getWord().getId());

    // add WordSounds removed by refresh
    final List<WordSound> actualWordSounds = ListUtils.emptyIfNull(listViewWordVariants.getItems());
    if (wordSounds.size() > actualWordSounds.size()) {
      wordSounds.forEach(ws -> {
        if (actualWordSounds.stream().noneMatch(aws -> aws.getEnglishName().equals(ws.getEnglishName()) && aws.getType().equals(ws.getType()))) {
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

  private WordSound getWordSoundById() {
    return wordController.getWordSoundService().getById(Long.valueOf(textFieldWordSoundId.getText()))
        .orElseThrow(() -> new RequiredObjectNotFoundException(NOT_FOUND_WORD_SOUND_INSTANCE));
  }

  private void refreshListViewWordVariants(final Long wordId) {
    final List<WordSound> wordSounds = wordController.getWordSoundService()
        .getAllByWordId(wordId);
    listViewWordVariants.setItems(FXCollections.observableArrayList(wordSounds));
  }

  private void setWordSoundForm(final WordSound wordSound) {
    textFieldWordSoundId.setText(String.valueOf(wordSound.getId()));
    textFieldEnglishName.setText(wordSound.getEnglishName());
    textFieldAdditionalInformation.setText(wordSound.getAdditionalInformation());
    comboBoxWordType.getSelectionModel().select(WordTypeEnum.valueOf(wordSound.getType()));
    textFieldAmericanSound.setText(wordSound.getAmericanSound());
    textFieldBritishSound.setText(wordSound.getBritishSound());
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
    setToggleGroup(Arrays.asList(toggleButtonNoun, toggleButtonVerb, toggleButtonAdjective, toggleButtonAdverb,
                                 toggleButtonPhrasalVerb, toggleButtonSentence, toggleButtonIdiom, toggleButtonOther), toggleGroupPartOfSpeech);

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
    final WordSoundRepository wordSoundRepository = wordController.getWordSoundRepository();
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

        final List<WordSound> wordSounds = new ArrayList<>(wordSoundFromTerm(WordTypeEnum.WORD, englishNames, target.getSound()));
        wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.COMPARATIVE, target.getComparative())));
        wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SUPERLATIVE, target.getSuperlative())));
        wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_TENSE, target.getPastTense())));
        wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_PARTICIPLE, target.getPastParticiple())));
        wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PLURAL, target.getPlural())));
        wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SYNONYM, target.getSynonym())));
        final List<ReleaseSentence> sentences = releaseSentenceRepository.findAllByWord_Id(target.getId());
        if (CollectionUtils.isNotEmpty(sentences)) {
          final List<WordSound> wordSoundSentences = sentences.stream()
                                                              .map(sentence -> WordSound.builder()
                                                                                        .englishName(sentence.getEnSentence())
                                                                                        .additionalInformation(sentence.getPlSentence())
                                                                                        .britishSound(sentence.getSound())
                                                                                        .type(WordTypeEnum.SENTENCE.toString())
                                                                                        .build())
                                                              .toList();
          wordSounds.addAll(wordSoundSentences);
        }

        final Word word = Word.builder()
                              .id(target.getId())
                              .polishName(target.getPlName())
                              .partOfSpeech(target.getPartOfSpeech())
                              .article(target.getArticle())
                              .grammarType(target.getGrammarType())
                              .level(target.getLevel())
                              .englishNames(wordSounds)
                              .partOfSpeech("")
                              .build();

//        log.info("SAVING WORD [{}]", word.toString());
        final Word savedWord = wordRepository.save(word);

//        log.info("SAVING WORD_SOUNDS [{}]", wordSounds.size());
        if (CollectionUtils.isNotEmpty(wordSounds)) {
          wordSounds.forEach(wordSound -> wordSound.setWord(savedWord));
          wordSoundRepository.saveAll(wordSounds);
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

    final List<WordSound> wordSounds = new ArrayList<>(wordSoundFromTerm(WordTypeEnum.WORD, englishNames));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.COMPARATIVE, releaseWord.getComparative())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SUPERLATIVE, releaseWord.getSuperlative())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_TENSE, releaseWord.getPastTense())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PAST_PARTICIPLE, releaseWord.getPastParticiple())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.PLURAL, releaseWord.getPlural())));
    wordSounds.addAll(new ArrayList<>(wordSoundFromTerm(WordTypeEnum.SYNONYM, releaseWord.getSynonym())));

    listViewLessons.setItems(null);
    setWordForm(releaseWord.getPlName(), wordSounds);
  }

  public void buttonCheckSoundsOnAction() {
    final String longmanAme = "https://www.ldoceonline.com/media/english/ameProns/";
    final String longmanBre = "https://www.ldoceonline.com/media/english/breProns/";

    final String dikiBre = "https://www.diki.pl/images-common/en/mp3/";
    final String dikiAme = "https://www.diki.pl/images-common/en-ame/mp3/";

    String sound = textFieldBritishSound.getText();
    if (StringUtils.isNoneBlank(sound)) {
      sound = sound.substring(sound.indexOf("=") + 1, sound.indexOf("]"));
      checkSound(longmanBre + sound, dikiBre + sound, textFieldBritishSound);
      checkSound(longmanAme + sound, dikiAme + sound, textFieldAmericanSound);
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

}
