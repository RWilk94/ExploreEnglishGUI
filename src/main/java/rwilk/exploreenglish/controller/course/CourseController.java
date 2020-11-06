package rwilk.exploreenglish.controller.course;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.service.CourseService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class CourseController implements Initializable {

  private final CourseService courseService;
  private CourseFormController courseFormController;
  private CourseTableController courseTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  private void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/course/course_form.fxml"));
      VBox form = fxmlLoaderForm.load();
      courseFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/course/course_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      courseTableController = fxmlLoaderTable.getController();

      courseFormController.init(this);
      courseTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
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

  public CourseService getCourseService() {
    return courseService;
  }

}
