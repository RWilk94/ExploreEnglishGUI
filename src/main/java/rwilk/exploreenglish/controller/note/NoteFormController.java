package rwilk.exploreenglish.controller.note;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Note;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class NoteFormController implements Initializable {

  private static final int MAX_LENGTH = 2000;
  private NoteController noteController;
  public TextField textFieldId;
  public ComboBox<Lesson> comboBoxLesson;
  public TextArea textAreaNote;
  public TextField textFieldTitle;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    textAreaNote.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > MAX_LENGTH) {
        textAreaNote.setStyle("-fx-background-color: #EF1212");
      } else {
        textAreaNote.setStyle("");
      }
    });
  }

  public void init(NoteController noteController) {
    this.noteController = noteController;
    textAreaNote.setWrapText(true);
    initializeLessonComboBox();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      noteController.getNoteService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(lesson -> noteController.getNoteService().delete(lesson));
      buttonClearOnAction(actionEvent);
      noteController.refreshTableView();
    }
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    comboBoxLesson.getSelectionModel().select(null);
    textAreaNote.clear();
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText()) && StringUtils.isNoneEmpty(textAreaNote.getText())
        && comboBoxLesson.getSelectionModel().getSelectedItem() != null) {
      noteController.getNoteService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(note -> {
            note.setNote(textAreaNote.getText());
            note.setLesson(comboBoxLesson.getSelectionModel().getSelectedItem());
            note = noteController.getNoteService().save(note);
            setNoteForm(note);
            noteController.refreshTableView();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textAreaNote.getText())
        && comboBoxLesson.getSelectionModel().getSelectedItem() != null) {
      Note note = Note.builder()
          .note(textAreaNote.getText())
          .position(noteController.getNoteService().getCountByLesson(comboBoxLesson.getSelectionModel().getSelectedItem()))
          .lesson(comboBoxLesson.getSelectionModel().getSelectedItem())
          .build();
      note = noteController.getNoteService().save(note);
      setNoteForm(note);
      noteController.refreshTableView();
    }
  }

  public void setNoteForm(Note note) {
    textFieldId.setText(note.getId().toString());
    textAreaNote.setText(note.getNote());
    comboBoxLesson.getSelectionModel().select(note.getLesson());
  }

  public void initializeLessonComboBox() {
    List<Lesson> lessons = noteController.getLessonService().getAll();
    comboBoxLesson.setItems(FXCollections.observableArrayList(lessons));
  }

  public void buttonAddWordsOnAction(ActionEvent actionEvent) {
    Lesson selectedLesson = comboBoxLesson.getSelectionModel().getSelectedItem();
    if (selectedLesson != null) {
      List<LessonWord> lessonWords = noteController.getLessonWordService()
          .getAllByLesson(selectedLesson).stream()
          .sorted(Comparator.comparing(LessonWord::getPosition))
          .collect(Collectors.toList());
      for (LessonWord lessonWord : lessonWords) {
        if (StringUtils.isNoneEmpty(textAreaNote.getText())) {
          textAreaNote.setText(textAreaNote.getText() + getWordTag(lessonWord));
        } else {
          textAreaNote.setText(getWordTag(lessonWord));
        }
      }
    }
  }

  private String getWordTag(LessonWord lessonWord) {
    return "\n" + "<word id=" + lessonWord.getWord().getId() + ">" + lessonWord.getWord().getEnglishNames() + " = " + lessonWord.getWord().getPolishName() + "</word>";
  }

  public void buttonAddTitleOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldTitle.getText())) {
      if (StringUtils.isNoneEmpty(textAreaNote.getText())) {
        textAreaNote.setText(textAreaNote.getText() + "\n<b>" + textFieldTitle.getText() + "</b>");
      } else {
        textAreaNote.setText("<b>" + textFieldTitle.getText() + "</b>");
      }
    }
  }

  public void buttonBoldOnAction(ActionEvent actionEvent) {
    final String selectedText = textAreaNote.getSelectedText().trim();
    final String text = textAreaNote.getText();
    if (StringUtils.isNoneEmpty(selectedText, text)) {
      textAreaNote.setText(text.replace(selectedText, "<b>".concat(selectedText).concat("</b>")));
    }
  }

  public void buttonItalicsOnAction(ActionEvent actionEvent) {
    final String selectedText = textAreaNote.getSelectedText().trim();
    final String text = textAreaNote.getText();
    if (StringUtils.isNoneEmpty(selectedText, text)) {
      textAreaNote.setText(text.replace(selectedText, "<i>".concat(selectedText).concat("</i>")));
    }
  }

  public void buttonWordTagOnAction(ActionEvent actionEvent) {
    final String selectedText = textAreaNote.getSelectedText().trim();
    final String text = textAreaNote.getText();
    if (StringUtils.isNoneEmpty(selectedText, text)) {
      textAreaNote.setText(text.replace(selectedText, "<word>".concat(selectedText).concat("</word>")));
    }
  }
}
