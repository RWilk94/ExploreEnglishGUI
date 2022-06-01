package rwilk.exploreenglish.controller.word.scrapper;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.scrapper.bab.BabScrapper;
import rwilk.exploreenglish.scrapper.cambridge.CambridgeDictionaryScrapper;
import rwilk.exploreenglish.scrapper.diki.DikiScrapper;
import rwilk.exploreenglish.scrapper.longman.LongmanScrapper;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.TermService;
import rwilk.exploreenglish.utils.WordUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ScrapperController implements Initializable, CommandLineRunner {
  private final Map<String, List<Term>> cache = new HashMap<>();

  private final InjectService injectService;
  private final BabScrapper babScrapper;
  private final DikiScrapper dikiScrapper;
  private final CambridgeDictionaryScrapper cambridgeDictionaryScrapper;
  private final LongmanScrapper longmanScrapper;
  private final TermService termService;
  public TabPane tabPaneScrapper;

  public ScrapperController(final InjectService injectService, final BabScrapper babScrapper,
                            final DikiScrapper dikiScrapper, final CambridgeDictionaryScrapper cambridgeDictionaryScrapper,
                            final LongmanScrapper longmanScrapper, final TermService termService) {
    this.injectService = injectService;
    this.babScrapper = babScrapper;
    this.dikiScrapper = dikiScrapper;
    this.cambridgeDictionaryScrapper = cambridgeDictionaryScrapper;
    this.longmanScrapper = longmanScrapper;
    this.termService = termService;
    this.injectService.setScrapperController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void webScrap(final List<String> englishTerms, final boolean forceTranslate) {
    tabPaneScrapper.getTabs().clear();
    CompletableFuture.supplyAsync(
        () -> {
          List<Term> terms = new ArrayList<>();
          for (String englishTerm : englishTerms) {
            englishTerm = WordUtils.trim(englishTerm);

            if (CollectionUtils.isNotEmpty(cache.get(englishTerm))) {
              terms.addAll(cache.get(englishTerm));
            } else {
              terms.addAll(dikiScrapper.webScrap(englishTerm, forceTranslate));
              terms.addAll(babScrapper.webScrap(englishTerm, forceTranslate));
              terms.addAll(cambridgeDictionaryScrapper.webScrap(englishTerm, forceTranslate));
              terms.addAll(longmanScrapper.webScrap(englishTerm, forceTranslate));

              if (cache.size() > 20) {
                cache.clear();
              }
              cache.put(englishTerm, terms);
            }
          }
          return terms;
        })
        .thenAccept(this::createTab)
        .exceptionally(ex -> {
          createEmptyTab("no records found");
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
        for (Term term : terms.stream().sorted(Comparator.comparing(term -> term.getEnglishName().length())).collect(Collectors.toList())) {
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

  @Override
  public void run(String... args) throws Exception {
/*    List<Term> terms = termService.getTermsBySourceAndCategoryLike("etutor", "%a2%");
    int i = 0;
    for (Term term : terms) {
      i++;
      log.info("{}/{}", i, terms.size());
      dikiScrapper.webScrap(term.getEnglishName());
      babScrapper.webScrap(term.getEnglishName());
      cambridgeDictionaryScrapper.webScrap(term.getEnglishName());
    }*/
  }
}
