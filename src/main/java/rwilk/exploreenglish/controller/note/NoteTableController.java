package rwilk.exploreenglish.controller.note;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Note;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class NoteTableController implements Initializable {

  private NoteController noteController;
  private List<Note> notes;

  public TextField textFieldSearch;
  public TableView<Note> tableNotes;
  public TableColumn<Note, Long> columnId;
  public TableColumn<Note, String> columnNote;
  public TableColumn<Note, String> columnLesson;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTableByLesson(newValue));
    textFieldSearch.setOnMouseClicked(view -> filterTableByLesson(textFieldSearch.getText()));

  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableNotes.widthProperty().multiply(0.1));
    columnNote.prefWidthProperty().bind(tableNotes.widthProperty().multiply(0.6));
    columnLesson.prefWidthProperty().bind(tableNotes.widthProperty().multiply(0.3));
  }

  public void init(NoteController noteController) {
    this.noteController = noteController;
    fillInTableView();
  }

  public void fillInTableView() {
    notes = noteController.getNoteService().getAll();
    tableNotes.setItems(FXCollections.observableArrayList(notes));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (tableNotes.getSelectionModel().getSelectedItem() != null) {
      Note selectedNote = tableNotes.getSelectionModel().getSelectedItem();
      noteController.setNoteForm(selectedNote);
    }
  }

  private void filterTableByLesson(String value) {
    List<Note> filtered = notes.stream()
        .filter(note ->
            note.getLesson().getEnglishName().toLowerCase().contains(value.toLowerCase())
                || note.getLesson().getPolishName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableNotes.setItems(FXCollections.observableArrayList(filtered));
  }
}
