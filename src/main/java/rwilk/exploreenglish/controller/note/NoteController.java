package rwilk.exploreenglish.controller.note;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.NoteService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class NoteController implements Initializable {

  private final LessonService lessonService;
  private final NoteService noteService;
  private NoteFormController noteFormController;
  private NoteTableController noteTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public NoteController(LessonService lessonService, NoteService noteService) {
    this.lessonService = lessonService;
    this.noteService = noteService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/note/note_form.fxml"));
      VBox form = fxmlLoaderForm.load();
      noteFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/note/note_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      noteTableController = fxmlLoaderTable.getController();

      noteFormController.init(this);
      noteTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setNoteForm(Note note) {
    noteFormController.setNoteForm(note);
  }

  public void refreshTableView() {
    noteTableController.fillInTableView();
  }

  public LessonService getLessonService() {
    return lessonService;
  }

  public NoteService getNoteService() {
    return noteService;
  }

}
