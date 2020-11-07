package rwilk.exploreenglish.controller.exerciseitem;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.ExerciseItem;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class ExerciseItemTableController implements Initializable {

  private ExerciseItemController exerciseItemController;
  private List<ExerciseItem> exerciseItems;

  public TextField textFieldSearch;
  public TableView<ExerciseItem> tableExerciseItem;
  public TableColumn<ExerciseItem, Long> columnId;
  public TableColumn<ExerciseItem, String> columnQuestion;
  public TableColumn<ExerciseItem, String> columnCorrectAnswer;
  public TableColumn<ExerciseItem, String> columnFinalAnswer;
  public TableColumn<ExerciseItem, String> columnFirstPossibleAnswer;
  public TableColumn<ExerciseItem, String> columnSecondPossibleAnswer;
  public TableColumn<ExerciseItem, String> columnThirdPossibleAnswer;
  public TableColumn<ExerciseItem, String> columnForthPossibleAnswer;
  public TableColumn<ExerciseItem, String> columnDialogueEnglish;
  public TableColumn<ExerciseItem, String> columnDialoguePolish;
  public TableColumn<ExerciseItem, String> columnDescription;
  public TableColumn<ExerciseItem, String> columnExercise;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTableByExercise(newValue));
    textFieldSearch.setOnMouseClicked(view -> filterTableByExercise(textFieldSearch.getText()));
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.1));
    columnQuestion.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.25));
    columnCorrectAnswer.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.2));
    columnFinalAnswer.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.25));
    columnFirstPossibleAnswer.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.2));
    columnSecondPossibleAnswer.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.2));
    columnThirdPossibleAnswer.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.2));
    columnForthPossibleAnswer.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.2));
    columnDialogueEnglish.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.3));
    columnDialoguePolish.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.3));
    columnDescription.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.3));
    columnExercise.prefWidthProperty().bind(tableExerciseItem.widthProperty().multiply(0.3));
  }

  public void init(ExerciseItemController exerciseItemController) {
    this.exerciseItemController = exerciseItemController;
    fillInTableView();
  }

  public void fillInTableView() {
    exerciseItems = exerciseItemController.getExerciseItemService().getAll();
    tableExerciseItem.setItems(FXCollections.observableArrayList(exerciseItems));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (tableExerciseItem.getSelectionModel().getSelectedItem() != null) {
      ExerciseItem exerciseItem = tableExerciseItem.getSelectionModel().getSelectedItem();
      exerciseItemController.setExerciseForm(exerciseItem);
    }
  }

  private void filterTableByExercise(String value) {
    List<ExerciseItem> filtered = exerciseItems.stream()
        .filter(exerciseItem ->
            exerciseItem.getExercise().getName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableExerciseItem.setItems(FXCollections.observableArrayList(filtered));
  }
}
