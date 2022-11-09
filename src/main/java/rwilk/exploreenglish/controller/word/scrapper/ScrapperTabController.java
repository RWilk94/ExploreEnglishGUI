package rwilk.exploreenglish.controller.word.scrapper;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import rwilk.exploreenglish.controller.word.WordFormController;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.utils.SoundUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Controller
public class ScrapperTabController implements Initializable {

  private InjectService injectService;
  public TextField textFieldEnglishName;
  public TextField textFieldAmericanName;
  public TextField textFieldOtherNames;
  public ListView<String> listViewMeaning;
  public ListView<String> listViewSentences;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPlural;
  public TextField textFieldSynonym;
  public TextField textFieldPartOfSpeech;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(Term term, InjectService injectService) {
    this.injectService = injectService;

    textFieldEnglishName.setText(StringUtils.trimToEmpty(term.getEnglishName()));
    textFieldAmericanName.setText(StringUtils.trimToEmpty(term.getAmericanName()));
    textFieldOtherNames.setText(StringUtils.trimToEmpty(term.getOtherName()));
    textFieldPastTense.setText(StringUtils.trimToEmpty(term.getPastTense()));
    textFieldPastParticiple.setText(StringUtils.trimToEmpty(term.getPastParticiple()));
    textFieldComparative.setText(StringUtils.trimToEmpty(term.getComparative()));
    textFieldSuperlative.setText(StringUtils.trimToEmpty(term.getSuperlative()));
    textFieldPlural.setText(StringUtils.trimToEmpty(term.getPlural()));
    textFieldPartOfSpeech.setText(StringUtils.trimToEmpty(term.getPartOfSpeech()));
    textFieldSynonym.setText(StringUtils.trimToEmpty(term.getSynonym()));

    listViewMeaning.setItems(FXCollections.observableArrayList(
        Arrays.stream(StringUtils.split(StringUtils.trimToEmpty(term.getPolishName()), ";"))
              .map(StringUtils::trim)
              .toList()));

    if (StringUtils.isNoneEmpty(term.getEnglishSentence()) && StringUtils.isNoneEmpty(term.getPolishSentence())) {
      String[] split = StringUtils.split(term.getEnglishSentence(), ";");
      String[] split1 = StringUtils.split(term.getPolishSentence().replace(".mp3; https://", ".mp3 https://"), ";");
      List<String> sentences = new ArrayList<>();
      if (split.length == split1.length) {
        for (int i = 0; i < split.length; i++) {
          sentences.add(split[i].trim() + " " + split1[i].trim());
        }
      }

      listViewSentences.setItems(FXCollections.observableArrayList(sentences));
    }
  }

  public void listViewMeaningOnMouseClicked(MouseEvent mouseEvent) {
    String meaning = listViewMeaning.getSelectionModel().getSelectedItem();
    if (meaning != null) {
      injectService.getWordController().setMeaningAndProperties(meaning, textFieldPartOfSpeech.getText());
    }
  }

  public void buttonLoadDataOnAction(ActionEvent actionEvent) {
  }

  public void downloadEnglishMp3(final ActionEvent actionEvent) {
    final String fieldText = textFieldOtherNames.getText();
    if (StringUtils.isNotBlank(fieldText) && fieldText.contains("https://www")) {
      String trimmedText = fieldText.substring(fieldText.indexOf("https"), fieldText.lastIndexOf("]"));
      if (trimmedText.contains("?version")) {
        trimmedText = trimmedText.substring(0, trimmedText.indexOf("?version"));
      }
      SoundUtils.downloadFile(trimmedText);

      final ClipboardContent content = new ClipboardContent();
      content.putString(trimmedText);
      Clipboard.getSystemClipboard().setContent(content);
    }

  }

  public void downloadAmericanMp3(final ActionEvent actionEvent) {
    final String fieldText = textFieldAmericanName.getText();
    if (StringUtils.isNotBlank(fieldText) && fieldText.contains("https://www")) {
      String trimmedText = fieldText.substring(fieldText.indexOf("https"), fieldText.lastIndexOf("]"));
      if (trimmedText.contains("?version")) {
        trimmedText = trimmedText.substring(0, trimmedText.indexOf("?version"));
      }
      SoundUtils.downloadFile(trimmedText);

      final ClipboardContent content = new ClipboardContent();
      content.putString(trimmedText);
      Clipboard.getSystemClipboard().setContent(content);
    }
  }

  public void listViewSentenceOnMouseClicked(final MouseEvent mouseEvent) {
    String selectedSentence = listViewSentences.getSelectionModel().getSelectedItem();
    if (StringUtils.isNotBlank(selectedSentence)) {
      final String enSentence = selectedSentence.substring(0, selectedSentence.indexOf("(")).trim();
      final String plSentence = selectedSentence.substring(selectedSentence.indexOf("(") + 1, selectedSentence.indexOf(")")).trim();
      final WordFormController wordFormController = injectService.getWordController().getWordFormController();
      wordFormController.buttonClearWordSoundOnAction();
      wordFormController.getTextFieldEnglishName().setText(enSentence);
      wordFormController.getTextFieldAdditionalInformation().setText(plSentence);
      wordFormController.getComboBoxWordType().getSelectionModel().select(WordTypeEnum.SENTENCE);

      if (selectedSentence.contains("https://www")) {
        final String trimmedText = selectedSentence.substring(selectedSentence.indexOf("https"));
        SoundUtils.downloadFile(trimmedText);

        final ClipboardContent content = new ClipboardContent();
        content.putString(trimmedText);
        Clipboard.getSystemClipboard().setContent(content);

        if (trimmedText.contains("en-ame")) {
          wordFormController.getTextFieldAmericanSound().setText(trimmedText);
        } else {
          wordFormController.getTextFieldBritishSound().setText(trimmedText);
        }
      }
    }
  }

}
