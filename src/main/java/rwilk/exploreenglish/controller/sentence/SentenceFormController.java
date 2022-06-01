package rwilk.exploreenglish.controller.sentence;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSentence;
import rwilk.exploreenglish.utils.SoundUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class SentenceFormController implements Initializable {

  private SentenceController sentenceController;

  public TextField textFieldId;
  public ComboBox<Word> comboBoxWord;
  public TextField textFieldEnglishName;
  public TextField textFieldPolishName;
  public TextField textFieldSound;
  public ListView<Word> listViewWords;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    textFieldEnglishName.textProperty()
                        .addListener((observable, oldValue, newValue) -> {
                                       if (newValue.contains("(") && newValue.contains(")")) {
                                         final String plName = newValue.substring(newValue.indexOf("(") + 1, newValue.indexOf(")"));
                                         textFieldPolishName.setText(plName.trim());
                                         newValue = newValue.replace(plName, "")
                                                            .replace("(", "")
                                                            .replace(")", "");
                                         textFieldEnglishName.setText(newValue.trim());
                                         return;
                                       }
                                       if (newValue.contains("https://www")) {
                                         final String sound = newValue.substring(newValue.lastIndexOf("https://www")).trim();
                                         if (StringUtils.isBlank(textFieldSound.getText())) {
                                           textFieldSound.setText(sound.trim());
                                         } else if (!StringUtils.trimToEmpty(textFieldSound.getText()).contains(sound)) {
                                           textFieldSound.setText(textFieldSound.getText()
                                                                                .concat("; ")
                                                                                .concat(sound)
                                                                                .trim());
                                         }
                                         newValue = newValue.replace(sound, "");
                                         textFieldEnglishName.setText(newValue.trim());
                                       }
                                     }
                                    );
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
    textFieldSound.clear();
    listViewWords.setItems(null);
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldId.getText())
        && StringUtils.isNoneEmpty(textFieldEnglishName.getText())
        && StringUtils.isNoneEmpty(textFieldPolishName.getText())) {
      sentenceController.getSentenceService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(sentence -> {
            sentence.setEnglishName(StringUtils.trimToEmpty(textFieldEnglishName.getText()));
            sentence.setPolishName(StringUtils.trimToEmpty(textFieldPolishName.getText()));
            sentence.setSound(StringUtils.trimToEmpty(textFieldSound.getText()));
            sentence = sentenceController.getSentenceService().save(sentence);
            setSentenceForm(sentence);
            sentenceController.refreshTableView();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldEnglishName.getText())
        && StringUtils.isNoneEmpty(textFieldPolishName.getText())) {
      Sentence sentence = Sentence.builder()
          .englishName(StringUtils.trimToEmpty(textFieldEnglishName.getText()))
          .polishName(StringUtils.trimToEmpty(textFieldPolishName.getText()))
          .sound(StringUtils.trimToEmpty(textFieldSound.getText()))
          .build();
      sentence = sentenceController.getSentenceService().save(sentence);
      setSentenceForm(sentence);

      if (comboBoxWord.getSelectionModel().getSelectedItem() != null) {
        WordSentence wordSentence = WordSentence.builder()
            .id(null)
            .position(sentenceController.getWordSentenceService().getCountByWord(comboBoxWord.getSelectionModel().getSelectedItem()))
            .word(comboBoxWord.getSelectionModel().getSelectedItem())
            .sentence(sentence)
            .build();
        sentenceController.getWordSentenceService().save(wordSentence);
        setWordSentenceForm();
      }

      sentenceController.refreshTableView();
    }
  }

  public void setSentenceForm(Sentence sentence) {
    textFieldId.setText(sentence.getId().toString());
    textFieldEnglishName.setText(sentence.getEnglishName());
    textFieldPolishName.setText(sentence.getPolishName());
    textFieldSound.setText(sentence.getSound());
    setWordSentenceForm();
  }

  public void initializeWordComboBox() {
    new Thread(() -> {
      List<Word> words = sentenceController.getWordService().getAll();

      Platform.runLater(() -> comboBoxWord.setItems(FXCollections.observableArrayList(words)));
    }).start();
  }

  private void setWordSentenceForm() {
    if (StringUtils.isNoneEmpty(textFieldId.getText())) {
      sentenceController.getSentenceService().getById(Long.parseLong(textFieldId.getText()))
          .ifPresent(sentence -> {
            List<WordSentence> wordSentences = sentenceController.getWordSentenceService().getAllBySentence(sentence);
            listViewWords.setItems(null);
            listViewWords.setItems(FXCollections.observableArrayList(wordSentences.stream()
                .map(WordSentence::getWord)
                .collect(Collectors.toList())));
          });
    }

  }

  public void buttonAddWordOnAction(ActionEvent actionEvent) {
    if (comboBoxWord.getSelectionModel().getSelectedItem() != null
        && StringUtils.isNoneEmpty(textFieldId.getText())) {
      Word word = comboBoxWord.getSelectionModel().getSelectedItem();
      Long sentenceId = Long.parseLong(textFieldId.getText());

      sentenceController.getSentenceService().getById(sentenceId)
          .ifPresent(sentence -> {
            WordSentence wordSentence = sentenceController.getWordSentenceService().save(
                WordSentence.builder()
                    .position(sentenceController.getWordSentenceService().getCountByWord(comboBoxWord.getSelectionModel().getSelectedItem()))
                    .word(word)
                    .sentence(sentence)
                    .build());
            setWordSentenceForm();
            sentenceController.refreshTableView();
          });
    }
  }

  public void buttonRemoveWordOnAction(ActionEvent actionEvent) {
    if (comboBoxWord.getSelectionModel().getSelectedItem() != null
        && StringUtils.isNoneEmpty(textFieldId.getText())) {
      Word word = comboBoxWord.getSelectionModel().getSelectedItem();
      Long sentenceId = Long.parseLong(textFieldId.getText());

      sentenceController.getWordSentenceService().getByWordIdAndSentenceId(word.getId(), sentenceId)
          .ifPresent(wordSentence -> sentenceController.getWordSentenceService().deleteById(wordSentence.getId()));
      setWordSentenceForm();
      sentenceController.refreshTableView();
    }
  }

  public void listViewWordsOnMouseClicked(MouseEvent mouseEvent) {
    Word selectedItem = listViewWords.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      comboBoxWord.getSelectionModel().select(selectedItem);
    }
  }

  public void downloadMp3(final ActionEvent actionEvent) {
    final String soundText = textFieldSound.getText();
    if (StringUtils.isNotBlank(soundText)) {
      final List<String> sounds = Arrays.stream(soundText.split(";")).map(StringUtils::trimToEmpty).collect(
              Collectors.toList());
      new Thread(() -> {

        for (String sound : sounds) {
          SoundUtils.downloadFile(sound.trim());
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

      }).start();
    }
  }

}
