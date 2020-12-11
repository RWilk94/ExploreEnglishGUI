package rwilk.exploreenglish.controller.exerciseitem;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.model.entity.Word;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class ExerciseItemFormController implements Initializable {

  private ExerciseItemController exerciseItemController;

  public TextField textFieldId;
  public ComboBox<Exercise> comboBoxExercise;
  public TextField textFieldQuestion;
  public TextField textFieldCorrectAnswer;
  public TextField textFieldFinalAnswer;
  public TextField textFieldFirstPossibleAnswer;
  public TextField textFieldSecondPossibleAnswer;
  public TextField textFieldThirdPossibleAnswer;
  public TextField textFieldFourthPossibleAnswer;
  public TextField textFieldDialogueEnglish;
  public TextField textFieldDialoguePolish;
  public TextField textFieldDescription;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(ExerciseItemController exerciseItemController) {
    this.exerciseItemController = exerciseItemController;
    initializeExerciseComboBox();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      exerciseItemController.getExerciseItemService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(item -> exerciseItemController.getExerciseItemService().delete(item));
      buttonClearOnAction(actionEvent);
      exerciseItemController.refreshTableView();
    }
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    comboBoxExercise.getSelectionModel().select(null);
    textFieldQuestion.clear();
    textFieldCorrectAnswer.clear();
    textFieldFinalAnswer.clear();
    textFieldFirstPossibleAnswer.clear();
    textFieldSecondPossibleAnswer.clear();
    textFieldThirdPossibleAnswer.clear();
    textFieldFourthPossibleAnswer.clear();
    textFieldDialogueEnglish.clear();
    textFieldDialoguePolish.clear();
    textFieldDescription.clear();
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNoneEmpty(textFieldId.getText())
        && comboBoxExercise.getSelectionModel().getSelectedItem() != null) {
      Exercise exercise = comboBoxExercise.getSelectionModel().getSelectedItem();
      exerciseItemController.getExerciseItemService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(item -> {
            if (exercise.getType().equals("DIALOGUE")
                && StringUtils.isNoneEmpty(textFieldDialogueEnglish.getText())
                && StringUtils.isNoneEmpty(textFieldDialoguePolish.getText())) {
              item.setDialogueEnglish(textFieldDialogueEnglish.getText());
              item.setDialoguePolish(textFieldDialoguePolish.getText());
            } else if (exercise.getType().equals("CHOICE")
                && StringUtils.isNoneEmpty(textFieldQuestion.getText())
                && StringUtils.isNoneEmpty(textFieldCorrectAnswer.getText())
                && StringUtils.isNoneEmpty(textFieldFinalAnswer.getText())
                && StringUtils.isNoneEmpty(textFieldFirstPossibleAnswer.getText())
                && StringUtils.isNoneEmpty(textFieldSecondPossibleAnswer.getText())) {
              item.setQuestion(textFieldQuestion.getText());
              item.setCorrectAnswer(textFieldCorrectAnswer.getText());
              item.setFinalAnswer(textFieldFinalAnswer.getText());
              item.setFirstPossibleAnswer(textFieldFirstPossibleAnswer.getText());
              item.setSecondPossibleAnswer(textFieldSecondPossibleAnswer.getText());
              item.setThirdPossibleAnswer(textFieldThirdPossibleAnswer.getText());
              item.setForthPossibleAnswer(textFieldFourthPossibleAnswer.getText());
              item.setDescription(textFieldDescription.getText());
            }
            item = exerciseItemController.getExerciseItemService().save(item);
            setExerciseItemForm(item);
            exerciseItemController.refreshTableView();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    Exercise exercise = comboBoxExercise.getSelectionModel().getSelectedItem();
    if (exercise != null) {
      ExerciseItem exerciseItem = null;
      if (exercise.getType().equals("DIALOGUE")
          && StringUtils.isNoneEmpty(textFieldDialogueEnglish.getText())
          && StringUtils.isNoneEmpty(textFieldDialoguePolish.getText())) {
        exerciseItem = ExerciseItem.builder()
            .dialogueEnglish(textFieldDialogueEnglish.getText())
            .dialoguePolish(textFieldDialoguePolish.getText())
            .position(exerciseItemController.getExerciseItemService().getCountByExercise(exercise))
            .exercise(exercise)
            .build();
      } else if (exercise.getType().equals("CHOICE")
          && StringUtils.isNoneEmpty(textFieldQuestion.getText())
          && StringUtils.isNoneEmpty(textFieldCorrectAnswer.getText())
          && StringUtils.isNoneEmpty(textFieldFinalAnswer.getText())
          && StringUtils.isNoneEmpty(textFieldFirstPossibleAnswer.getText())
          && StringUtils.isNoneEmpty(textFieldSecondPossibleAnswer.getText())) {
        exerciseItem = ExerciseItem.builder()
            .question(textFieldQuestion.getText())
            .correctAnswer(textFieldCorrectAnswer.getText())
            .finalAnswer(textFieldFinalAnswer.getText())
            .firstPossibleAnswer(textFieldFirstPossibleAnswer.getText())
            .secondPossibleAnswer(textFieldSecondPossibleAnswer.getText())
            .thirdPossibleAnswer(textFieldThirdPossibleAnswer.getText())
            .forthPossibleAnswer(textFieldFourthPossibleAnswer.getText())
            .description(textFieldDescription.getText())
            .position(exerciseItemController.getExerciseItemService().getCountByExercise(exercise))
            .exercise(exercise)
            .build();
      }
      exerciseItem = exerciseItemController.getExerciseItemService().save(exerciseItem);
      setExerciseItemForm(exerciseItem);
      exerciseItemController.refreshTableView();
    }
  }

  public void setExerciseItemForm(ExerciseItem exerciseItem) {
    textFieldId.setText(exerciseItem.getId().toString());
    comboBoxExercise.getSelectionModel().select(exerciseItem.getExercise());
    textFieldQuestion.setText(exerciseItem.getQuestion());
    textFieldCorrectAnswer.setText(exerciseItem.getCorrectAnswer());
    textFieldFinalAnswer.setText(exerciseItem.getFinalAnswer());
    textFieldFirstPossibleAnswer.setText(exerciseItem.getFirstPossibleAnswer());
    textFieldSecondPossibleAnswer.setText(exerciseItem.getSecondPossibleAnswer());
    textFieldThirdPossibleAnswer.setText(exerciseItem.getThirdPossibleAnswer());
    textFieldFourthPossibleAnswer.setText(exerciseItem.getForthPossibleAnswer());
    textFieldDialogueEnglish.setText(exerciseItem.getDialogueEnglish());
    textFieldDialoguePolish.setText(exerciseItem.getDialoguePolish());
    textFieldDescription.setText(exerciseItem.getDescription());
  }

  public void initializeExerciseComboBox() {
    List<Exercise> exercises = exerciseItemController.getExerciseService().getAll();
    comboBoxExercise.setItems(FXCollections.observableArrayList(exercises));
  }

  public void buttonLoadWordsOnAction(ActionEvent actionEvent) {
    Exercise exercise = comboBoxExercise.getSelectionModel().getSelectedItem();
    if (exercise != null) {
      List<Word> words = exerciseItemController.getInjectService().getWordController().getWordService()
          .getAllByLesson(exercise.getLesson()).stream()
          .sorted(Comparator.comparing(Word::getPosition))
          .collect(Collectors.toList());
      // FIXME otherName split(";")[0]
      if (words.size() >= 4) {
        textFieldFirstPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
        textFieldSecondPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
        textFieldThirdPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
        textFieldFourthPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
      } else if (words.size() >= 3) {
        textFieldFirstPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
        textFieldSecondPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
        textFieldThirdPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
      } else if (words.size() >= 2) {
        textFieldFirstPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
        textFieldSecondPossibleAnswer.setText(words.remove(getIndex(words)).getEnglishNames());
      }
    }
  }

  private int getIndex(List<Word> words) {
    return new Random().nextInt(((words.size() - 1) + 1));
  }

  private Word getWord(List<Word> words) {
    int index = new Random().nextInt(((words.size() - 1) + 1));
    return words.get(index);
  }

}
