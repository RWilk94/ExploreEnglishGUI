package rwilk.exploreenglish.scrapper.ellalanguage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EllaLanguageScrapper implements CommandLineRunner {

  private final static int NUMBER_OF_PAGE = 12;
  private final static String BASE_URL = "https://ellalanguage.com/blog/slownictwo/page/";
  private final TermService termService;

  public EllaLanguageScrapper(TermService termService) {
    this.termService = termService;
  }

  public void webScrap() {
    for (int i = NUMBER_OF_PAGE; i > 0; i--) {
      log.info("START scrapping page: {}", i);
      try {

        Document document = Jsoup.connect(BASE_URL.concat(String.valueOf(i)))
            .timeout(10000).get();
        Elements elements = document.select("h2.entry-title");

        for (Element element : elements) {
          log.info("START scrapping {}", element.select("a").text());
          List<Term> terms = webScrapLesson(element.select("a").attr("href"), element.select("a").text());
          termService.saveAll(terms);
        }
      } catch (Exception e) {
        log.error("EXCEPTION during scrapping due to: {}", e.getMessage(), e);
      }
    }
  }

  private List<Term> webScrapLesson(String url, String title) {
    try {
      List<Term> terms = new ArrayList<>();
      Document document = Jsoup.connect(url).timeout(10000).get();

      Elements tables = document.select("table");
      for (Element table : tables) {
        Elements rows = table.select("tr");
        for (Element row : rows) {
          List<String> texts = row.children().stream()
              .map(Element::text)
              .collect(Collectors.toList());
          if (texts.size() > 1) {
            Term term = Term.builder()
                .englishName(texts.get(0))
                .polishName(texts.get(texts.size() - 1))
                .source("ellaLanguage")
                .category(title)
                .build();
            terms.add(term);
          }
        }
      }

      Elements words = document.select(".leftonedot30");
      for (Element word : words) {
        Term term = extractElement(word, title);
        if (term != null) {
          terms.add(term);
        }
      }

      Elements sentences = document.select(".leftonedot60");
      for (Element sentence : sentences) {
        Term term = extractElement(sentence, title);
        if (term != null) {
          terms.add(term);
        }
      }
      return terms;
    } catch (Exception e) {
      return null;
    }
  }

  private Term extractElement(Element element, String title) {
    String[] split = element.text().split("–");
    if (split.length > 1 && StringUtils.isNoneEmpty(split[0]) && StringUtils.isNoneEmpty(split[1])) {
      String englishName = split[0].trim();
      String polishName = split[1].trim();

      return Term.builder()
          .englishName(englishName)
          .polishName(polishName)
          .source("ellaLanguage")
          .category(title)
          .build();
    }
    return null;
  }

    @Override
    public void run (String...args) throws Exception {
       // webScrap();
      // webScrapLesson("https://ellalanguage.com/blog/rozmowki-angielskie/", "test");
      // 396
//      webScrapLesson("https://ellalanguage.com/blog/drzewa-po-angielsku/", "test");
      // 183
      // webScrapLesson("https://ellalanguage.com/blog/thanksgiving-swieto-dziekczynienia/", "test");
      // webScrapLesson("https://ellalanguage.com/blog/phrasal-verb-dla-poczatkujacych/", "test");
//    webScrapLesson("https://ellalanguage.com/blog/liczby-po-angielsku/", "test");
    }


  }
