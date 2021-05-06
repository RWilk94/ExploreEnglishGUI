package rwilk.exploreenglish.controller.word;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.custom.ToggleGroup2;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.utils.FormUtils;
import rwilk.exploreenglish.utils.WordUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class WordFormController implements Initializable {

  private WordController wordController;
  private final List<Object> controls = new ArrayList<>();
  private final List<Control> requiredControls = new ArrayList<>();

  public TextField textFieldId;
  public ComboBox<Lesson> comboBoxLesson;
  public TextField textFieldPolishName;
  public TextField textFieldEnglishNames;
  public ListView<String> listViewNames;
  public ToggleButton toggleButtonNoun;
  public ToggleButton toggleButtonVerb;
  public ToggleButton toggleButtonAdjective;
  public ToggleButton toggleButtonAdverb;
  public ToggleButton toggleButtonPhrasalVerb;
  public ToggleButton toggleButtonSentence;
  public ToggleButton toggleButtonIdiom;
  public ToggleButton toggleButtonOther;
  public ToggleButton toggleButtonA;
  public ToggleButton toggleButtonAn;
  public ToggleButton toggleButtonThe;
  public ToggleButton toggleButtonNone;
  public ToggleButton toggleButtonCountable;
  public ToggleButton toggleButtonUncountable;
  public ToggleButton toggleButtonCountableAndUncountable;
  public ToggleButton toggleButtonPlural;
  public ToggleButton toggleButtonEmpty;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldPlural;
  public TextField textFieldOpposite;
  public TextField textFieldSynonym;
  private ToggleGroup2 toggleGroupPartOfSpeech;
  private ToggleGroup2 toggleGroupArticle;
  private ToggleGroup2 toggleGroupGrammar;
  public ListView<Lesson> listViewLessons;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setToggleGroups();
    controls.addAll(Arrays.asList(textFieldId, textFieldEnglishNames,
        textFieldPolishName, toggleGroupPartOfSpeech, /* sound, */ toggleGroupArticle, toggleGroupGrammar, textFieldComparative,
        textFieldSuperlative, textFieldPastTense, textFieldPastParticiple, textFieldPlural, textFieldOpposite, textFieldSynonym, comboBoxLesson, listViewLessons));
    requiredControls.addAll(Arrays.asList(textFieldEnglishNames, textFieldPolishName));

    textFieldEnglishNames.textProperty().addListener((observable, oldValue, newValue) -> {
      List<String> words = Arrays.stream(newValue.split(";"))
          .map(StringUtils::trimToEmpty)
          .filter(StringUtils::isNoneEmpty)
          .collect(Collectors.toList());
      listViewNames.setItems(FXCollections.observableArrayList(words));

      listViewNames.setCellFactory(lv -> new ListCell<String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          if (item != null) {
            this.setText(item);
            if (!wordController.getWordService().getAllByEnglishNames(item).isEmpty()) {
              setStyle("-fx-background-color: #ff0000");
            } else if (!wordController.getWordService().getAllByEnglishNamesLike(item).isEmpty()) {
              setStyle("-fx-background-color: #ffa200");
            } else {
              setStyle("");
            }
          } else {
            this.setText("");
            setStyle("");
          }
        }
      });

      wordController.getWordTableController().textFieldFilterByEnName.setText(newValue);
    });

    textFieldPolishName.textProperty().addListener((observable, oldValue, newValue) ->
        wordController.getWordTableController().textFieldFilterByPlName.setText(newValue));
  }

  public void init(WordController wordController) {
    this.wordController = wordController;
    initializeLessonComboBox();
    setLessonWordForm();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      wordController.getWordService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(word -> wordController.getWordService().delete(word));
      buttonClearOnAction(actionEvent);
      wordController.refreshTableView();
      wordController.refreshChildTableView();
    }
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    FormUtils.clear(controls);
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (FormUtils.allFieldsFilled(requiredControls)) {
      wordController.getWordService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(word -> {
            word.setEnglishNames(StringUtils.trimToEmpty(textFieldEnglishNames.getText()));
            word.setPolishName(StringUtils.trimToEmpty(textFieldPolishName.getText()));
            word.setPartOfSpeech(StringUtils.trimToEmpty(getSelectedToggleText(toggleGroupPartOfSpeech)));
            word.setArticle(StringUtils.trimToEmpty(getSelectedToggleText(toggleGroupArticle)));
            word.setGrammarType(StringUtils.trimToEmpty(getSelectedToggleText(toggleGroupGrammar)));
            word.setComparative(StringUtils.trimToEmpty(textFieldComparative.getText()));
            word.setSuperlative(StringUtils.trimToEmpty(textFieldSuperlative.getText()));
            word.setPastTense(StringUtils.trimToEmpty(textFieldPastTense.getText()));
            word.setPastParticiple(StringUtils.trimToEmpty(textFieldPastParticiple.getText()));
            word.setPlural(StringUtils.trimToEmpty(textFieldPlural.getText()));
            word.setOpposite(StringUtils.trimToEmpty(textFieldOpposite.getText()));
            word.setSynonym(StringUtils.trimToEmpty(textFieldSynonym.getText()));
            word = wordController.getWordService().save(word);

            setWordForm(word);
            wordController.refreshTableView();
            wordController.refreshChildComboBoxes();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (FormUtils.allFieldsFilled(requiredControls)) {

      Word word = Word.builder()
          .id(null)
          .englishNames(StringUtils.trimToEmpty(textFieldEnglishNames.getText()))
          .polishName(StringUtils.trimToEmpty(textFieldPolishName.getText()))
          .partOfSpeech(StringUtils.trimToEmpty(getSelectedToggleText(toggleGroupPartOfSpeech)))
          .article(StringUtils.trimToEmpty(getSelectedToggleText(toggleGroupArticle)))
          .grammarType(StringUtils.trimToEmpty(getSelectedToggleText(toggleGroupGrammar)))
          .comparative(StringUtils.trimToEmpty(textFieldComparative.getText()))
          .superlative(StringUtils.trimToEmpty(textFieldSuperlative.getText()))
          .pastTense(StringUtils.trimToEmpty(textFieldPastTense.getText()))
          .pastParticiple(StringUtils.trimToEmpty(textFieldPastParticiple.getText()))
          .plural(StringUtils.trimToEmpty(textFieldPlural.getText()))
          .opposite(StringUtils.trimToEmpty(textFieldOpposite.getText()))
          .synonym(StringUtils.trimToEmpty(textFieldSynonym.getText()))
          .build();
      word = wordController.getWordService().save(word);
      setWordForm(word);

      if (comboBoxLesson.getSelectionModel().getSelectedItem() != null) {
        LessonWord lessonWord = LessonWord.builder()
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
  }

  public void setWordForm(Word word) {
    textFieldId.setText(StringUtils.trimToEmpty(word.getId().toString()));

    toggleGroupPartOfSpeech.selectToggle(toggleGroupPartOfSpeech.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(StringUtils.trimToEmpty(word.getPartOfSpeech())))
        .findFirst()
        .orElse(null));
    toggleGroupArticle.selectToggle(toggleGroupArticle.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(StringUtils.trimToEmpty(word.getArticle())))
        .findFirst()
        .orElse(null));
    toggleGroupGrammar.selectToggle(toggleGroupGrammar.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(StringUtils.trimToEmpty(word.getGrammarType())))
        .findFirst()
        .orElse(null));
    setWordForm(word.getEnglishNames(), word.getPolishName(), word.getComparative(), word.getSuperlative(), word.getPastTense(),
        word.getPastParticiple(), word.getPlural(), word.getSynonym());
    textFieldOpposite.setText(StringUtils.trimToEmpty(word.getOpposite()));

    setLessonWordForm();
  }

  public void setWordForm(Term term) {
    toggleGroupPartOfSpeech.selectToggle(toggleGroupPartOfSpeech.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(StringUtils.trimToEmpty(term.getPartOfSpeech())))
        .findFirst()
        .orElse(null));
    toggleGroupArticle.selectToggle(null);
    toggleGroupGrammar.selectToggle(null);
    String englishNames = (StringUtils.isNoneEmpty(term.getEnglishName()) ? term.getEnglishName() + "; " : "")
        .concat((StringUtils.isNoneEmpty(term.getAmericanName()) ? term.getAmericanName() + "; " : ""))
        .concat((StringUtils.isNoneEmpty(term.getOtherName()) ? term.getOtherName() + "; " : "")).trim();
    englishNames = englishNames.substring(englishNames.length() - 1).equals(";")
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

    setWordForm(WordUtils.replaceSpecialText(englishNames), term.getPolishName(), term.getComparative(), term.getSuperlative(), term.getPastTense(), term.getPastParticiple(), term.getPlural(), term.getSynonym());
    textFieldSynonym.setText(StringUtils.trimToEmpty(term.getSynonym()));
  }

  private void setWordForm(String otherName, String polishName, String comparative, String superlative, String pastTense,
                           String pastParticiple, String plural, String synonym) {
    textFieldPolishName.setText(StringUtils.trimToEmpty(polishName));
    textFieldComparative.setText(StringUtils.trimToEmpty(comparative));
    textFieldSuperlative.setText(StringUtils.trimToEmpty(superlative));
    textFieldPastTense.setText(StringUtils.trimToEmpty(pastTense));
    textFieldPastParticiple.setText(StringUtils.trimToEmpty(pastParticiple));
    textFieldPlural.setText(StringUtils.trimToEmpty(plural));
    textFieldOpposite.setText(StringUtils.EMPTY);
    textFieldSynonym.setText(StringUtils.trimToEmpty(synonym));
    textFieldEnglishNames.setText(StringUtils.trimToEmpty(otherName));
  }

  private void setToggleGroups() {
    toggleGroupPartOfSpeech = new ToggleGroup2("toggleGroupPartOfSpeech");
    setToggleGroup(Arrays.asList(toggleButtonNoun, toggleButtonVerb, toggleButtonAdjective, toggleButtonAdverb,
        toggleButtonPhrasalVerb, toggleButtonSentence, toggleButtonIdiom, toggleButtonOther), toggleGroupPartOfSpeech);

    toggleGroupArticle = new ToggleGroup2("toggleGroupArticle");
    setToggleGroup(Arrays.asList(toggleButtonA, toggleButtonAn, toggleButtonThe, toggleButtonNone), toggleGroupArticle);

    toggleGroupGrammar = new ToggleGroup2("toggleGroupGrammar");
    setToggleGroup(Arrays.asList(toggleButtonCountable, toggleButtonUncountable, toggleButtonCountableAndUncountable, toggleButtonPlural, toggleButtonEmpty), toggleGroupGrammar);
  }

  private void setToggleGroup(List<ToggleButton> toggleButtons, ToggleGroup toggleGroup) {
    toggleButtons.forEach(toggleButton -> toggleButton.setToggleGroup(toggleGroup));
  }

  public void initializeLessonComboBox() {
    List<Lesson> lessons = wordController.getLessonService().getAll();
    comboBoxLesson.setItems(FXCollections.observableArrayList(lessons));
  }

  public void buttonTranslateOnAction(ActionEvent actionEvent) {
    translate(true);
  }

  public void translate(boolean changeTab) {
    if (!StringUtils.trimToEmpty(textFieldEnglishNames.getText()).isEmpty()) {
      wordController.getInjectService().getScrapperController().webScrap(StringUtils.trimToEmpty(textFieldEnglishNames.getText().split(";")[0]));
      if (changeTab) {
        wordController.getTabPane().getSelectionModel().select(1);
      }
    }
  }

  private String getSelectedToggleText(ToggleGroup2 toggleGroup2) {
      if (toggleGroup2.getSelectedToggle() != null && toggleGroup2.getSelectedToggle().isSelected()) {
        return ((ToggleButton) toggleGroup2.getSelectedToggle()).getUserData().toString();
    }
    return StringUtils.EMPTY;
  }

  public ToggleGroup2 getToggleGroupGrammar() {
    return toggleGroupGrammar;
  }

  public ToggleGroup2 getToggleGroupArticle() {
    return toggleGroupArticle;
  }

  public ToggleGroup2 getToggleGroupPartOfSpeech() {
    return toggleGroupPartOfSpeech;
  }

  public void buttonAddLessonOnAction(ActionEvent actionEvent) {
    if (comboBoxLesson.getSelectionModel().getSelectedItem() != null && StringUtils.isNoneEmpty(textFieldId.getText())) {
      Lesson lesson = comboBoxLesson.getSelectionModel().getSelectedItem();
      Long wordId = Long.parseLong(textFieldId.getText());

      wordController.getWordService().getById(wordId)
          .ifPresent(word -> {
            LessonWord lessonWord = wordController.getLessonWordService().save(
                LessonWord.builder()
                    .word(word)
                    .lesson(lesson)
                    .position(wordController.getLessonWordService().getCountByLesson(comboBoxLesson.getSelectionModel().getSelectedItem()))
                    .build());
            setLessonWordForm();
            wordController.refreshTableView();
            wordController.refreshChildComboBoxes();
          });
    }
  }

  public void buttonRemoveLessonOnAction(ActionEvent actionEvent) {
    if (comboBoxLesson.getSelectionModel().getSelectedItem() != null && StringUtils.isNoneEmpty(textFieldId.getText())) {
      Lesson lesson = comboBoxLesson.getSelectionModel().getSelectedItem();
      Long wordId = Long.parseLong(textFieldId.getText());

      wordController.getLessonWordService().getByLessonIdAndWordId(lesson.getId(), wordId)
          .ifPresent(lessonWord -> wordController.getLessonWordService().deleteById(lessonWord.getId()));
      setLessonWordForm();
      wordController.refreshTableView();
      wordController.refreshChildComboBoxes();
    }
  }

  private void setLessonWordForm() {
    if (StringUtils.isNoneEmpty(textFieldId.getText())) {
      wordController.getWordService().getById(Long.parseLong(textFieldId.getText()))
          .ifPresent(word -> {
            List<LessonWord> lessonWords = wordController.getLessonWordService().getAllByWord(word);
            listViewLessons.setItems(null);
            listViewLessons.setItems(FXCollections.observableArrayList(lessonWords.stream()
                .map(LessonWord::getLesson)
                .collect(Collectors.toList())));
          });
    }

  }

  public void listViewLessonsOnMouseClicked(MouseEvent mouseEvent) {
    Lesson selectedItem = listViewLessons.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      comboBoxLesson.getSelectionModel().select(selectedItem);
    }

  }

  public void toggleButtonAOnAction(ActionEvent actionEvent) {
    toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
    toggleGroupGrammar.selectToggle(toggleButtonCountable);
  }

  public void toggleButtonPluralOnAction(ActionEvent actionEvent) {
    toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
    toggleGroupArticle.selectToggle(toggleButtonNone);
  }

  public void toggleButtonPartOfSpeechOnMouseClicked(MouseEvent mouseEvent) {
    if (toggleGroupArticle.getSelectedToggle() == null && toggleGroupGrammar.getSelectedToggle() == null) {
      toggleGroupArticle.selectToggle(toggleButtonNone);
      toggleGroupGrammar.selectToggle(toggleButtonEmpty);
    }
  }

  public void toggleButtonCountableAndUncountableOnAction(ActionEvent actionEvent) {
    if (toggleGroupPartOfSpeech.getSelectedToggle() == null && toggleGroupArticle.getSelectedToggle() == null) {
      toggleGroupPartOfSpeech.selectToggle(toggleButtonNoun);
      toggleGroupArticle.selectToggle(toggleButtonNone);
    }
  }
}
