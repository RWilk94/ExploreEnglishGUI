package rwilk.exploreenglish.controller.course;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.export.DocService;
import rwilk.exploreenglish.service.export.ExportDocumentService;
import rwilk.exploreenglish.service.export.ExportService;

@Controller
public class CourseController implements Initializable {

  private final InjectService injectService;
  private final CourseService courseService;
  private final ExportService exportService;
  private final DocService docService;
  private final ExportDocumentService exportDocumentService;
  private CourseFormController courseFormController;
  private CourseTableController courseTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public CourseController(final InjectService injectService, final CourseService courseService,
                          final ExportService exportService, final DocService docService,
                          final ExportDocumentService exportDocumentService) {
    this.injectService = injectService;
    this.courseService = courseService;
    this.exportService = exportService;
    this.docService = docService;
    this.exportDocumentService = exportDocumentService;
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

  public DocService getDocService() {
    return docService;
  }

  public ExportDocumentService getExportDocumentService() {
    return exportDocumentService;
  }
}
