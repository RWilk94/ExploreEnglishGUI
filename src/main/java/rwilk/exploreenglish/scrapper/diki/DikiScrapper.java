package rwilk.exploreenglish.scrapper.diki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.service.TermService;
import rwilk.exploreenglish.utils.WordUtils;

@Slf4j
@Service
public class DikiScrapper implements CommandLineRunner {

  private static final String SOURCE = "diki";
  private final TermService termService;

  public DikiScrapper(TermService termService) {
    this.termService = termService;
  }

  public List<Term> webScrap(String englishWord, boolean forceTranslate) {
    log.info("[Diki scrapper] {}", englishWord);

    if (!forceTranslate) {
      List<Term> cachedResults = termService.getTermsByCategoryAndSource(englishWord, SOURCE);
      if (!cachedResults.isEmpty()) {
        log.info("[Diki scrapper] return cached results");
        return cachedResults;
      }
    } else {
      final List<Term> cachedResults = termService.getTermsByCategoryAndSource(englishWord, SOURCE);
      termService.deleteAll(cachedResults);
    }

    try {
      List<Term> results = new ArrayList<>();

      String url = "https://www.diki.pl/slownik-angielskiego?q=" + WordUtils.trimAndReplace(englishWord, "+");
      Document document = Jsoup.connect(url)
                               // .cookie("autoLoginToken", "aCCJem50QmbkPp163sFnocX9ypt4eTHl5hA00rEs")
                               .userAgent("Mozilla").timeout(10000).get();
      Elements elements = document.select("div.diki-results-left-column").get(0).child(0)
                                  .select("div.dictionaryEntity"); // return elements containing translations

      for (Element dictionaryEntity : elements) {
        Term term = new Term();
        term.setSource(SOURCE);
        for (Element element : dictionaryEntity.children()) {
          if (element.hasClass("nativeToForeignEntrySlices")) {
            doScrap2(element, term, results);
          } else {
            doScrap(element, term, results);
          }
        }
        results.add(term);
      }
      return saveTerms(results, englishWord);
    } catch (Exception e) {
      log.error("[Diki scrapper] exception during scrapping {}", englishWord, e);
      return Collections.emptyList();
    }
  }

  private void doScrap2(final Element element, Term term, final List<Term> results) {
    if (element.hasClass("nativeToForeignEntrySlices")) {
      final List<String> englishNames = new ArrayList<>();
      for (Element child : element.select("li").get(0).children()) {
        if (child.hasClass("hw")) {
          englishNames.add(child.select("span.hw").text());
        } else if (child.hasClass("recordingsAndTranscriptions")) {


        } else if (child.hasClass("dictionaryEntryHeaderAdditionalInformation")) {

        } else if (child.hasClass("dictionaryEntryHeaderAdditionalInformation")) {

        } else if (child.hasClass("dictionaryEntryHeaderAdditionalInformation")) {

        } else if (child.hasClass("nativeToForeignMeanings")) {
          List<String> meanings = new ArrayList<>();
          List<String> englishSentences = new ArrayList<>();
          List<String> polishSentences = new ArrayList<>();
          for (Element sentence : child.select("li").get(0).children()) {
            if (sentence.hasClass("hw")) {
              meanings.add(sentence.select("span.hw").text());
            } else if (sentence.hasClass("exampleSentence")) {
              final String polish = sentence.select("div.exampleSentence").select("span.exampleSentenceTranslation").text();
              final String english = sentence.select("div.exampleSentence").text().replace(polish, "").trim();
              final List<String> audioHrefs = sentence.select("div.exampleSentence").select("span.audioIcon").stream()
                                                      .map(el -> el.attr("data-audio-url"))
                                                      .map("https://www.diki.pl"::concat)
                                                      .collect(Collectors.toList());
              if (audioHrefs.isEmpty()) {
                polishSentences.add(polish);
              } else {
                polishSentences.add(polish.concat(" ").concat(String.join("; ", audioHrefs)));
              }
              englishSentences.add(english);
            }
            term.setPolishName(StringUtils.left(String.join("; ", meanings), 2000));
            term.setEnglishSentence(StringUtils.left(String.join("; ", englishSentences), 2000));
            term.setPolishSentence(StringUtils.left(String.join("; ", polishSentences), 2000));
          }
        }
      }
      mapWordVersion(term, englishNames);
    }
  }

