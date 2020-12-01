package rwilk.exploreenglish.controller.exerciseitem;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.service.ExerciseItemService;
import rwilk.exploreenglish.service.ExerciseService;
import rwilk.exploreenglish.service.InjectService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ExerciseItemController implements Initializable {

  private final InjectService injectService;
  private final ExerciseService exerciseService;
  private final ExerciseItemService exerciseItemService;
  private ExerciseItemFormController exerciseItemFormController;
  private ExerciseItemTableController exerciseItemTableController;

  public AnchorPane anchorPaneForm;
  public AnchorPane anchorPaneTable;

  public ExerciseItemController(InjectService injectService, ExerciseService exerciseService, ExerciseItemService exerciseItemService) {
    this.injectService = injectService;
    this.exerciseService = exerciseService;
    this.exerciseItemService = exerciseItemService;
    injectService.setExerciseItemController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/exerciseitem/exercise_item_form.fxml"));
      VBox form = fxmlLoaderForm.load();
      exerciseItemFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/exerciseitem/exercise_item_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      exerciseItemTableController = fxmlLoaderTable.getController();

      exerciseItemFormController.init(this);
      exerciseItemTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setExerciseForm(ExerciseItem exerciseItem) {
    exerciseItemFormController.setExerciseItemForm(exerciseItem);
  }

  public void refreshTableView() {
    exerciseItemTableController.fillInTableView();
    injectService.getViewController().refreshListViewLessonItemChildren();
  }

  public void refreshExerciseComboBox() {
    exerciseItemFormController.initializeExerciseComboBox();
  }

  public void setExerciseComboBox(Exercise exercise) {
    exerciseItemFormController.comboBoxExercise.getSelectionModel().select(exercise);
  }

  public ExerciseService getExerciseService() {
    return exerciseService;
  }

  public ExerciseItemService getExerciseItemService() {
    return exerciseItemService;
  }

  public InjectService getInjectService() {
    return injectService;
  }
}
