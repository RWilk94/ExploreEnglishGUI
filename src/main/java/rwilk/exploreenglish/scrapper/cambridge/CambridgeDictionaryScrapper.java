package rwilk.exploreenglish.scrapper.cambridge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;
import rwilk.exploreenglish.utils.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CambridgeDictionaryScrapper {

  private static final String BASE_URL = "https://dictionary.cambridge.org";
  private static final String SOURCE = "cambridge";
  private final TermService termService;

  public CambridgeDictionaryScrapper(TermService termService) {
    this.termService = termService;
  }

  public List<Term> webScrap(String englishTerm) {
    return webScrap(englishTerm, null);
  }

  private List<Term> webScrap(String englishTerm, String requestUrl) {
    log.info("[CambridgeDictionary scrapper] {} {}", englishTerm, StringUtils.trimToEmpty(requestUrl));

    List<Term> cachedResults = termService.getTermsByCategoryAndSource(englishTerm, SOURCE);
    if (!cachedResults.isEmpty()) {
      log.info("[CambridgeDictionary scrapper] return cached results");
      return cachedResults;
    }

    try {
      List<Term> terms = new ArrayList<>();
      String url = BASE_URL + "/us/dictionary/english-polish/"
          + WordUtils.trimAndReplace(englishTerm, "-");

      Document document = Jsoup.connect(requestUrl != null ? requestUrl : url)
          .cookie("XSRF-TOKEN", "88c1b5fa-e8fc-4065-96cc-95c48356acb2")
          .userAgent("Mozilla")
          .timeout(10000)
          .get();
      Elements elements = document.select("div.entry-body");

      Elements header = elements.select("div.normal-entry");
      String partOfSpeech = header.select("span.pos").text();
      String pastTense = "";
      String pastParticiple = "";
      String comparative = "";
      String superlative = "";

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

      Elements body = elements.select("div.normal-entry-body").select("div.sense-block");

      List<String> meanings = new ArrayList<>();
      List<String> englishSentences = new ArrayList<>();
      List<Term> otherTerms = new ArrayList<>();
      for (Element element : body) {
        // if (!element.hasClass("dsense-noh")) {
        if (StringUtils.isEmpty(element.select("span.dphrase-title").text())) {
          String polishName = element.select("span.trans").text();
          for (Element example : element.select("div.examp")) {
            englishSentences.add(example.text());
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
          .englishName(document.select("div.di-title").text())
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

      // other links
      List<String> urls = new ArrayList<>();
      for (Element element : document.select("amp-accordion").select("li")) {
        if (element.select("span.hw").text().equals(englishTerm)) {
          urls.add(BASE_URL + element.select("a").attr("href"));
        }
      }
      if (requestUrl == null) {
        for (String otherUrl : urls) {
          terms.addAll(webScrap(englishTerm, otherUrl));
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

}