  private void doScrap(final Element element, Term term, final List<Term> results) {
    if (element.hasClass("hws")) {
      List<String> englishNames = new ArrayList<>();
      String englishName = "";
      String popularity = "";

      for (Element element1 : element.select("div.hws").select("h1").get(0).children()) {
        if (element1.hasClass("hw")) {
          if (StringUtils.isNoneEmpty(englishName)) {
            englishNames.add(englishName);
            englishName = "";
          }
          englishName = element1.select("span.hw").text();
        } else if (element1.hasClass("dictionaryEntryHeaderAdditionalInformation")) {
          String englishVariety = element1.select("span.dictionaryEntryHeaderAdditionalInformation").text();
          if (StringUtils.isNoneEmpty(englishVariety) && StringUtils.isNoneEmpty(englishName)
              && !englishVariety.equals("także:")) {
            if (englishVariety.contains("*****") || englishVariety.contains("****")
                || englishVariety.contains("***") || englishVariety.contains("**") || englishVariety.contains("*")) {
              popularity = englishVariety.replaceAll("[^*]", "");
              String englishVar = StringUtils.trimToEmpty(englishVariety.replaceAll("[*]", ""));
              if (StringUtils.isNoneEmpty(englishVar)) {
                englishName = englishName.concat(" (").concat(englishVar).concat(")").trim();
              }
            } else {
              englishName = englishName.concat(" (").concat(englishVariety).concat(")");
              englishNames.add(englishName);
              englishName = "";
            }
          }
        }
      }
      if (StringUtils.isNoneEmpty(englishName)) {
        englishNames.add(englishName);
      }
      mapWordVersion(term, englishNames);
      term.setPopularity(popularity);
    } else if (element.hasClass("partOfSpeechSectionHeader")) {
      if (StringUtils.isNoneEmpty(term.getPartOfSpeech())) {
        results.add(term);
        term = Term.builder()
                   .englishName(term.getEnglishName())
                   .americanName(term.getAmericanName())
                   .otherName(StringUtils.trimToEmpty(term.getOtherName()))
                   .popularity(term.getPopularity())
                   .source(term.getSource())
                   .build();
      }
      String partOfSpeech = element.select("span.partOfSpeech").text();
      term.setPartOfSpeech(partOfSpeech);
    } else if (element.hasClass("vf")) {
      String type = "";
      List<String> pastTenses = new ArrayList<>();
      List<String> pastParticiples = new ArrayList<>();
      for (Element element1 : element.select("div.vf").first().children()) {
        if (element1.hasClass("foreignTermHeader")) {
          if (element1.text().equals("past tense") || element1.text().equals("past tense (British English)") ||
              element1.text().equals("past tense (American English)") || element1.text().equals("past participle")
              || element1.text().equals("past participle (British English)") || element1.text().equals("past participle (American English)")) {
            type = element1.text();
          }
        } else if (element1.hasClass("foreignTermText")) {
          if (type.equals("past tense")) {
            pastTenses.add(element1.text());
          } else if (type.equals("past tense (British English)")) {
            pastTenses.add(element1.text() + " (British English)");
          } else if (type.equals("past tense (American English)")) {
            pastTenses.add(element1.text() + " (American English)");
          } else if (type.equals("past participle")) {
            pastParticiples.add(element1.text());
          } else if (type.equals("past participle (British English)")) {
            pastParticiples.add(element1.text() + " (British English)");
          } else if (type.equals("past participle (American English)")) {
            pastParticiples.add(element1.text() + " (American English)");
          }
        }
      }
      term.setPastTense(String.join("; ", pastTenses));
      term.setPastParticiple(String.join("; ", pastParticiples));
    } else if (element.hasClass("foreignToNativeMeanings")) {
      if (StringUtils.isNoneEmpty(term.getPolishName())) {
        results.add(term);
        term = Term.builder()
                   .englishName(term.getEnglishName())
                   .americanName(term.getAmericanName())
                   .otherName(StringUtils.trimToEmpty(term.getOtherName()))
                   .comparative(term.getComparative())
                   .superlative(term.getSuperlative())
                   .pastTense(term.getPastTense())
                   .pastParticiple(term.getPastParticiple())
                   .plural(term.getPlural())
                   .partOfSpeech(term.getPartOfSpeech())
                   .popularity(term.getPopularity())
                   .source(term.getSource())
                   .build();
      }
      List<String> meanings = new ArrayList<>();
      List<String> englishSentences = new ArrayList<>();
      List<String> polishSentences = new ArrayList<>();

      for (Element element2 : element.children()) {
        List<String> singleMeaning = new ArrayList<>();
        for (Element element1 : element2.children()) {
          if (element1.hasClass("hw")) {
            String meaning = element1.text();
            singleMeaning.add(StringUtils.trim(meaning.replace("(****)", "")));
          } else if (element1.hasClass("exampleSentence")) {
            String exampleSentence = element1.select("div.exampleSentence").text();
            String exampleSentenceTranslation = element1.select("div.exampleSentence").select("span.exampleSentenceTranslation").text();
            exampleSentence = exampleSentence.substring(0, exampleSentence.indexOf(exampleSentenceTranslation)).trim();
            englishSentences.add(exampleSentence);

            List<String> audioHrefs = element1.select("span.audioIcon").stream()
                                              .map(el -> el.attr("data-audio-url"))
                                              .map("https://www.diki.pl"::concat)
                                              .collect(Collectors.toList());
            if (audioHrefs.isEmpty()) {
              polishSentences.add(exampleSentenceTranslation);
            } else {
              polishSentences.add(exampleSentenceTranslation.concat(" ").concat(String.join("; ", audioHrefs)));
            }
          } else if (element1.hasClass("ref")) {
            for (Element element3 : element1.select("div.ref").first().children()) {
              if (element3.text().contains("synonim:")) {
                singleMeaning.set(singleMeaning.size() - 1, singleMeaning.get(singleMeaning.size() - 1).concat(" [").concat(element3.text()).concat("]"));
              } else if (element3.text().contains("przeciwieństwo:") || element3.text().contains("przeciwieństwa:")) {
                singleMeaning.set(singleMeaning.size() - 1, singleMeaning.get(singleMeaning.size() - 1).concat(" [").concat(element3.text()).concat("]"));
              }
            }
          } else if (element1.hasClass("grammarTag")) {
            singleMeaning.set(singleMeaning.size() - 1, singleMeaning.get(singleMeaning.size() - 1)
                                                                     .concat(" [grammarTag: ").concat(element1.text()
                                                                                                              .replaceFirst(Pattern.quote("["), "")
                                                                                                              .replaceFirst(Pattern.quote("]"), "")
                                                                                                              .trim())
                                                                     .concat("]"));
          } else if (element1.hasClass("meaningAdditionalInformation")) {
            if (StringUtils.isNoneEmpty(element1.text())) {
              singleMeaning.set(singleMeaning.size() - 1, singleMeaning.get(singleMeaning.size() - 1)
                                                                       .concat(" (").concat(element1.text().trim())
                                                                       .concat(")"));
            }
          }
        }
        meanings.add(String.join(", ", singleMeaning));
      }
      term.setPolishName(StringUtils.left(String.join("; ", meanings), 2000));
      term.setEnglishSentence(StringUtils.left(String.join("; ", englishSentences), 2000));
      term.setPolishSentence(StringUtils.left(String.join("; ", polishSentences), 2000));

    } else if (element.hasClass("af")) {
      String type = "";
      List<String> comparative = new ArrayList<>();
      List<String> superlative = new ArrayList<>();
      System.out.println();
      for (Element element1 : element.children()) {
        if (element1.hasClass("foreignTermHeader")) {
          if (element1.text().equals("stopień wyższy") || element1.text().equals("stopień wyższy (British English)") ||
              element1.text().equals("stopień wyższy (American English)") || element1.text().equals("stopień najwyższy")
              || element1.text().equals("stopień najwyższy (British English)") || element1.text().equals("stopień najwyższy (American English)")) {
            type = element1.text();
          }
        } else if (element1.hasClass("foreignTermText")) {
          if (type.equals("stopień wyższy")) {
            comparative.add(element1.text());
          } else if (type.equals("stopień wyższy (British English)")) {
            comparative.add(element1.text() + " (British English)");
          } else if (type.equals("stopień wyższy (American English)")) {
            comparative.add(element1.text() + " (American English)");
          } else if (type.equals("stopień najwyższy")) {
            superlative.add(element1.text());
          } else if (type.equals("stopień najwyższy (British English)")) {
            superlative.add(element1.text() + " (British English)");
          } else if (type.equals("stopień najwyższy (American English)")) {
            superlative.add(element1.text() + " (American English)");
          }
        }
      }
      term.setComparative(String.join("; ", comparative));
      term.setSuperlative(String.join("; ", superlative));
    } else if (element.hasClass("pf")) {
      String plural = element.select("span.foreignTermText").text();
      term.setPlural(plural);
    }
  }

