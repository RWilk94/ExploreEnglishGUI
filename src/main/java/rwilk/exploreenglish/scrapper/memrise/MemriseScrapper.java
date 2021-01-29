package rwilk.exploreenglish.scrapper.memrise;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MemriseScrapper {
  private static final String BASE_URL = "https://app.memrise.com";
  private final TermService termService;

  public MemriseScrapper(TermService termService) {
    this.termService = termService;
  }

  public void webScrap(String url) {
    try {
      log.info("START scrapping {}", url);
      Document document = Jsoup.connect(url)
          .cookie("sessionid_2", "7treevu919k2yeljxb3fz172yn2ekt98z")
          .userAgent("Mozilla")
          .timeout(10000)
          .get();
      Elements elements = document.select("a.level");
      List<Term> terms = new ArrayList<>();
      for (Element element : elements) {
        String levelUrl = BASE_URL + element.select("a").first().attr("href");
        terms.addAll(webScrapLevel(levelUrl));
      }
      termService.saveAll(terms);
    } catch (Exception e) {
      log.error("[MemriseScrapper] ", e);
    }
  }

  private List<Term> webScrapLevel(String url) {
    try {
      log.info("START scrapping level {}", url);
      Document document = Jsoup.connect(url)
          .cookie("sessionid_2", "")
          .userAgent("Mozilla")
          .timeout(10000)
          .get();
      Elements elements = document.select("div.thing");
      List<Term> terms = new ArrayList<>();

      for (Element element : elements) {
        String en = element.select("div.col_a").first().text();
        String pl = element.select("div.col_b").first().text();
        String category = "[" + document.select("h1.course-name").text() + "]" + document.select("h3.progress-box-title").text();

        Term term = Term.builder()
            .englishName(en)
            .polishName(pl)
            .category(category)
            .source("memrise")
            .isIgnored(false)
            .isAdded(false)
            .build();
        terms.add(term);
      }
      return terms;
    } catch (Exception e) {
      log.error("[MemriseScrapper] ", e);
      return Collections.emptyList();
    }
  }
}
