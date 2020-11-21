package rwilk.exploreenglish.controller.term;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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
public class TermDuplicatedTableController implements Initializable {

  private final InjectService injectService;
  private final TermService termService;
  private List<Term> terms = new ArrayList<>();
  public TextField textFieldFilterByName;
  public TextField textFieldFilterByPLName;
  public TableView<Term> tableDuplicatedTerms;
  public TableColumn<Term, Long> columnId;
  public TableColumn<Term, CheckBox> columnIsAdded;
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

  public TermDuplicatedTableController(InjectService injectService, TermService termService) {
    this.injectService = injectService;
    this.termService = termService;
    this.injectService.setTermDuplicatedTableController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();

    textFieldFilterByName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldFilterByName.setOnMouseClicked(view -> filterTableByName(textFieldFilterByName.getText()));

    textFieldFilterByPLName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByPLName(newValue));
    textFieldFilterByPLName.setOnMouseClicked(view -> filterTableByPLName(textFieldFilterByPLName.getText()));
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnEnglishName, columnPolishName), 0.16);
    initializeColumns(Arrays.asList(columnAmericanName, columnOtherName, columnEnglishSentence, columnPolishSentence), 0.14);
    initializeColumns(Arrays.asList(
        columnCategory, columnComparative,
        columnSuperlative, columnPastTense, columnPastParticiple, columnPlural, columnSynonym), 0.1);
    initializeColumns(Arrays.asList(columnId, columnIsAdded, columnSource), 0.05);

    columnIsAdded.setCellValueFactory(param -> {
      Term term = param.getValue();
      CheckBox checkBox = new CheckBox();
      checkBox.selectedProperty().setValue(term.getIsAdded());
      checkBox.selectedProperty().addListener((ov, oldVal, newVal) -> {
        term.setIsAdded(newVal);
        termService.save(term);
        injectService.getTermTableController().updateById(term.getId());
        // tableDuplicatedTerms.refresh();
      });
      return new SimpleObjectProperty<>(checkBox);
    });

    tableDuplicatedTerms.setRowFactory(row -> new TableRow<Term>() {
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
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableDuplicatedTerms.widthProperty().multiply(other)));
  }

  private void fillInTableView() {
    terms = termService.getAll();
    tableDuplicatedTerms.setItems(FXCollections.observableArrayList(terms));
  }

  private void filterTableByName(String value) {
    List<Term> filteredTerms = terms.stream()
        .filter(term ->
            (term.getEnglishName() != null && term.getEnglishName().toLowerCase().equals(value.toLowerCase()))
                || (term.getAmericanName() != null && term.getAmericanName().toLowerCase().equals(value.toLowerCase()))
                || (term.getOtherName() != null && term.getOtherName().toLowerCase().equals(value.toLowerCase())))
        .collect(Collectors.toList());
    tableDuplicatedTerms.setItems(FXCollections.observableArrayList(filteredTerms));
  }

  private void filterTableByPLName(String value) {
    List<Term> filteredTerms = terms.stream()
        .filter(term -> term.getPolishName() != null && term.getPolishName().toLowerCase().equals(value.toLowerCase()))
        .collect(Collectors.toList());
    tableDuplicatedTerms.setItems(FXCollections.observableArrayList(filteredTerms));
  }

  public void tableViewDuplicatedTermsOnMouseClicked(MouseEvent mouseEvent) {
    Term term = tableDuplicatedTerms.getSelectionModel().getSelectedItem();
  }

  private int findById(Long id) {
    List<Long> ids = terms.stream().map(Term::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }

}
