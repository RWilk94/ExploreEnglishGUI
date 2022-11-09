package rwilk.exploreenglish.controller.word.scrapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.scrapper.bab.BabScrapper;
import rwilk.exploreenglish.scrapper.cambridge.CambridgeDictionaryScrapper;
import rwilk.exploreenglish.scrapper.diki.DikiScrapper;
import rwilk.exploreenglish.scrapper.longman.LongmanScrapper;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.TermService;
import rwilk.exploreenglish.utils.WordUtils;

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

    new Thread(() -> {
      final List<Term> terms = new ArrayList<>();
      try {
        terms.addAll(CompletableFuture.supplyAsync(
          () -> {
            final List<Term> translations = new ArrayList<>();
            for (String englishTerm : englishTerms) {
              englishTerm = WordUtils.trim(englishTerm);

              if (CollectionUtils.isNotEmpty(cache.get(englishTerm)) && !forceTranslate) {
                translations.addAll(cache.get(englishTerm));
              } else {
                translations.addAll(dikiScrapper.webScrap(englishTerm, forceTranslate));
                translations.addAll(babScrapper.webScrap(englishTerm, forceTranslate));
                translations.addAll(cambridgeDictionaryScrapper.webScrap(englishTerm, forceTranslate));
                translations.addAll(longmanScrapper.webScrap(englishTerm.replaceAll(Pattern.quote("!"), ""), forceTranslate));

                if (cache.size() > 20) {
                  cache.clear();
                }
                cache.put(englishTerm, translations);
              }
            }
            return translations;
          }).get());
      } catch (Exception e) {
        log.error("[webScrap]", e);
      }

      Platform.runLater(() -> {
        if (CollectionUtils.isNotEmpty(terms)) {
          createTab(terms);
        } else {
          createEmptyTab("no records found");
        }

      });
    }).start();
  }

  private void createTab(List<Term> terms) {
    if (terms.isEmpty()) {
      throw new IllegalArgumentException();
    }

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        for (Term term : terms.stream()
                              .filter(term -> StringUtils.isNoneEmpty(term.getEnglishName()))
                              .sorted(Comparator.comparing(term -> term.getEnglishName().length())).toList()) {
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
