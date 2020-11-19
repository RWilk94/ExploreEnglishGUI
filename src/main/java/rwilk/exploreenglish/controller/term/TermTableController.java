package rwilk.exploreenglish.controller.term;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class TermTableController implements Initializable {

  private final TermService termService;
  private List<Term> terms = new ArrayList<>();
  public TextField textFieldFilterByName;
  public TextField textFieldFilterByCategory;
  public TableView<Term> tableTerms;
  public TableColumn<Term, Long> columnId;
  public TableColumn<Term, String> columnEnglishName;
  public TableColumn<Term, String> columnAmericanName;
  public TableColumn<Term, String> columnOtherName;
  public TableColumn<Term, String> columnPolishName;
  public TableColumn<Term, String> columnCategory;
  public TableColumn<Term, String> columnComparative;
  public TableColumn<Term, String> columnSuperlative;
  public TableColumn<Term, String> columnPastTense;
  public TableColumn<Term, String> columnPastParticiple;
  public TableColumn<Term, String> columnPlural;
  public TableColumn<Term, String> columnSynonym;
  public TableColumn<Term, String> columnSource;
  public TableColumn<Term, String> columnEnglishSentence;
  public TableColumn<Term, String> columnPolishSentence;

  public TermTableController(TermService termService) {
    this.termService = termService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();

    textFieldFilterByName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldFilterByName.setOnMouseClicked(view -> filterTableByName(textFieldFilterByName.getText()));

    textFieldFilterByCategory.textProperty().addListener((observable, oldValue, newValue) -> filterTableByCategory(newValue));
    textFieldFilterByCategory.setOnMouseClicked(view -> filterTableByCategory(textFieldFilterByCategory.getText()));
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnEnglishName, columnPolishName), 0.16);
    initializeColumns(Arrays.asList(columnAmericanName, columnOtherName, columnEnglishSentence, columnPolishSentence), 0.14);
    initializeColumns(Arrays.asList(
        columnCategory, columnComparative,
        columnSuperlative, columnPastTense, columnPastParticiple, columnPlural, columnSynonym), 0.1);
    initializeColumns(Arrays.asList(columnId, columnSource), 0.05);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableTerms.widthProperty().multiply(other)));
  }

  private void fillInTableView() {
    terms = termService.getAll();
    tableTerms.setItems(FXCollections.observableArrayList(terms));
  }

  public void tableVieTermsOnMouseClicked(MouseEvent mouseEvent) {
  }

  private void filterTableByName(String value) {
    List<Term> filteredTerms = terms.stream()
        .filter(term ->
            (term.getEnglishName() != null && term.getEnglishName().toLowerCase().startsWith(value.toLowerCase()))
                || (term.getPolishName() != null && term.getPolishName().toLowerCase().startsWith(value.toLowerCase()))
                || (term.getAmericanName() != null && term.getAmericanName().toLowerCase().startsWith(value.toLowerCase()))
                || (term.getOtherName() != null && term.getOtherName().toLowerCase().startsWith(value.toLowerCase())))
        .collect(Collectors.toList());
    tableTerms.setItems(FXCollections.observableArrayList(filteredTerms));
  }

  private void filterTableByCategory(String value) {
    List<Term> filteredTerms = terms.stream()
        .filter(term ->
            term.getCategory().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableTerms.setItems(FXCollections.observableArrayList(filteredTerms));
  }
}
