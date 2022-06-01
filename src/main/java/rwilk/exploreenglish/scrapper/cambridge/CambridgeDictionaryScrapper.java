package rwilk.exploreenglish.scrapper.cambridge;

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
import rwilk.exploreenglish.utils.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CambridgeDictionaryScrapper implements CommandLineRunner {

  private static final String BASE_URL = "https://dictionary.cambridge.org";
  private static final String SOURCE = "cambridge";
  private final TermService termService;

  public CambridgeDictionaryScrapper(TermService termService) {
    this.termService = termService;
  }

  public List<Term> webScrap(String englishTerm, boolean forceTranslate) {
    return webScrap(englishTerm, null, forceTranslate);
  }

  private List<Term> webScrap(String englishTerm, String requestUrl, boolean forceTranslate) {
    log.info("[CambridgeDictionary scrapper] {} {}", englishTerm, StringUtils.trimToEmpty(requestUrl));

    if (!forceTranslate) {
      List<Term> cachedResults = termService.getTermsByCategoryAndSource(englishTerm, SOURCE);
      if (!cachedResults.isEmpty()) {
        log.info("[CambridgeDictionary scrapper] return cached results");
        return cachedResults;
      }
    }

    try {
      List<Term> terms = new ArrayList<>();
      String url = BASE_URL + "/us/dictionary/english-polish/"
          + WordUtils.trimAndReplace(englishTerm, "-");

      Document document = Jsoup.connect(requestUrl != null ? requestUrl : url)
          // .cookie("XSRF-TOKEN", "88c1b5fa-e8fc-4065-96cc-95c48356acb2")
          .userAgent("Mozilla")
          .timeout(10000)
          .get();

      Elements elements = document.select("div.entry-body__el");
      for (Element entryBody : elements) {




      // Elements elements = document.select("div.entry-body");

      Elements header = entryBody.select("div.pos-header");
      String partOfSpeech = header.select("span.pos").text();
      String pastTense = "";
      String pastParticiple = "";
      String comparative = "";
      String superlative = "";
      String grammarTag = Optional.of(header.select("span.gram").select("span.gc").text()).orElse("");

      Elements grammarElements = header.select("span.inf-group");
      for (Element grammarElement : grammarElements) {

        if (StringUtils.trimToEmpty(grammarElement.text()).contains("past tense")) {
          pastTense = grammarElement.text().replace("past tense ", "").trim();
        } else if (StringUtils.trimToEmpty(grammarElement.text()).contains("past participle")) {
          pastParticiple = grammarElement.text().replace("past participle ", "").trim();
        } else if (StringUtils.isNoneEmpty(grammarElement.text()) && StringUtils.isEmpty(comparative)) {
          comparative = grammarElement.text();
        } else {
          superlative = "the " + grammarElement.text();
        }
      }

      Elements body = entryBody.select("div.pos-body").select("div.dsense");

      List<String> meanings = new ArrayList<>();
      List<String> englishSentences = new ArrayList<>();
      List<Term> otherTerms = new ArrayList<>();
      for (Element element : body) {
        if (StringUtils.isEmpty(element.select("span.dphrase-title").text())) {
          String polishName = element.select("span.trans").text();
          for (Element example : element.select("div.examp")) {
            englishSentences.add(example.text());
          }
          if (StringUtils.isNoneEmpty(grammarTag)) {
            polishName = polishName.concat(" [grammarTag: ").concat(extractGrammarTag(grammarTag)).concat("]");
          }
          meanings.add(polishName);
        } else {
          List<String> englishSentences2 = new ArrayList<>();
          for (Element example : element.select("div.examp")) {
            englishSentences2.add(example.text());
          }
          Term term = Term.builder()
              .englishName(element.select("span.dphrase-title").text())
              .americanName("")
              .otherName("")
              .polishName(element.select("span.trans").text())
              .englishSentence(String.join("; ", englishSentences2))
              .polishSentence("")
              .source(SOURCE)
              .build();
          otherTerms.add(term);
        }
      }
      Term term = Term.builder()
          .englishName(document.select("div.di-title").select("span.hw").get(0).text())
          .americanName("")
          .otherName("")
          .polishName(String.join("; ", meanings))
          .englishSentence(String.join("; ", englishSentences))
          .polishSentence("")
          .pastTense(pastTense)
          .pastParticiple(pastParticiple)
          .comparative(comparative)
          .superlative(superlative)
          .partOfSpeech(partOfSpeech)
          .source(SOURCE)
          .build();
      terms.add(term);
      terms.addAll(otherTerms);

    }
      // other links
      List<String> urls = new ArrayList<>();
      for (Element element : document.select("amp-accordion").select("li")) {
        if (element.select("span.hw").text().equals(englishTerm)) {
          urls.add(BASE_URL + element.select("a").attr("href"));
        }
      }
      if (requestUrl == null) {
        for (String otherUrl : urls) {
          terms.addAll(webScrap(englishTerm, otherUrl, forceTranslate));
        }
      }
      if (requestUrl == null) {
        return saveTerms(terms, englishTerm);
      }

      return terms;
    } catch (Exception e) {
      log.error("[CambridgeDictionary scrapper] exception during scrapping {}", englishTerm, e);
      return Collections.emptyList();
    }
  }

  private List<Term> saveTerms(List<Term> terms, String englishWord) {
    for (Term term : terms) {
      term.setCategory(englishWord);
      term.setIsAdded(false);
      term.setIsIgnored(false);
    }
    log.info("[CambridgeDictionary scrapper] save and return terms");
    return termService.saveAll(terms);
  }

  private String extractGrammarTag(String grammarTag) {
    if (grammarTag.equals("C")) {
      return "COUNTABLE";
    } else if (grammarTag.equals("U")) {
      return "UNCOUNTABLE";
    } else if (grammarTag.equals("C U")) {
      return "COUNTABLE AND UNCOUNTABLE";
    }
    return "";
  }

  @Override
  public void run(final String... args) throws Exception {

//    final List<Term> terms = webScrap("purse", true);
//    System.out.println();

  }
}
