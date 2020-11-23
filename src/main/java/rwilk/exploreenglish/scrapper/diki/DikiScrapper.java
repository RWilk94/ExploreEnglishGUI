package rwilk.exploreenglish.scrapper.diki;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DikiScrapper {

  public List<Term> webScrap(String englishWord) {
    log.info("[Diki scrapper] {}", englishWord);
    try {
      List<Term> results = new ArrayList<>();

      String url = "http://www.diki.pl/slownik-angielskiego?q=" + englishWord.
          trim()
          .replaceAll("British English", "")
          .trim()
          .replaceAll("American English", "")
          .trim()
          .replaceAll(" ", "+");

      Document document = Jsoup.connect(url).cookie("autoLoginToken", "7Gzy1dCKErpdyBhzvc14Xt6uBoZMFUso0LwclUan").userAgent("Mozilla").timeout(10000).get();
      Elements elements = document.select("div.diki-results-left-column").get(0).child(0)
          .select("div.dictionaryEntity"); // return elements containing translations

      for (Element dictionaryEntity : elements) {
        Term term = new Term();
        term.setSource("diki");
        for (Element element : dictionaryEntity.children()) {
          if (element.hasClass("hws")) {
            List<String> englishNames = new ArrayList<>();
            String englishName = "";
            for (Element element1 : element.select("div.hws").select("h1").get(0).children()) {
              if (element1.hasClass("hw")) {
                if (StringUtils.isNoneEmpty(englishName)) {
                  englishNames.add(englishName);
                  englishName = "";
                }
                englishName = element1.select("span.hw").text();
              } else if (element1.hasClass("dictionaryEntryHeaderAdditionalInformation")) {
                String englishVariety = element1.select("span.dictionaryEntryHeaderAdditionalInformation").text();
                if (StringUtils.isNoneEmpty(englishVariety) && StringUtils.isNoneEmpty(englishName)) {
                  englishName = englishName.concat(" (").concat(englishVariety).concat(")");
                  englishNames.add(englishName);
                  englishName = "";
                }
              }
            }
            if (StringUtils.isNoneEmpty(englishName)) {
              englishNames.add(englishName);
            }
            mapWordVersion(term, englishNames);
          } else if (element.hasClass("partOfSpeechSectionHeader")) {
            if (StringUtils.isNoneEmpty(term.getPartOfSpeech())) {
              results.add(term);
              term = Term.builder()
                  .englishName(term.getEnglishName())
                  .americanName(term.getAmericanName())
                  .otherName(term.getOtherName())
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
                  .otherName(term.getOtherName())
                  .comparative(term.getComparative())
                  .superlative(term.getSuperlative())
                  .pastTense(term.getPastTense())
                  .pastParticiple(term.getPastParticiple())
                  .plural(term.getPlural())
                  .partOfSpeech(term.getPartOfSpeech())
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
                  singleMeaning.add(meaning);
                } else if (element1.hasClass("exampleSentence")) {
                  String exampleSentence = element1.select("div.exampleSentence").text();
                  String exampleSentenceTranslation = element1.select("div.exampleSentence").select("span.exampleSentenceTranslation").text();
                  exampleSentence = exampleSentence.replace(exampleSentenceTranslation, "");
                  englishSentences.add(exampleSentence);
                  polishSentences.add(exampleSentenceTranslation);
                } else if (element1.hasClass("ref")) {
                  for (Element element3 : element1.select("div.ref").first().children()) {
                    if (element3.text().contains("synonim:")) {
                      singleMeaning.set(singleMeaning.size() - 1, singleMeaning.get(singleMeaning.size() - 1).concat(" [").concat(element3.text()).concat("]"));
                    } else if (element3.text().contains("przeciwieństwo:") || element3.text().contains("przeciwieństwa:")) {
                      singleMeaning.set(singleMeaning.size() - 1, singleMeaning.get(singleMeaning.size() - 1).concat(" [").concat(element3.text()).concat("]"));
                    }
                  }
                }
              }
              meanings.add(String.join(", ", singleMeaning));
            }
            term.setPolishName(String.join("; ", meanings));
            term.setEnglishSentence(String.join("; ", englishSentences));
            term.setPolishSentence(String.join("; ", polishSentences));

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
        results.add(term);
      }

      return results;
    } catch (Exception e) {
      log.error("[{}] ", englishWord, e);
      return Collections.emptyList();
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
        term.setOtherName(term.getOtherName() + wordVersions.get(i) + "; ");
        if (term.getOtherName() == null) {
          term.setOtherName("");
        }
      }
    }
  }

}
