package rwilk.exploreenglish.scrapper.bab;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class BabScrapper {

  public List<Term> webScrap(String englishTerm) {
    log.info("[Bab scrapper] {}", englishTerm);
    try {
      List<Term> terms = new ArrayList<>();

      String url = "https://pl.bab.la/slownik/angielski-polski/" + englishTerm.
          trim()
          .replaceAll("British English", "")
          .trim()
          .replaceAll("American English", "")
          .trim()
          .replaceAll(" ", "-")
          .replaceAll("something", "sth")
          .replaceAll("somebody", "sb");

      Document document = Jsoup.connect(url)
          .cookie("PHPSESSID", "odhs2tres3v5ho5t0s8asi1b44")
          .userAgent("Mozilla")
          .timeout(10000)
          .get();
      Elements elements = document.select("div.quick-results").get(0).children();

      boolean skip = false;
      for (Element result : elements) {
        if (result.className().equals("quick-results-header")) {
          if (!skip) {
            skip = true;
            continue;
          } else {
            break;
          }
        }
        String englishName = result.select("div.quick-result-option").select("a.babQuickResult").text();
        if (!result.select("div.quick-result-overview").select("ul.sense-group-results").isEmpty()) {
          Elements translations = result.select("div.quick-result-overview").select("ul.sense-group-results").get(0).children();


          Term term = new Term();
          term.setEnglishName(englishName);
          if (!result.select("div.quick-result-option").select("span.suffix").isEmpty()) {
            term.setPartOfSpeech(
                result.select("div.quick-result-option").select("span.suffix").get(0).text().replace("{", "").replace("}", ""));
          } else {
            term.setPartOfSpeech("");
          }
          term.setSource("bab");
          List<String> polishTranslations = new ArrayList<>();
          for (Element translation : translations) {
            polishTranslations.add(translation.text());
          }
          term.setPolishName(String.join(", ", polishTranslations));
          terms.add(term);
        }
      }
      return terms;
    } catch (Exception e) {
      log.error("[{}] ", englishTerm/*, e*/);
      return Collections.emptyList();
    }
  }

}
