package rwilk.exploreenglish.controller.course;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.service.CourseService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Controller
public class CourseController implements Initializable {

  private final CourseService courseService;
  private CourseFormController courseFormController;
  private CourseTableController courseTableController;

  public AnchorPane anchorPaneCourseForm;
  public AnchorPane anchorPaneCourseTable;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  private void init() {
    try {
      FXMLLoader fxmlLoaderCourseForm = new FXMLLoader();
      fxmlLoaderCourseForm.setLocation(getClass().getResource("/scene/course/course_form.fxml"));
      VBox form = fxmlLoaderCourseForm.load();
      courseFormController = fxmlLoaderCourseForm.getController();

      FXMLLoader fxmlLoaderCourseTable = new FXMLLoader();
      fxmlLoaderCourseTable.setLocation(getClass().getResource("/scene/course/course_table.fxml"));
      VBox table = fxmlLoaderCourseTable.load();
      courseTableController = fxmlLoaderCourseTable.getController();

      courseFormController.init(this, courseService);
      courseTableController.init(this, courseService);

      anchorPaneCourseForm.getChildren().add(form);
      anchorPaneCourseTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void refreshTableView() {
    courseTableController.fillInTableView();
  }

  public void setCourseForm(Course course) {
    courseFormController.setCourseForm(course);
  }

  public CourseFormController getCourseFormController() {
    return courseFormController;
  }

  public CourseTableController getCourseTableController() {
    return courseTableController;
  }
}
