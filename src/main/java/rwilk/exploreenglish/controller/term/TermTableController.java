package rwilk.exploreenglish.controller.term;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.TermService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class TermTableController implements Initializable {

  private final InjectService injectService;
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

  public TermTableController(InjectService injectService, TermService termService) {
    this.injectService = injectService;
    this.termService = termService;
    this.injectService.setTermTableController(this);
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

    tableTerms.setRowFactory(row -> new TableRow<Term>() {
      @Override
      protected void updateItem(Term item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && item.getIsAdded() != null && item.getIsAdded()) {
          this.setStyle("-fx-background-color: #07cded");
        } else {
          setStyle("");
        }
      }
    });
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableTerms.widthProperty().multiply(other)));
  }

  private void fillInTableView() {
    terms = termService.getAll();
    tableTerms.setItems(FXCollections.observableArrayList(terms));
  }

  public void tableViewTermsOnMouseClicked(MouseEvent mouseEvent) {
    Term term = tableTerms.getSelectionModel().getSelectedItem();
    if (term != null) {
      injectService.getWordController().setWordForm(term);
      injectService.getMainController().tabPane.getSelectionModel().select(2);
      injectService.getTermDuplicatedTableController().textFieldFilterByPLName.setText(term.getPolishName());
      injectService.getTermDuplicatedTableController().textFieldFilterByName.setText(term.getEnglishName());
    }
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

  public void updateById(Long id) {
    Term term = terms.get(findById(id));
    term.setIsAdded(!term.getIsAdded());
    tableTerms.refresh();
  }

  private int findById(Long id) {
    List<Long> ids = terms.stream().map(Term::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }
}
