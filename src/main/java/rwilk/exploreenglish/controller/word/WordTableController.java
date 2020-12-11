package rwilk.exploreenglish.controller.word;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Word;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class WordTableController implements Initializable {

  private WordController wordController;
  private List<Word> words;

  public TableView<Word> tableWords;
  public TableColumn<Word, Long> columnId;
  public TableColumn<Word, String> columnEnglishName;
  public TableColumn<Word, String> columnAmericanName;
  public TableColumn<Word, String> columnOtherName;
  public TableColumn<Word, String> columnPolishName;
  public TableColumn<Word, String> columnLevel;
  public TableColumn<Word, String> columnLesson;
  public TableColumn<Word, String> columnPartOfSpeech;
  public TableColumn<Word, String> columnComparative;
  public TableColumn<Word, String> columnSuperlative;
  public TableColumn<Word, String> columnPastTense;
  public TableColumn<Word, String> columnPastParticiple;
  public TableColumn<Word, String> columnPlural;
  public TableColumn<Word, String> columnOpposite;
  public TableColumn<Word, String> columnSynonym;
  public TextField textFieldFilterByLesson;
  public TextField textFieldFilterByCourse;
  public TextField textFieldFilterByName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldFilterByName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldFilterByName.setOnMouseClicked(view -> filterTableByName(textFieldFilterByName.getText()));

    textFieldFilterByLesson.textProperty().addListener((observable, oldValue, newValue) -> filterTableByLesson(newValue));
    textFieldFilterByLesson.setOnMouseClicked(view -> filterTableByLesson(textFieldFilterByLesson.getText()));

    textFieldFilterByCourse.textProperty().addListener((observable, oldValue, newValue) -> filterTableByCourse(newValue));
    textFieldFilterByCourse.setOnMouseClicked(view -> filterTableByCourse(textFieldFilterByCourse.getText()));
  }

  public void init(WordController wordController) {
    this.wordController = wordController;
    fillInTableView();
  }

  public void fillInTableView() {
    words = wordController.getWordService().getAll();
    tableWords.setItems(FXCollections.observableArrayList(words));
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnEnglishName, columnPolishName), 0.16);
    initializeColumns(Arrays.asList(columnAmericanName, columnOtherName, columnPartOfSpeech), 0.14);
    initializeColumns(Arrays.asList(
        columnLesson, columnComparative,
        columnSuperlative, columnPastTense, columnPastParticiple, columnPlural, columnOpposite, columnSynonym), 0.2);
    initializeColumns(Arrays.asList(columnId, columnLevel), 0.05);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableWords.widthProperty().multiply(other)));
  }

  public void tableViewWordsOnMouseClicked(MouseEvent mouseEvent) {
    if (tableWords.getSelectionModel().getSelectedItem() != null) {
      Word selectedItem = tableWords.getSelectionModel().getSelectedItem();
      wordController.setWordForm(selectedItem);
    }
  }

  private void filterTableByName(String value) {
    List<Word> filteredWords = words.stream()
        .filter(word ->
                word.getPolishName().toLowerCase().contains(value.toLowerCase())
                || word.getEnglishNames().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableWords.setItems(FXCollections.observableArrayList(filteredWords));
  }

  private void filterTableByLesson(String value) {
    List<Word> filteredWords = words.stream()
        .filter(word ->
            word.getLesson().getEnglishName().toLowerCase().contains(value.toLowerCase())
                || word.getLesson().getPolishName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableWords.setItems(FXCollections.observableArrayList(filteredWords));
  }

  private void filterTableByCourse(String value) {
    List<Word> filteredWords = words.stream()
        .filter(word ->
            word.getLesson().getCourse().getEnglishName().toLowerCase().contains(value.toLowerCase())
                || word.getLesson().getCourse().getPolishName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableWords.setItems(FXCollections.observableArrayList(filteredWords));
  }
}
