package rwilk.exploreenglish.controller.word;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.custom.ToggleGroup2;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.utils.FormUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class WordFormController implements Initializable {

  private WordController wordController;
  private final List<Object> controls = new ArrayList<>();
  private final List<Control> requiredControls = new ArrayList<>();

  public TextField textFieldId;
  public ComboBox<Lesson> comboBoxLesson;
  public TextField textFieldEnglishName;
  public TextField textFieldAmericanName;
  public TextField textFieldPolishName;
  public TextField textFieldOtherNames;
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
  public ToggleButton toggleButtonNone;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldPlural;
  public TextField textFieldSynonym;
  private ToggleGroup2 toggleGroupPartOfSpeech;
  private ToggleGroup2 toggleGroupArticle;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setToggleGroups();
    controls.addAll(Arrays.asList(textFieldId, textFieldEnglishName, textFieldAmericanName, textFieldOtherNames,
        textFieldPolishName, toggleGroupPartOfSpeech, /* sound, */ toggleGroupArticle, textFieldComparative,
        textFieldSuperlative, textFieldPastTense, textFieldPastParticiple, textFieldPlural, textFieldSynonym, comboBoxLesson));
    requiredControls.addAll(Arrays.asList(textFieldEnglishName, textFieldPolishName, comboBoxLesson));
  }

  public void init(WordController wordController) {
    this.wordController = wordController;
    initializeLessonComboBox();
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
            word = FormUtils.getWord(controls);
            word = wordController.getWordService().save(word);
            setWordForm(word);
            wordController.refreshTableView();
            wordController.refreshChildComboBoxes();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (FormUtils.allFieldsFilled(requiredControls)) {
      Word word = FormUtils.getWord(controls);
      word.setId(null);
      word.setPosition(wordController.getWordService().getCountByLesson(comboBoxLesson.getSelectionModel().getSelectedItem()));
      word = wordController.getWordService().save(word);
      FormUtils.setWordForm(word, controls);
      wordController.refreshTableView();
      wordController.refreshChildComboBoxes();
    }
  }

  public void setWordForm(Word word) {
    FormUtils.setWordForm(word, controls);
  }

  private void setToggleGroups() {
    toggleGroupPartOfSpeech = new ToggleGroup2("toggleGroupPartOfSpeech");
    setToggleGroup(Arrays.asList(toggleButtonNoun, toggleButtonVerb, toggleButtonAdjective, toggleButtonAdverb,
        toggleButtonPhrasalVerb, toggleButtonSentence, toggleButtonIdiom, toggleButtonOther), toggleGroupPartOfSpeech);

    toggleGroupArticle = new ToggleGroup2("toggleGroupArticle");
    setToggleGroup(Arrays.asList(toggleButtonA, toggleButtonAn, toggleButtonNone), toggleGroupArticle);
  }

  private void setToggleGroup(List<ToggleButton> toggleButtons, ToggleGroup toggleGroup) {
    toggleButtons.forEach(toggleButton -> toggleButton.setToggleGroup(toggleGroup));
  }

  public void initializeLessonComboBox() {
    List<Lesson> lessons = wordController.getLessonService().getAll();
    comboBoxLesson.setItems(FXCollections.observableArrayList(lessons));
  }

}
