package rwilk.exploreenglish.scrapper.speaklanguages;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SpeakLanguagesScrapper implements CommandLineRunner {

  private final List<String> urls = Arrays.asList("https://pl.speaklanguages.com/angielski/s%C5%82ownictwo/",
      "https://pl.speaklanguages.com/angielski/zwroty/");
  private final TermService termService;

  public SpeakLanguagesScrapper(TermService termService) {
    this.termService = termService;
  }

  @Transactional
  public void webScrap(SpeakLanguagesType type) {
    try {
      Document document = Jsoup.connect(urls.get(type.getValue())).timeout(10000).get();
      Elements elements = document.select("div#contents").get(0).select("a");

      for (Element lesson : elements) {
        log.info("START scrapping {}", lesson.attr("href"));
        List<Term> terms = webScrapLesson(lesson.attr("href"));
        termService.saveAll(terms);
      }

    } catch (Exception e) {
      log.error("EXCEPTION during scrapping due to: {}", e.getMessage(), e);
    }
  }

  private List<Term> webScrapLesson(String url) {
    try {
      List<Term> terms = new ArrayList<>();
      Document document = Jsoup.connect(url).timeout(10000).get();
      String category = document.select("h1").text();
      Elements elements = document.select("div#content").get(0).select("tr");

      for (Element row : elements) {
        if (row.select("td").size() == 2) {
          Term term = Term.builder()
              .englishName(row.select("td").get(0).text())
              .polishName(row.select("td").get(1).text())
              .source("speakLanguages")
              .category(category)
              .build();
          terms.add(term);
        }
      }
      return terms;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public void run(String... args) throws Exception {
     // webScrap(SpeakLanguagesType.WORDS);
     // webScrap(SpeakLanguagesType.PHRASES);
  }
}
