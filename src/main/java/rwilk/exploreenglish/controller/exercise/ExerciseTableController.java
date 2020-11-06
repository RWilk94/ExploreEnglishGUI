package rwilk.exploreenglish.controller.exercise;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import rwilk.exploreenglish.model.entity.Exercise;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExerciseTableController implements Initializable {


  private ExerciseController exerciseController;
  private List<Exercise> exercises;

  public TextField textFieldSearch;
  public TableView<Exercise> tableExercise;
  public TableColumn<Exercise, Long> columnId;
  public TableColumn<Exercise, String> columnType;
  public TableColumn<Exercise, String> columnName;
  public TableColumn<Exercise, String> columnLesson;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTableByLesson(newValue));
    textFieldSearch.setOnMouseClicked(view -> filterTableByLesson(textFieldSearch.getText()));

  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableExercise.widthProperty().multiply(0.1));
    columnType.prefWidthProperty().bind(tableExercise.widthProperty().multiply(0.3));
    columnName.prefWidthProperty().bind(tableExercise.widthProperty().multiply(0.3));
    columnLesson.prefWidthProperty().bind(tableExercise.widthProperty().multiply(0.3));
  }

  public void init(ExerciseController exerciseController) {
    this.exerciseController = exerciseController;
    fillInTableView();
  }

  public void fillInTableView() {
    exercises = exerciseController.getExerciseService().getAll();
    tableExercise.setItems(FXCollections.observableArrayList(exercises));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (tableExercise.getSelectionModel().getSelectedItem() != null) {
      Exercise selectedItem = tableExercise.getSelectionModel().getSelectedItem();
      exerciseController.setExerciseForm(selectedItem);
    }
  }

  private void filterTableByLesson(String value) {
    List<Exercise> filtered = exercises.stream()
        .filter(exercise ->
            exercise.getLesson().getEnglishName().toLowerCase().contains(value.toLowerCase())
                || exercise.getLesson().getPolishName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableExercise.setItems(FXCollections.observableArrayList(filtered));
  }

}
