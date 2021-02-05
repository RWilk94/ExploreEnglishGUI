package rwilk.exploreenglish.controller.word;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.PartOfSpeechEnum;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.WordService;
import rwilk.exploreenglish.utils.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class WordController implements Initializable {

  private final InjectService injectService;
  private final LessonService lessonService;
  private final WordService wordService;
  private final LessonWordService lessonWordService;
  public TabPane tabPane;
  private WordFormController wordFormController;
  private WordTableController wordTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public WordController(InjectService injectService, LessonService lessonService, WordService wordService,
                        LessonWordService lessonWordService) {
    this.injectService = injectService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.lessonWordService = lessonWordService;
    injectService.setWordController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/word/word_form.fxml"));
      ScrollPane form = fxmlLoaderForm.load();
      wordFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/word/word_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      wordTableController = fxmlLoaderTable.getController();

      wordFormController.init(this);
      wordTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setWordForm(Word word) {
    wordFormController.setWordForm(word);
    wordFormController.translate(false);
  }

  public void setWordForm(Term term) {
    wordFormController.setWordForm(term);
    wordFormController.translate(false);
  }

  public void refreshTableView() {
    wordTableController.fillInTableView();
  }

  public void refreshChildTableView() {
    injectService.getSentenceController().refreshTableView();
    injectService.getSentenceController().refreshWordComboBox();
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshChildComboBoxes() {
    injectService.getSentenceController().refreshWordComboBox();
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshLessonComboBox() {
    wordFormController.initializeLessonComboBox();
  }

  public void setLessonComboBox(Lesson lesson) {
    wordFormController.comboBoxLesson.getSelectionModel().select(lesson);
  }

  public void setMeaningAndProperties(String text, String partOfSpeech) {
    if (StringUtils.isNoneEmpty(text)) {
      wordFormController.textFieldSynonym.setText(WordUtils.extractSynonym(text));
      wordFormController.textFieldOpposite.setText(WordUtils.extractOpposite(text));

      String extractedPartOfSpeech = WordUtils.extractPartOfSpeech(partOfSpeech);
      Toggle toggleButtonPOS = wordFormController.getToggleGroupPartOfSpeech().getToggles().stream()
          .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(extractedPartOfSpeech))
          .findFirst()
          .orElse(null);
      wordFormController.getToggleGroupPartOfSpeech().selectToggle(toggleButtonPOS);

      String grammarTag = WordUtils.extractGrammarTag(text);
      Toggle toggleButtonGT = wordFormController.getToggleGroupGrammar().getToggles().stream()
          .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(grammarTag))
          .findFirst()
          .orElse(null);
      if (extractedPartOfSpeech.equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
          && toggleButtonGT != null && !toggleButtonGT.getUserData().toString().isEmpty()) {
        wordFormController.getToggleGroupGrammar().selectToggle(toggleButtonGT);
        if (toggleButtonGT.getUserData().toString().equals("countable and uncountable")) {
          wordFormController.getToggleGroupArticle().selectToggle(
              wordFormController.getToggleGroupArticle().getToggles().stream()
                  .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(""))
                  .findFirst()
                  .orElse(null));
        }
      } else {
        wordFormController.getToggleGroupGrammar().selectToggle(wordFormController.getToggleGroupGrammar().getToggles()
            .stream()
            .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(""))
            .findFirst()
            .orElse(null));
      }
      if (text.contains("[")) {
        wordFormController.textFieldPolishName.setText(text.substring(0, text.indexOf("[")).trim());
      } else {
        wordFormController.textFieldPolishName.setText(text);
      }
    }
  }

  public LessonService getLessonService() {
    return lessonService;
  }

  public WordService getWordService() {
    return wordService;
  }

  public LessonWordService getLessonWordService() {
    return lessonWordService;
  }

  public WordTableController getWordTableController() {
    return wordTableController;
  }

  public WordFormController getWordFormController() {
    return wordFormController;
  }

  public InjectService getInjectService() {
    return injectService;
  }

  public TabPane getTabPane() {
    return tabPane;
  }
}
