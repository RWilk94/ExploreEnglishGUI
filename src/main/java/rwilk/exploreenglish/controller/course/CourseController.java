package rwilk.exploreenglish.controller.course;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.export.ExportService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class CourseController implements Initializable {

  private final InjectService injectService;
  private final CourseService courseService;
  private final ExportService exportService;
  private CourseFormController courseFormController;
  private CourseTableController courseTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public CourseController(InjectService injectService, CourseService courseService, ExportService exportService) {
    this.injectService = injectService;
    this.courseService = courseService;
    this.exportService = exportService;
    injectService.setCourseController(this);
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

  /**
   * Refresh child view after delete course.
   */
  public void refreshChildTableView() {
    injectService.getLessonController().refreshTableView();
    injectService.getLessonController().refreshCourseComboBox();
    injectService.getWordController().refreshTableView();
    injectService.getWordController().refreshLessonComboBox();
    injectService.getSentenceController().refreshTableView();
    injectService.getSentenceController().refreshWordComboBox();
    injectService.getNoteController().refreshTableView();
    injectService.getNoteController().refreshLessonComboBox();
    injectService.getExerciseController().refreshTableView();
    injectService.getExerciseController().refreshLessonComboBox();
    injectService.getExerciseItemController().refreshTableView();
    injectService.getExerciseItemController().refreshExerciseComboBox();
    injectService.getViewController().refreshListViewCourses();
  }

  /**
   * Refresh child view after add or edit course.
   */
  public void refreshChildComboBoxes() {
    injectService.getLessonController().refreshCourseComboBox();
    injectService.getViewController().refreshListViewCourses();
  }

  public void setCourseForm(Course course) {
    courseFormController.setCourseForm(course);
  }

  public CourseService getCourseService() {
    return courseService;
  }

  public ExportService getExportService() {
    return exportService;
  }
}
