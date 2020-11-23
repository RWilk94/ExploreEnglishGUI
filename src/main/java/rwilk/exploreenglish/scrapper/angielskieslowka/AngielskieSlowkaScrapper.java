package rwilk.exploreenglish.scrapper.angielskieslowka;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AngielskieSlowkaScrapper {

  private static final float PAGE_SIZE = 50.0f;
  private static final String BASE_URL = "https://angielskie-slowka.pl";
  private final TermService termService;

  public AngielskieSlowkaScrapper(TermService termService) {
    this.termService = termService;
  }

  public void webScrap() {
    try {
      Document document = Jsoup.connect("https://angielskie-slowka.pl/slowka-angielskie").timeout(10000).get();
      Elements elements = document.select("div.categories").get(0).children();

      List<Term> terms = new ArrayList<>();
      for (Element element : elements) {
        String text = element.select("a.category").text();
        log.info("START scrapping {}", text);

        String category = text.substring(0, text.indexOf("(")).trim();
        String href = BASE_URL + element.select("a.category").attr("href");
        int numberOfElements = Integer.parseInt(text.replaceAll("[^0-9]", ""));
        int numberOfPages = (int) Math.ceil(numberOfElements / PAGE_SIZE);

        for (int i = 1; i <= numberOfPages; i++) {
          terms.addAll(webScrapCategory(href + "," + i, category));
        }
        termService.saveAll(terms);
        terms = new ArrayList<>();
      }
    } catch (Exception e) {
      log.error("[AngielskieSlowkaScrapper]", e);
    }
  }

  public void webScrapUserList(String url, String category) {
    try {
      Document document = Jsoup.connect(url).timeout(10000).get();
      Elements elements = document.select("table.standard").first().children().first().children();
      List<Term> terms = new ArrayList<>();
      for (Element element : elements) {
        Elements tds = element.select("td");
        if (tds.size() == 3) {
          String listUrl = BASE_URL + tds.get(0).select("a").first().attr("href");
          int numberOfPages = getNumberOfPages(listUrl, 1);
          for (int i = 1; i <= numberOfPages; i++) {
            terms.addAll(webScrapCategory(listUrl + "," + i, category));
          }
        }
      }
      System.out.println();
      termService.saveAll(terms);
    } catch (Exception e) {
      log.error("[AngielskieSlowkaScrapper]", e);
    }
  }

  public void webScrapPopularList(String url) {
    try {
      Document document = Jsoup.connect(url).timeout(10000).get();
      Elements elements = document.select("table.standard").first().children().first().children();
      List<Term> terms = new ArrayList<>();
      for (Element element : elements) {
        Elements tds = element.select("td");
        if (tds.size() == 5) {
          log.info("START scrapping {}", tds.get(0).text());
          String listUrl = BASE_URL + tds.get(0).select("a").first().attr("href");
          int numberOfPages = getNumberOfPages(listUrl, 1);
          for (int i = 1; i <= numberOfPages; i++) {
            terms.addAll(webScrapCategory(listUrl + "," + i, tds.get(0).text()));
          }
        }
        termService.saveAll(terms);
        terms = new ArrayList<>();
      }
    } catch (Exception e) {
      log.error("[AngielskieSlowkaScrapper]", e);
    }
  }

  private int getNumberOfPages(String url, int page) {
    try {
      Document document = Jsoup.connect(url + "," + page).timeout(10000).get();
      Elements elements = document.select("ul.pagination");
      if (elements.size() == 0) {
        return 1;
      } else {
        String className = elements.first().select("li.next").first().className();
        if (className.contains("disable")) {
          return 1;
        } else {
          return 1 + getNumberOfPages(url, page + 1);
        }
      }
    } catch (Exception e) {
      log.error("[AngielskieSlowkaScrapper]", e);
      return -1;
    }
  }

  private List<Term> webScrapCategory(String url, String category) {
    try {
      List<Term> terms = new ArrayList<>();

      Document document = Jsoup.connect(url).timeout(10000).get();
      Elements elements = document.select("table.standard").select("tr");
      for (Element row : elements) {

        Elements tds = row.select("td");
        if (tds.size() == 3) {
          Term term = Term.builder()
              .englishName(tds.get(0).text())
              .polishName(tds.get(1).text())
              .category(category)
              .source("angielskie-slowka")
              .build();
          terms.add(term);
        }
      }
      return terms;
    } catch (Exception e) {
      log.error("[AngielskieSlowkaScrapper]", e);
      return new ArrayList<>();
    }
  }

}
