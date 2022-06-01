package rwilk.exploreenglish.scrapper.longman;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LongmanScrapper {

  private static final String BASE_URL = "https://www.ldoceonline.com/dictionary/";
  private static final String SOURCE = "Longman";
  private final TermService termService;

  public static void main(String[] args) {
    LongmanScrapper scrapper = new LongmanScrapper(null);
    scrapper.webScrap("large", false);
//    scrapper.webScrap("good morning");

  }

  public List<Term> webScrap(final String key, final boolean forceTranslate) {
    log.info("[Longman scrapper] {}", key);

    if (!forceTranslate) {
      List<Term> cachedResults = termService.getTermsByCategoryAndSource(key, SOURCE);
      if (!cachedResults.isEmpty()) {
        log.info("[Longman scrapper] return cached results");
        return cachedResults;
      }
    }

    try {
      final Document document = Jsoup.connect(BASE_URL.concat(key).replace(" ", "-"))
                                     .timeout(10000).get();
      final Elements elements = document.select("span.dictentry");

      final List<Term> terms = new ArrayList<>();
      for (Element element : elements) {
        final Element head = element.select("span.Head").first();
        final String enName = head.select("span.HWD").text();
        final String enMp3File = head.select("span.speaker").select("span.brefile").attr("data-src-mp3");
        final String usMp3File = head.select("span.speaker").select("span.amefile").attr("data-src-mp3");

        final String partOfSpeech = Optional.ofNullable(head.select("span.POS"))
                                            .map(Elements::text)
                                            .orElse("");
        final String style = Optional.ofNullable(head.select("span.REGISTERLAB"))
                                     .map(Elements::text)
                                     .orElse("");

        final String otherForms = Optional.ofNullable(head.select("span.Inflections"))
                                          .map(Elements::text)
                                          .orElse("");
        final String grammarTag = Optional.ofNullable(head.select("span.GRAM"))
                                          .map(Elements::text)
                                          .orElse("");
        final List<String> definitions = new ArrayList<>();
        final List<String> relatedWords = new ArrayList<>();
        final List<String> englishSentence = new ArrayList<>();
        final List<String> polishSentence = new ArrayList<>();

        for (Element sense : element.select("span.Sense")) {
          String def = Optional.ofNullable(sense.select("span.DEF"))
                                     .map(Elements::text)
                                     .orElse("");
          if (StringUtils.isNotBlank(def)) {
            final String opposite = sense.select("span.OPP").text();
            if (StringUtils.isNoneEmpty(opposite)) {
              def = def
                  .concat("[")
                  .concat("przeciwieństwo: ")
                  .concat(opposite.replace("OPP", "").trim())
                  .concat("] ");
            }
            final String synonym =  sense.select("span.SYN").text();
            if (StringUtils.isNoneEmpty(synonym)) {
              def = def
                  .concat("[")
                  .concat("synonim: ")
                  .concat(synonym.replace("SYN", "").trim())
                  .concat("]");
            }
            definitions.add(def.trim());
          }

          final String relatedWord = Optional.ofNullable(sense.select("span.RELATEDWD"))
                                             .map(Elements::text)
                                             .map(text -> text.replace("→", "").trim())
                                             .orElse("");
          if (StringUtils.isNotBlank(relatedWord)) {
            relatedWords.add(relatedWord);
          }

          for (Element example : sense.select("span.EXAMPLE")) {
            final String text = example.text();

            final Elements mp3Hrefs = example.select("span.speaker");
            String mp3Href = "";
            if (mp3Hrefs != null && !mp3Hrefs.isEmpty()) {
              mp3Href = mp3Hrefs.get(0).attr("data-src-mp3");
            }
            englishSentence.add(text);
            polishSentence.add(StringUtils.isBlank(mp3Href) ? "EMPTY" : mp3Href);
          }

        }

        String otherName = enName;
        if (StringUtils.isNotBlank(style)) {
          otherName = otherName.concat(" (").concat(style).concat(")");
        }
        if (StringUtils.isNotBlank(enMp3File)) {
          otherName = otherName.concat(" [").concat(enMp3File).concat("]");
        }

        final Term term = Term.builder()
                              .englishName(enName)
                              .otherName(otherName)
                              .americanName(enName.concat(" [").concat(usMp3File).concat("]"))
                              .polishName(String.join("; ", definitions))
                              .partOfSpeech(StringUtils.isBlank(grammarTag)
                                                    ? partOfSpeech
                                                    : partOfSpeech.concat(" ").concat(grammarTag))
                              .synonym(String.join("; ", relatedWords))
                              .comparative(otherForms)
                              .englishSentence(StringUtils.left(String.join("; ", englishSentence), 2000))
                              .polishSentence(StringUtils.left(String.join("; ", polishSentence), 2000))
                              .source("Longman")
                              .build();
        terms.add(term);
      }
      return saveTerms(terms, key);

    } catch (Exception e) {
      log.error("[Longman scrapper] exception during scrapping {}", key, e);
      return Collections.emptyList();
    }
  }

  private List<Term> saveTerms(List<Term> terms, String englishWord) {
    for (Term term : terms) {
      term.setCategory(englishWord);
      term.setIsAdded(false);
      term.setIsIgnored(false);
    }
    log.info("[Longman scrapper] save and return terms");
    return termService.saveAll(terms);
  }

}
