package rwilk.exploreenglish.controller.word.scrapper;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.InjectService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class ScrapperTabController implements Initializable {

  public TextField textFieldEnglishName;
  public TextField textFieldAmericanName;
  public TextField textFieldOtherNames;
  public ListView<String> listViewMeaning;
  public ListView<String> listViewSentences;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPlural;
  public TextField textFieldSynonym;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(Term term) {
    textFieldEnglishName.setText(StringUtils.trimToEmpty(term.getEnglishName()));
    textFieldAmericanName.setText(StringUtils.trimToEmpty(term.getAmericanName()));
    textFieldOtherNames.setText(StringUtils.trimToEmpty(term.getOtherName()));
    textFieldPastTense.setText(StringUtils.trimToEmpty(term.getPastTense()));
    textFieldPastParticiple.setText(StringUtils.trimToEmpty(term.getPastParticiple()));
    textFieldComparative.setText(StringUtils.trimToEmpty(term.getComparative()));
    textFieldSuperlative.setText(StringUtils.trimToEmpty(term.getSuperlative()));
    textFieldPlural.setText(StringUtils.trimToEmpty(term.getPlural()));


    listViewMeaning.setItems(FXCollections.observableArrayList(
        Arrays.stream(StringUtils.split(StringUtils.trimToEmpty(term.getPolishName()), ";")).map(StringUtils::trim).collect(Collectors.toList())
    ));

    if (StringUtils.isNoneEmpty(term.getEnglishSentence()) && StringUtils.isNoneEmpty(term.getPolishSentence())) {
      String[] split = StringUtils.split(term.getEnglishSentence(), ";");
      String[] split1 = StringUtils.split(term.getPolishSentence(), ";");
      List<String> sentences = new ArrayList<>();
      if (split.length == split1.length) {
        for (int i = 0; i < split.length; i++) {
          sentences.add(split[i].trim() + " " + split1[i].trim());
        }
      }

      listViewSentences.setItems(FXCollections.observableArrayList(sentences));
    }
  }

  public void listViewMeaningOnMouseClicked(MouseEvent mouseEvent) {
  }

  public void buttonLoadDataOnAction(ActionEvent actionEvent) {
  }

}
