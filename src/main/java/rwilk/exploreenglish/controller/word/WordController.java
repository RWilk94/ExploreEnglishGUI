package rwilk.exploreenglish.controller.word;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.WordService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class WordController implements Initializable {

  private final LessonService lessonService;
  private final WordService wordService;
  private WordFormController wordFormController;
  private WordTableController wordTableController;

  public AnchorPane anchorPaneWordForm;
  public AnchorPane anchorPaneWordTable;

  public WordController(LessonService lessonService, WordService wordService) {
    this.lessonService = lessonService;
    this.wordService = wordService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderLessonForm = new FXMLLoader();
      fxmlLoaderLessonForm.setLocation(getClass().getResource("/scene/word/word_form.fxml"));
      ScrollPane form = fxmlLoaderLessonForm.load();
      wordFormController = fxmlLoaderLessonForm.getController();

      FXMLLoader fxmlLoaderLessonTable = new FXMLLoader();
      fxmlLoaderLessonTable.setLocation(getClass().getResource("/scene/word/word_table.fxml"));
      VBox table = fxmlLoaderLessonTable.load();
      wordTableController = fxmlLoaderLessonTable.getController();

      wordFormController.init(this);
      wordTableController.init(this);

      anchorPaneWordForm.getChildren().add(form);
      anchorPaneWordTable.getChildren().add(table);
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

  public LessonService getLessonService() {
    return lessonService;
  }

  public WordService getWordService() {
    return wordService;
  }

}
