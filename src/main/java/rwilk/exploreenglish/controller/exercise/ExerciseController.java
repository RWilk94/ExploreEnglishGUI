package rwilk.exploreenglish.controller.exercise;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.service.ExerciseService;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ExerciseController implements Initializable {

  private final InjectService injectService;
  private final LessonService lessonService;
  private final ExerciseService exerciseService;
  private ExerciseFormController exerciseFormController;
  private ExerciseTableController exerciseTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public ExerciseController(InjectService injectService, LessonService lessonService, ExerciseService exerciseService) {
    this.injectService = injectService;
    this.lessonService = lessonService;
    this.exerciseService = exerciseService;
    injectService.setExerciseController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/exercise/exercise_form.fxml"));
      VBox form = fxmlLoaderForm.load();
      exerciseFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/exercise/exercise_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      exerciseTableController = fxmlLoaderTable.getController();

      exerciseFormController.init(this);
      exerciseTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setExerciseForm(Exercise exercise) {
    exerciseFormController.setExerciseForm(exercise);
  }

  public void refreshTableView() {
    exerciseTableController.fillInTableView();
  }

  public void refreshChildView() {
    injectService.getExerciseItemController().refreshTableView();
    injectService.getExerciseItemController().refreshExerciseComboBox();
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshChildComboBoxes() {
    injectService.getExerciseItemController().refreshExerciseComboBox();
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshLessonComboBox() {
    exerciseFormController.initializeLessonComboBox();
  }

  public void setLessonComboBox(Lesson lesson) {
    exerciseFormController.comboBoxLesson.getSelectionModel().select(lesson);
  }

  public LessonService getLessonService() {
    return lessonService;
  }

  public ExerciseService getExerciseService() {
    return exerciseService;
  }

}
