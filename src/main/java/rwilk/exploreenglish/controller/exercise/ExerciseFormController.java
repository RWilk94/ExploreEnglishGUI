package rwilk.exploreenglish.controller.exercise;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.Lesson;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ExerciseFormController implements Initializable {

  private ExerciseController exerciseController;
  public TextField textFieldId;
  public ComboBox<Lesson> comboBoxLesson;
  public ComboBox<String> comboBoxType;
  public TextField textFieldName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(ExerciseController exerciseController) {
    this.exerciseController = exerciseController;
    initializeLessonComboBox();
    initializeLessonType();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      exerciseController.getExerciseService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(exercise -> exerciseController.getExerciseService().delete(exercise));
      buttonClearOnAction(actionEvent);
      exerciseController.refreshTableView();
      exerciseController.refreshChildView();
    }
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    textFieldName.clear();
    comboBoxLesson.getSelectionModel().select(null);
    comboBoxType.getSelectionModel().select(null);
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText()) && StringUtils.isNoneEmpty(textFieldName.getText())
        && comboBoxLesson.getSelectionModel().getSelectedItem() != null
        && comboBoxType.getSelectionModel().getSelectedItem() != null) {
      exerciseController.getExerciseService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(exercise -> {
            exercise.setName(textFieldName.getText());
            exercise.setLesson(comboBoxLesson.getSelectionModel().getSelectedItem());
            exercise.setType(comboBoxType.getSelectionModel().getSelectedItem());
            exercise = exerciseController.getExerciseService().save(exercise);
            setExerciseForm(exercise);
            exerciseController.refreshTableView();
            exerciseController.refreshChildComboBoxes();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldName.getText())
        && comboBoxLesson.getSelectionModel().getSelectedItem() != null
        && comboBoxType.getSelectionModel().getSelectedItem() != null) {

      Exercise exercise = Exercise.builder()
          .name(textFieldName.getText())
          .type(comboBoxType.getSelectionModel().getSelectedItem())
          .position(exerciseController.getExerciseService().getCountByLesson(comboBoxLesson.getSelectionModel().getSelectedItem()))
          .lesson(comboBoxLesson.getSelectionModel().getSelectedItem())
          .build();
      exercise = exerciseController.getExerciseService().save(exercise);
      setExerciseForm(exercise);
      exerciseController.refreshTableView();
      exerciseController.refreshChildComboBoxes();
    }
  }

  public void setExerciseForm(Exercise exercise) {
    textFieldId.setText(exercise.getId().toString());
    textFieldName.setText(exercise.getName());
    comboBoxLesson.getSelectionModel().select(exercise.getLesson());
    comboBoxType.getSelectionModel().select(exercise.getType());
  }

  public void initializeLessonComboBox() {
    List<Lesson> lessons = exerciseController.getLessonService().getAll();
    comboBoxLesson.setItems(FXCollections.observableArrayList(lessons));
  }

  private void initializeLessonType() {
    List<String> types = Arrays.asList("CHOICE", "DIALOGUE");
    comboBoxType.setItems(FXCollections.observableArrayList(types));
  }
}
