package rwilk.exploreenglish.controller.lesson;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Lesson;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class LessonTableController implements Initializable {

  private LessonController lessonController;
  private List<Lesson> lessons;

  public TextField textFieldSearch;
  public TableView<Lesson> tableLessons;
  public TableColumn<Lesson, Long> columnId;
  public TableColumn<Lesson, String> columnEnName;
  public TableColumn<Lesson, String> columnPlName;
  public TableColumn<Lesson, String> columnCourse;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldSearch.setOnMouseClicked(view -> filterTableByName(textFieldSearch.getText()));
  }

  public void init(LessonController lessonController) {
    this.lessonController = lessonController;
    fillInTableView();
  }

  public void fillInTableView() {
    new Thread(() -> {
      lessons = lessonController.getLessonService().getAll();

      Platform.runLater(() -> tableLessons.setItems(FXCollections.observableArrayList(lessons)));
    }).start();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.1));
    columnEnName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnPlName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnCourse.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (tableLessons.getSelectionModel().getSelectedItem() != null) {
      Lesson selectedLesson = tableLessons.getSelectionModel().getSelectedItem();
      lessonController.setLessonForm(selectedLesson);
    }
  }

  private void filterTableByName(String value) {
    List<Lesson> filteredCourses = lessons.stream()
        .filter(lesson ->
            lesson.getEnglishName().toLowerCase().contains(value.toLowerCase())
                || lesson.getPolishName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableLessons.setItems(FXCollections.observableArrayList(filteredCourses));
  }

}
