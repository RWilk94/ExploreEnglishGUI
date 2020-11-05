package rwilk.exploreenglish.controller.course;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class CourseTableController implements Initializable {

  private CourseController courseController;
  private List<Course> courses;

  public TextField textFieldSearch;
  public TableView<Course> tableCourses;
  public TableColumn<Course, Long> columnId;
  public TableColumn<Course, String> columnEnglishName;
  public TableColumn<Course, String> columnPolishName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldSearch.setOnMouseClicked(view -> filterTableByName(textFieldSearch.getText()));
  }

  public void init(CourseController courseController) {
    this.courseController = courseController;
    fillInTableView();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.1));
    columnEnglishName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.45));
    columnPolishName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.45));
  }

  public void fillInTableView() {
    courses = courseController.getCourseService().getAll();
    tableCourses.setItems(FXCollections.observableArrayList(courses));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableCourses.getSelectionModel().isEmpty()) {
      Course selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
      courseController.setCourseForm(selectedCourse);
    }
  }

  private void filterTableByName(String value) {
    List<Course> filteredCourses = courses.stream()
        .filter(course ->
            course.getEnglishName().toLowerCase().contains(value.toLowerCase())
                || course.getPolishName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableCourses.setItems(FXCollections.observableArrayList(filteredCourses));
  }

}
