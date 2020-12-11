package rwilk.exploreenglish.controller.sentence;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Sentence;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class SentenceTableController implements Initializable {

  private SentenceController sentenceController;
  private List<Sentence> sentences;

  public TextField textFieldFilterByWord;
  public TableView<Sentence> tableSentence;
  public TableColumn<Sentence, Long> columnId;
  public TableColumn<Sentence, String> columnEnglishName;
  public TableColumn<Sentence, String> columnPolishName;
  public TableColumn<Sentence, String> columnWord;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldFilterByWord.textProperty().addListener((observable, oldValue, newValue) -> filterTableByWord(newValue));
    textFieldFilterByWord.setOnMouseClicked(view -> filterTableByWord(textFieldFilterByWord.getText()));
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableSentence.widthProperty().multiply(0.1));
    columnEnglishName.prefWidthProperty().bind(tableSentence.widthProperty().multiply(0.3));
    columnPolishName.prefWidthProperty().bind(tableSentence.widthProperty().multiply(0.3));
    columnWord.prefWidthProperty().bind(tableSentence.widthProperty().multiply(0.3));
  }

  public void init(SentenceController sentenceController) {
    this.sentenceController = sentenceController;
    fillInTableView();
  }

  public void fillInTableView() {
    sentences = sentenceController.getSentenceService().getAll();
    tableSentence.setItems(FXCollections.observableArrayList(sentences));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (tableSentence.getSelectionModel().getSelectedItem() != null) {
      Sentence selectedSentence = tableSentence.getSelectionModel().getSelectedItem();
      sentenceController.setSentenceForm(selectedSentence);
    }
  }

  private void filterTableByWord(String value) {
    List<Sentence> filtered = sentences.stream()
        .filter(sentence ->
                sentence.getWord().getPolishName().toLowerCase().contains(value.toLowerCase())
                || sentence.getWord().getEnglishNames().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableSentence.setItems(FXCollections.observableArrayList(filtered));
  }
}
