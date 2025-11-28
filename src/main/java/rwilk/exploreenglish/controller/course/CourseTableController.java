package rwilk.exploreenglish.controller.course;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.migration.entity.FinalCourse;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class CourseTableController implements Initializable {

  private CourseController courseController;
  private List<FinalCourse> courses;

  public TextField textFieldSearch;
  public TableView<FinalCourse> tableCourses;
  public TableColumn<FinalCourse, Long> columnId;
  public TableColumn<FinalCourse, String> columnName;
  public TableColumn<FinalCourse, String> columnDescription;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldSearch.setOnMouseClicked(view -> filterTableByName(textFieldSearch.getText()));
  }

  public void init(final rwilk.exploreenglish.controller.course.CourseController courseController) {
    this.courseController = courseController;
    fillInTableView();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.1));
    columnName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.45));
    columnDescription.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.45));
  }

  public void fillInTableView() {
    courses = courseController.getFinalCourseService().getAll();
    tableCourses.setItems(FXCollections.observableArrayList(courses));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (tableCourses.getSelectionModel().getSelectedItem() != null) {
      FinalCourse selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
      courseController.setCourseForm(selectedCourse);
    }
  }

  private void filterTableByName(String value) {
    List<FinalCourse> filteredCourses = courses.stream()
                                          .filter(course ->
                                                    course.getName().toLowerCase().contains(value.toLowerCase())
                                                    || course.getDescription().toLowerCase().contains(value.toLowerCase()))
                                          .collect(Collectors.toList());
    tableCourses.setItems(FXCollections.observableArrayList(filteredCourses));
  }

}
