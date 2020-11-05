package rwilk.exploreenglish.controller.lesson;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.LessonService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class LessonController implements Initializable {

  private final CourseService courseService;
  private final LessonService lessonService;
  private LessonFormController lessonFormController;
  private LessonTableController lessonTableController;

  public AnchorPane anchorPaneLessonForm;
  public AnchorPane anchorPaneLessonTable;

  public LessonController(CourseService courseService, LessonService lessonService) {
    this.courseService = courseService;
    this.lessonService = lessonService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderLessonForm = new FXMLLoader();
      fxmlLoaderLessonForm.setLocation(getClass().getResource("/scene/lesson/lesson_form.fxml"));
      VBox form = fxmlLoaderLessonForm.load();
      lessonFormController = fxmlLoaderLessonForm.getController();

      FXMLLoader fxmlLoaderLessonTable = new FXMLLoader();
      fxmlLoaderLessonTable.setLocation(getClass().getResource("/scene/lesson/lesson_table.fxml"));
      VBox table = fxmlLoaderLessonTable.load();
      lessonTableController = fxmlLoaderLessonTable.getController();

      lessonFormController.init(this);
      lessonTableController.init(this);

      anchorPaneLessonForm.getChildren().add(form);
      anchorPaneLessonTable.getChildren().add(table);
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

  public CourseService getCourseService() {
    return courseService;
  }

  public LessonService getLessonService() {
    return lessonService;
  }

}
