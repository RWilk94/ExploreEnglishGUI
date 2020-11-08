package rwilk.exploreenglish.controller.lesson;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class LessonController implements Initializable {

  private final InjectService injectService;
  private final CourseService courseService;
  private final LessonService lessonService;
  private LessonFormController lessonFormController;
  private LessonTableController lessonTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public LessonController(InjectService injectService, CourseService courseService, LessonService lessonService) {
    this.injectService = injectService;
    this.courseService = courseService;
    this.lessonService = lessonService;
    injectService.setLessonController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/lesson/lesson_form.fxml"));
      VBox form = fxmlLoaderForm.load();
      lessonFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/lesson/lesson_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      lessonTableController = fxmlLoaderTable.getController();

      lessonFormController.init(this);
      lessonTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setLessonForm(Lesson lesson) {
    lessonFormController.setLessonForm(lesson);
  }

  public void refreshTableView() {
    lessonTableController.fillInTableView();
  }

  public void refreshChildTableView() {
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
  }

  public void refreshChildComboBoxes() {
    injectService.getWordController().refreshLessonComboBox();
    injectService.getNoteController().refreshLessonComboBox();
    injectService.getExerciseController().refreshLessonComboBox();
  }

  public void refreshCourseComboBox() {
    lessonFormController.initializeCourseComboBox();
  }

  public CourseService getCourseService() {
    return courseService;
  }

  public LessonService getLessonService() {
    return lessonService;
  }

}
