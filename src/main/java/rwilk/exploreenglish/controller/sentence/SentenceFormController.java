package rwilk.exploreenglish.controller.sentence;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class SentenceFormController implements Initializable {

  private SentenceController sentenceController;

  public TextField textFieldId;
  public ComboBox<Word> comboBoxWord;
  public TextField textFieldEnglishName;
  public TextField textFieldPolishName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(SentenceController sentenceController) {
    this.sentenceController = sentenceController;
    initializeWordComboBox();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      sentenceController.getSentenceService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(sentence -> sentenceController.getSentenceService().delete(sentence));
      buttonClearOnAction(actionEvent);
      sentenceController.refreshTableView();
    }
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    comboBoxWord.getSelectionModel().select(null);
    textFieldEnglishName.clear();
    textFieldPolishName.clear();
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldId.getText())
        && StringUtils.isNoneEmpty(textFieldEnglishName.getText())
        && StringUtils.isNoneEmpty(textFieldPolishName.getText())
        && comboBoxWord.getSelectionModel().getSelectedItem() != null) {
      sentenceController.getSentenceService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(sentence -> {
            sentence.setEnglishName(textFieldEnglishName.getText());
            sentence.setPolishName(textFieldPolishName.getText());
            sentence.setWord(comboBoxWord.getSelectionModel().getSelectedItem());
            sentence = sentenceController.getSentenceService().save(sentence);
            setSentenceForm(sentence);
            sentenceController.refreshTableView();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldEnglishName.getText())
        && StringUtils.isNoneEmpty(textFieldPolishName.getText())
        && comboBoxWord.getSelectionModel().getSelectedItem() != null) {
      Sentence sentence = Sentence.builder()
          .englishName(textFieldEnglishName.getText())
          .polishName(textFieldPolishName.getText())
          .word(comboBoxWord.getSelectionModel().getSelectedItem())
          .build();
      sentence = sentenceController.getSentenceService().save(sentence);
      setSentenceForm(sentence);
      sentenceController.refreshTableView();
    }
  }

  public void setSentenceForm(Sentence sentence) {
    textFieldId.setText(sentence.getId().toString());
    textFieldEnglishName.setText(sentence.getEnglishName());
    textFieldPolishName.setText(sentence.getPolishName());
    comboBoxWord.getSelectionModel().select(sentence.getWord());
  }

  public void initializeWordComboBox() {
    List<Word> words = sentenceController.getWordService().getAll();
    comboBoxWord.setItems(FXCollections.observableArrayList(words));
  }

}
