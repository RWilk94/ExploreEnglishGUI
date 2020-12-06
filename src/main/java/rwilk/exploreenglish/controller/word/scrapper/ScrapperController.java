package rwilk.exploreenglish.controller.word.scrapper;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.scrapper.bab.BabScrapper;
import rwilk.exploreenglish.scrapper.cambridge.CambridgeDictionaryScrapper;
import rwilk.exploreenglish.scrapper.diki.DikiScrapper;
import rwilk.exploreenglish.service.InjectService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
public class ScrapperController implements Initializable {
  private final InjectService injectService;
  private final BabScrapper babScrapper;
  private final DikiScrapper dikiScrapper;
  private final CambridgeDictionaryScrapper cambridgeDictionaryScrapper;
  public TabPane tabPaneScrapper;

  public ScrapperController(InjectService injectService, BabScrapper babScrapper, DikiScrapper dikiScrapper, CambridgeDictionaryScrapper cambridgeDictionaryScrapper) {
    this.injectService = injectService;
    this.babScrapper = babScrapper;
    this.dikiScrapper = dikiScrapper;
    this.cambridgeDictionaryScrapper = cambridgeDictionaryScrapper;
    this.injectService.setScrapperController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void webScrap(String englishTerm) {
    tabPaneScrapper.getTabs().clear();
    CompletableFuture.supplyAsync(
        () -> dikiScrapper.webScrap(englishTerm))
        .thenAccept(this::createTab)
        .exceptionally(ex -> {
          createEmptyTab("diki");
          return null;
        });
    CompletableFuture.supplyAsync(
        () -> babScrapper.webScrap(englishTerm))
        .thenAccept(this::createTab)
        .exceptionally(ex -> {
          createEmptyTab("bab");
          return null;
        });
    CompletableFuture.supplyAsync(
        () -> cambridgeDictionaryScrapper.webScrap(englishTerm))
        .thenAccept(this::createTab)
        .exceptionally(ex -> {
          createEmptyTab("cambridge");
          return null;
        });
  }

  private void createTab(List<Term> terms) {
    if (terms.isEmpty()) {
      throw new IllegalArgumentException();
    }

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        for (Term term : terms) {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            ScrollPane vBox = fxmlLoader.load(getClass().getResource("/scene/word/scrapper_tab.fxml").openStream());
            ScrapperTabController scrapperTabController = fxmlLoader.getController();
            scrapperTabController.init(term, injectService);

            Tab tab = new Tab(term.getEnglishName() + " |" + term.getSource() + "|");
            tab.setContent(vBox);
            tabPaneScrapper.getTabs().add(tab);
          } catch (Exception e) {
            log.error("[updateViewAfterWebScrap]", e);
          }
        }
      }
    });
  }

  public void createEmptyTab(String source) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        try {
          FXMLLoader fxmlLoader = new FXMLLoader();
          HBox hBox = fxmlLoader.load(getClass().getResource("/scene/word/scrapper_empty_tab.fxml").openStream());
          Tab tab = new Tab(source);
          tab.setContent(hBox);
          tabPaneScrapper.getTabs().add(tab);
        } catch (Exception e) {
          log.error("[createEmptyTab]", e);
        }
      }
    });
  }

}
