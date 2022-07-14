package rwilk.exploreenglish.controller.word;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSound;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class WordTableController implements Initializable {

  private WordController wordController;
  private List<Word> words;

  public TableView<Word> tableWords;
  public TableColumn<Word, Long> columnId;
  public TableColumn<Word, String> columnEnglishName;
  public TableColumn<Word, String> columnPolishName;
  public TableColumn<Word, String> columnLevel;
  public TableColumn<Word, String> columnPartOfSpeech;
  public TableColumn<Word, String> columnAmeSound;
  public TableColumn<Word, String> columnBreSound;
  public TableColumn<Word, String> columnComparative;
  public TableColumn<Word, String> columnSuperlative;
  public TableColumn<Word, String> columnPastTense;
  public TableColumn<Word, String> columnPastParticiple;
  public TableColumn<Word, String> columnPlural;
  public TableColumn<Word, String> columnOpposite;
  public TableColumn<Word, String> columnSynonym;
  public TableColumn<Word, String> columnSentence;
  public TextField textFieldFilterByLesson;
  public TextField textFieldFilterByCourse;
  public TextField textFieldFilterByEnName;
  public TextField textFieldFilterByPlName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldFilterByEnName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByEnName(newValue));
    textFieldFilterByEnName.setOnMouseClicked(view -> filterTableByEnName(textFieldFilterByEnName.getText()));

    textFieldFilterByPlName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByPlName(newValue));
    textFieldFilterByPlName.setOnMouseClicked(view -> filterTableByPlName(textFieldFilterByPlName.getText()));

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
    new Thread(() -> {
      words = wordController.getWordService().getAll();

      Platform.runLater(() -> tableWords.setItems(FXCollections.observableArrayList(words)));
    }).start();
  }

  private void initializeTableView() {
    initializeColumns(List.of(columnEnglishName, columnPolishName), 0.16);
    initializeColumns(List.of(columnPartOfSpeech, columnAmeSound, columnBreSound), 0.10);
    initializeColumns(List.of(columnComparative, columnSuperlative, columnPastTense, columnPastParticiple,
        columnPlural, columnOpposite, columnSynonym, columnSentence), 0.2);
    initializeColumns(List.of(columnId, columnLevel), 0.05);

    tableWords.setRowFactory(row -> new TableRow<Word>() {
      @Override
      protected void updateItem(Word item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && (item.getLessonWords() == null || item.getLessonWords().isEmpty())) {
          this.setStyle("-fx-background-color: #ED073D");
        } else {
          setStyle("");
        }
      }
    });

    columnEnglishName.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.WORD, WordSound::getEnglishName));
    columnAmeSound.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.WORD, WordSound::getAmericanSound));
    columnBreSound.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.WORD, WordSound::getBritishSound));
    columnComparative.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.COMPARATIVE, WordSound::getEnglishName));
    columnSuperlative.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.SUPERLATIVE, WordSound::getEnglishName));
    columnPastTense.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.PAST_TENSE, WordSound::getEnglishName));
    columnPastParticiple.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.PAST_PARTICIPLE, WordSound::getEnglishName));
    columnPlural.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.PLURAL, WordSound::getEnglishName));
    columnOpposite.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.OPPOSITE, WordSound::getEnglishName));
    columnSynonym.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.SYNONYM, WordSound::getEnglishName));
    columnSentence.setCellValueFactory(param -> bindCellValue(param.getValue().getEnglishNames(), WordTypeEnum.SENTENCE, WordSound::getEnglishName));
  }

  private ObjectBinding<String> bindCellValue(final List<WordSound> wordSounds, final WordTypeEnum wordType,
                                              final Function<WordSound, String> mapFunction) {
    return Bindings.createObjectBinding(() -> StringUtils.defaultString(
        ListUtils.emptyIfNull(wordSounds)
            .stream()
            .filter(wordSound -> wordType.toString().equals(wordSound.getType()))
            .map(mapFunction)
            .collect(Collectors.joining("; "))));
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

  private void filterTableByEnName(String value) {
    if (words == null || words.isEmpty()) {
      return;
    }
    new Thread(() -> {
      List<Word> filteredWords = words.stream()
                                      .filter(word -> word.englishNamesAsString().toLowerCase().contains(
                                              value.toLowerCase()))
                                      .collect(Collectors.toList());
      Platform.runLater(() -> tableWords.setItems(FXCollections.observableArrayList(filteredWords)));
    }).start();
  }

  private void filterTableByPlName(String value) {
    if (words == null || words.isEmpty()) {
      return;
    }
    new Thread(() -> {
      List<Word> filteredWords = words.stream()
                                      .filter(word -> word.getPolishName().toLowerCase().contains(value.toLowerCase()))
                                      .collect(Collectors.toList());
      Platform.runLater(() -> tableWords.setItems(FXCollections.observableArrayList(filteredWords)));
    }).start();
  }

  private void filterTableByLesson(String value) {
    if (StringUtils.isNoneEmpty(value) && words != null) {
      new Thread(() -> {
        List<Word> filteredWords = words.stream()
                                        .filter(word -> word.getLessonWords() != null)
                                        .filter(word -> word.getLessonWords()
                                                            .stream()
                                                            .anyMatch(
                                                                    lessonWord -> lessonWord.getLesson().getEnglishName().toLowerCase().contains(
                                                                            value.toLowerCase()))
                                                || word.getLessonWords()
                                                       .stream()
                                                       .anyMatch(
                                                               lessonWord -> lessonWord.getLesson().getPolishName().toLowerCase().contains(
                                                                       value.toLowerCase())))
                                        .collect(Collectors.toList());
        Platform.runLater(() -> {
          if (value.equals(textFieldFilterByLesson.getText())) {
            tableWords.setItems(FXCollections.observableArrayList(filteredWords));
          }
        });
      }).start();
    }
  }

  private void filterTableByCourse(String value) {
    if (StringUtils.isNoneEmpty(value) && words != null) {
      new Thread(() -> {
        List<Word> filteredWords = words.stream()
                                        .filter(word -> word.getLessonWords() != null)
                                        .filter(word -> word.getLessonWords()
                                                            .stream()
                                                            .anyMatch(
                                                                    lessonWord -> lessonWord.getLesson().getCourse().getEnglishName().toLowerCase().contains(
                                                                            value.toLowerCase()))
                                                || word.getLessonWords()
                                                       .stream()
                                                       .anyMatch(
                                                               lessonWord -> lessonWord.getLesson().getCourse().getPolishName().toLowerCase().contains(
                                                                       value.toLowerCase())))
                                        .collect(Collectors.toList());
        Platform.runLater(() -> tableWords.setItems(FXCollections.observableArrayList(filteredWords)));
      }).start();
    }
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    tableWords.setItems(FXCollections.observableArrayList(words));
  }
}
