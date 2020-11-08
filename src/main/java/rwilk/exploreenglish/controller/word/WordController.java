package rwilk.exploreenglish.controller.word;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.WordService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class WordController implements Initializable {

  private final InjectService injectService;
  private final LessonService lessonService;
  private final WordService wordService;
  private WordFormController wordFormController;
  private WordTableController wordTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public WordController(InjectService injectService, LessonService lessonService, WordService wordService) {
    this.injectService = injectService;
    this.lessonService = lessonService;
    this.wordService = wordService;
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
  }

  public void refreshTableView() {
    wordTableController.fillInTableView();
  }

  public void refreshChildTableView() {
    injectService.getSentenceController().refreshTableView();
    injectService.getSentenceController().refreshWordComboBox();
  }

  public void refreshChildComboBoxes() {
    injectService.getSentenceController().refreshWordComboBox();
  }

  public void refreshLessonComboBox() {
    wordFormController.initializeLessonComboBox();
  }

  public LessonService getLessonService() {
    return lessonService;
  }

  public WordService getWordService() {
    return wordService;
  }

}
