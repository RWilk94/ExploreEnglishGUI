package rwilk.exploreenglish.controller.term;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.scrapper.bab.BabScrapper;
import rwilk.exploreenglish.scrapper.cambridge.CambridgeDictionaryScrapper;
import rwilk.exploreenglish.scrapper.diki.DikiScrapper;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.TermService;
import rwilk.exploreenglish.utils.WordUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class TermTableController implements Initializable, CommandLineRunner {

  private final InjectService injectService;
  private final TermService termService;
  private final DikiScrapper dikiScrapper;
  private final BabScrapper babScrapper;
  private final CambridgeDictionaryScrapper cambridgeDictionaryScrapper;
  private List<Term> terms = new ArrayList<>();
  public TextField textFieldFilterByName;
  public TextField textFieldFilterByCategory;
  public TableView<Term> tableTerms;
  public TableColumn<Term, Long> columnId;
  public TableColumn<Term, CheckBox> columnIsIgnored;
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

  public TermTableController(InjectService injectService, TermService termService,
                             DikiScrapper dikiScrapper, BabScrapper babScrapper,
                             CambridgeDictionaryScrapper cambridgeDictionaryScrapper) {
    this.injectService = injectService;
    this.termService = termService;
    this.dikiScrapper = dikiScrapper;
    this.babScrapper = babScrapper;
    this.cambridgeDictionaryScrapper = cambridgeDictionaryScrapper;
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
    initializeColumns(Arrays.asList(columnId, columnIsIgnored, columnSource), 0.05);

    tableTerms.setRowFactory(row -> new TableRow<Term>() {
      @Override
      protected void updateItem(Term item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && item.getIsAdded() != null && item.getIsAdded()) {
          this.setStyle("-fx-background-color: #07cded");
        } else if (item != null && item.getIsIgnored() != null && item.getIsIgnored()) {
          this.setStyle("-fx-background-color: #ED073D");
        } else {
          setStyle("");
        }
      }
    });

    columnIsIgnored.setCellValueFactory(param -> {
      Term term = param.getValue();
      CheckBox checkBox = new CheckBox();
      checkBox.selectedProperty().setValue(term.getIsIgnored());
      checkBox.selectedProperty().addListener((ov, oldVal, newVal) -> {
        term.setIsIgnored(newVal);
        this.terms.set(term.getId().intValue(), termService.save(term));
        injectService.getTermDuplicatedTableController().getTerms().set(injectService.getTermDuplicatedTableController().findById(term.getId()), term);
        injectService.getTermDuplicatedTableController().getTableDuplicatedTerms().refresh();
        tableTerms.refresh();
      });
      return new SimpleObjectProperty<>(checkBox);
    });
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableTerms.widthProperty().multiply(other)));
  }

  private void fillInTableView() {
    new Thread(() -> {
      terms = termService.getAllByIsIgnoredAndIsAdded(false, false);

      Platform.runLater(() -> tableTerms.setItems(FXCollections.observableArrayList(terms)));

    }).start();
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
    new Thread(() -> {
      List<Term> filteredTerms = terms.stream().filter(term ->
          (term.getEnglishName() != null && WordUtils.removeNonLiteralCharacters(term.getEnglishName()).toLowerCase()
                .contains(WordUtils.removeNonLiteralCharacters(value).toLowerCase()))
              || (term.getPolishName() != null && WordUtils.removeNonLiteralCharacters(term.getPolishName())
                .toLowerCase().contains(WordUtils.removeNonLiteralCharacters(value).toLowerCase()))
              || (term.getAmericanName() != null && WordUtils.removeNonLiteralCharacters(term.getAmericanName())
                .toLowerCase().contains(WordUtils.removeNonLiteralCharacters(value).toLowerCase()))
              || (term.getOtherName() != null && WordUtils.removeNonLiteralCharacters(term.getOtherName())
                .toLowerCase().contains(WordUtils.removeNonLiteralCharacters(value).toLowerCase())))
          .collect(Collectors.toList());
      Platform.runLater(() -> tableTerms.setItems(FXCollections.observableArrayList(filteredTerms)));
    }).start();
  }

  private void filterTableByCategory(String value) {
    new Thread(() -> {
      List<Term> filteredTerms = terms.stream()
          .filter(term ->
              term.getCategory().toLowerCase().contains(value.toLowerCase()))
          .collect(Collectors.toList());
      Platform.runLater(() -> tableTerms.setItems(FXCollections.observableArrayList(filteredTerms)));
    }).start();
  }

  public void updateIsAdded(Long id) {
    try {
      Term term = terms.get(findById(id));
      boolean newStatus = !term.getIsIgnored();
      if (newStatus) {
        term.setIsAdded(true);
        term.setIsIgnored(true);
      } else {
        term.setIsAdded(false);
        term.setIsIgnored(false);
      }
      tableTerms.refresh();
    } catch (ArrayIndexOutOfBoundsException e) {
    }
  }

  public void updateIsIgnore(Long id) {
    try {
      Term term = terms.get(findById(id));
      boolean newStatus = !term.getIsIgnored();
      term.setIsIgnored(newStatus);
      tableTerms.refresh();
    } catch (ArrayIndexOutOfBoundsException e) {
    }
  }

  private int findById(Long id) {
    List<Long> ids = terms.stream().map(Term::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }

  @Override
  public void run(String... args) throws Exception {
/*    for (int i = 229587; i <= 230620; i++) {
      Optional<Term> term = termService.getById((long) i);
      term.ifPresent(te -> {
        String englishNames = te.getEnglishName();
        if (englishNames.startsWith("a ")) {
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        } else if (englishNames.startsWith("an ")) {
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        } else if (englishNames.startsWith("the ")) {
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        } else if (englishNames.startsWith("to ")) {
          englishNames = englishNames.substring(englishNames.indexOf(" "));
        }
        dikiScrapper.webScrap(englishNames.trim());
        babScrapper.webScrap(englishNames.trim());
        cambridgeDictionaryScrapper.webScrap(englishNames.trim());
      });
    }*/

/*    List<Term> terms = termService.getAllByIsIgnoredAndIsAdded(false, false);
    terms.forEach(term -> {
      String t = StringUtils.trimToEmpty(term.getEnglishName().split(";")[0]);
      if (StringUtils.isNoneEmpty(t)) {
        dikiScrapper.webScrap(t);
        babScrapper.webScrap(t);
        cambridgeDictionaryScrapper.webScrap(t);
      }
    });*/
  }
}
