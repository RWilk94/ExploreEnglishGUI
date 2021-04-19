package rwilk.exploreenglish.scrapper.oxford;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OxfordDictionaryScrapper implements CommandLineRunner {

  private static final List<String> hrefs = Arrays.asList(
      "https://www.oxfordlearnersdictionaries.com/topic/category/animals_1",
      "https://www.oxfordlearnersdictionaries.com/topic/category/appearance_1",
      "https://www.oxfordlearnersdictionaries.com/topic/category/communication",
      "https://www.oxfordlearnersdictionaries.com/topic/category/culture",
      "https://www.oxfordlearnersdictionaries.com/topic/category/food-and-drink",
      "https://www.oxfordlearnersdictionaries.com/topic/category/functions",
      "https://www.oxfordlearnersdictionaries.com/topic/category/health",
      "https://www.oxfordlearnersdictionaries.com/topic/category/homes-and-buildings",
      "https://www.oxfordlearnersdictionaries.com/topic/category/leisure",
      "https://www.oxfordlearnersdictionaries.com/topic/category/notions",
      "https://www.oxfordlearnersdictionaries.com/topic/category/people",
      "https://www.oxfordlearnersdictionaries.com/topic/category/politics-and-society",
      "https://www.oxfordlearnersdictionaries.com/topic/category/science-and-technology",
      "https://www.oxfordlearnersdictionaries.com/topic/category/sport",
      "https://www.oxfordlearnersdictionaries.com/topic/category/the-natural-world",
      "https://www.oxfordlearnersdictionaries.com/topic/category/time-and-space",
      "https://www.oxfordlearnersdictionaries.com/topic/category/travel",
      "https://www.oxfordlearnersdictionaries.com/topic/category/work-and-business");

  private final TermService termService;

  public OxfordDictionaryScrapper(TermService termService) {
    this.termService = termService;
  }

  public void webScrap(String href) {
    try {
      Document document = Jsoup.connect(href).timeout(10000).get();
      Elements containers = document.select("div.topic-box");

      for (Element container : containers) {
        for (Element url : container.select("div.l3")) {
          for (Element a : url.select("a")) {
            webScrapTopic(a.attr("href"));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void webScrapTopic(String href) {
    log.info("START scrapping {}", href);
    try {
      Document document = Jsoup.connect(href).timeout(10000).get();
      Map<String, String> topicMap = document.select("select#filterList").get(0)
          .children()
          .stream()
          .collect(Collectors.toMap(Element::text, value -> value.attr("value")));
      String dataTag = href.substring(href.indexOf("=") + 1);

      List<Element> words = document.select("div.responsive_entry_center")
          .select("div.responsive_row")
          .select("div#wordlistsContentPanel")
          .select("li").stream()
          .filter(a -> !a.attr("data-" + dataTag).isEmpty())
          .collect(Collectors.toList());

      String title = "[" + document.select("h1").text() + "]";
      String lesson = topicMap.entrySet().stream()
          .filter(entrySet -> entrySet.getValue().equals(dataTag))
          .map(Map.Entry::getKey)
          .findFirst()
          .orElse("");
      List<Term> terms = new ArrayList<>();

      for (Element word : words) {
        String level = word.children().get(2).text();

        terms.add(Term.builder()
            .category(title + " " + lesson + " " + level)
            .source("oxfordDictionary")
            .englishName(word.children().get(0).text())
            .polishName("")
            .partOfSpeech(word.children().get(1).text())
            .build());
      }

      termService.saveAll(terms);
      log.info("FINISH scrapping {}", href);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run(String... args) throws Exception {
/*    for (String href : hrefs) {
      webScrap(href);
    }*/
  }
}