  private void mapWordVersion(Term term, List<String> wordVersions) {
    for (int i = 0; i < wordVersions.size(); i++) {
      if (i == 0) {
        term.setEnglishName(wordVersions.get(i));
        continue;
      }
      if (wordVersions.get(i).contains("American English") && StringUtils.isEmpty(term.getAmericanName())) {
        term.setAmericanName(wordVersions.get(i));
      } else {
        term.setOtherName(StringUtils.trimToEmpty(term.getOtherName()) + wordVersions.get(i) + "; ");
        if (term.getOtherName() == null) {
          term.setOtherName("");
        }
      }
    }
  }

  private List<Term> saveTerms(List<Term> terms, String englishWord) {
    for (Term term : terms) {
      term.setCategory(englishWord);
      term.setIsAdded(false);
      term.setIsIgnored(false);
    }
    log.info("[Diki scrapper] save and return terms");
    return termService.saveAll(terms);
  }

  @Override
  public void run(String... args) throws Exception {
    // webScrap("good morning", true);
    // remove duplications
/*    final Map<String, List<Term>> collection = termService.getAll()
        .stream()
        .collect(Collectors.groupingBy(Term::getSource));

    collection.forEach((s, terms) -> {
      final Map<String, List<Term>> collect = terms.stream()
          .collect(Collectors.groupingBy(term -> StringUtils.trimToEmpty(term.getEnglishName())
              .concat("=")
              .concat(StringUtils.trimToEmpty(term.getPolishName()))));

      final Map<String, List<Term>> dupl = new HashMap<>();
      collect.forEach((s1, terms1) -> {
        if (terms1.size() > 1) {
          dupl.put(s1, terms1);
        }
      });

      dupl.forEach((s1, terms1) -> {
        final List<Term> toRemove = terms1.subList(1, terms1.size());
        toRemove.forEach(termService::delete);
      });

    });*/
  }
}
