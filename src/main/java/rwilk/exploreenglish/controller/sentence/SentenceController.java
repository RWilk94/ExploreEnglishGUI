package rwilk.exploreenglish.controller.sentence;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.SentenceService;
import rwilk.exploreenglish.service.WordSentenceService;
import rwilk.exploreenglish.service.WordService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class SentenceController implements Initializable {

  private final InjectService injectService;
  private final WordService wordService;
  private final SentenceService sentenceService;
  private final WordSentenceService wordSentenceService;
  private SentenceFormController sentenceFormController;
  private SentenceTableController sentenceTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public SentenceController(InjectService injectService, WordService wordService, SentenceService sentenceService,
                            WordSentenceService wordSentenceService) {
    this.injectService = injectService;
    this.wordService = wordService;
    this.sentenceService = sentenceService;
    this.wordSentenceService = wordSentenceService;
    injectService.setSentenceController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/sentence/sentence_form.fxml"));
      VBox form = fxmlLoaderForm.load();
      sentenceFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/sentence/sentence_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      sentenceTableController = fxmlLoaderTable.getController();

      sentenceFormController.init(this);
      sentenceTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setSentenceForm(Sentence sentence) {
    sentenceFormController.setSentenceForm(sentence);
  }

  public void refreshTableView() {
    sentenceTableController.fillInTableView();
    injectService.getViewController().refreshListViewLessonItemChildren();
  }

  public void refreshWordComboBox() {
    sentenceFormController.initializeWordComboBox();
  }

  public void setWordComboBox(Word word) {
    sentenceFormController.comboBoxWord.getSelectionModel().select(word);
  }

  public WordService getWordService() {
    return wordService;
  }

  public WordSentenceService getWordSentenceService() {
    return wordSentenceService;
  }

  public SentenceService getSentenceService() {
    return sentenceService;
  }
}
