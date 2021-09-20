package rwilk.exploreenglish.scrapper.langeek;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanGeekScrapper implements CommandLineRunner {

  private static final String BASE_URL = "https://langeek.co";
  private static final List<String> courses = List.of(
          "https://langeek.co/en/vocab/level-based",
          "https://langeek.co/en/vocab/topic-related",
          "https://langeek.co/en/vocab/book-collection/1/ielts",
          "https://langeek.co/en/vocab/book-collection/2/sat",
          "https://langeek.co/en/vocab/book-collection/3/toefl"
                                                     );
  private final TermService termService;

  public List<String> webScrapCourse(final String course) throws IOException {
    final Document document = Jsoup.connect(course)
                                   .timeout(10000).get();
    final Elements elements = document.select("a.stretched-link");

    List<String> hrefs = new ArrayList<>();
    for (Element element : elements) {
      final String attr = element.attr("href");
      hrefs.add(attr);
    }
    return hrefs.stream()
                .distinct()
                .collect(Collectors.toList());
  }

  public List<String> webScrapLessonUrls(final String lesson) throws IOException {
    final Document document = Jsoup.connect(BASE_URL + lesson)
                                   .timeout(10000).get();
    final Elements elements = document.select("div.tab");

    List<String> hrefs = new ArrayList<>();
    for (Element element : elements) {

      final String attr = element.select("a").get(1).attr("href");
      hrefs.add(attr);
    }
    return hrefs;
  }

  public List<Term> webScrapLesson(final String href) throws IOException {

    final Document document = Jsoup.connect(BASE_URL + href)
                                   .timeout(10000).get();
    final String mainTitle = document.select("h1").text();
    String category = "";

    final Elements titles = document.select("div.Grammar_breadcrumbText__3-yFK").select("a");
    for (Element title : titles) {
      if (titles.indexOf(title) > 1) {
        category = category
                .concat("[")
                .concat(title.text())
                .concat("]");
      }
    }
    category = category
            .concat("[")
            .concat(mainTitle)
            .concat("]");

    final Elements elements = document.select("div.WordItem_wordItem__2VUgb");

    final List<Term> terms = new ArrayList<>();

    for (Element element : elements) {
      final String englishText = element.select("span.WordItem_wordTitle__2HxXb").text();
      final String partOfSpeech = element.select("span.WordItem_translationPOS__1JbWy").text();
      final String englishDefinition = element.select("p.WordItem_translationBox__1hFmj").text();

      final Term term = Term.builder()
                            .englishName(englishText)
                            .polishName("")
                            .partOfSpeech(partOfSpeech)
                            .englishSentence(englishDefinition)
                            .source("LanGeek")
                            .category(category)
                            .build();
      terms.add(term);
    }

    return terms;
  }

  @Override
  public void run(final String... args) throws Exception {
/*    final List<Term> terms = new ArrayList<>();
    for (String course : courses) {
      log.info("Scrapping {}", course);
      final List<String> lessons = webScrapCourse(course);

      for (String lesson : lessons) {
        final List<String> hrefs = webScrapLessonUrls(lesson);

        for (String href : hrefs) {
          terms.addAll(webScrapLesson(href));
        }
      }

      termService.saveAll(terms);
      terms.clear();
    }*/
  }
}
