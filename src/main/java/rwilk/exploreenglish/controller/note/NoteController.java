package rwilk.exploreenglish.controller.note;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.NoteService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class NoteController implements Initializable {

  private final InjectService injectService;
  private final LessonService lessonService;
  private final NoteService noteService;
  private NoteFormController noteFormController;
  private NoteTableController noteTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public NoteController(InjectService injectService, LessonService lessonService, NoteService noteService) {
    this.injectService = injectService;
    this.lessonService = lessonService;
    this.noteService = noteService;
    injectService.setNoteController(this);
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
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshLessonComboBox() {
    noteFormController.initializeLessonComboBox();
  }

  public void setLessonComboBox(Lesson lesson) {
    noteFormController.comboBoxLesson.getSelectionModel().select(lesson);
  }

  public LessonService getLessonService() {
    return lessonService;
  }

  public NoteService getNoteService() {
    return noteService;
  }

  public InjectService getInjectService() {
    return injectService;
  }
}
