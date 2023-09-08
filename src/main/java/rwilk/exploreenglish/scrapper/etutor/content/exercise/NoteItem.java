package rwilk.exploreenglish.scrapper.etutor.content.exercise;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;
import rwilk.exploreenglish.scrapper.etutor.type.NoteItemType;

@SuppressWarnings({"java:S1192", "java:S3776", "java:S131"})
public class NoteItem {

  private static final String BASE_URL = "https://www.etutor.pl";

  public static List<EtutorNoteItem> webScrap(final EtutorNote etutorNote, final String paragraph) {
    return new NoteItem().get(etutorNote, paragraph);
  }

  private NoteItem() {
  }

  private List<EtutorNoteItem> get(final EtutorNote etutorNote, final String paragraph) {
    final List<Pair<String, NoteItemType>> results = new ArrayList<>();
    parse(paragraph, results);

    return extractParsed(results, etutorNote);
  }

  private void parse(final String paragraph, final List<Pair<String, NoteItemType>> result) {
    if (!paragraph.contains("<")) {
      result.add(Pair.of(paragraph, NoteItemType.PLAIN_TEXT));
    } else {

      if (paragraph.startsWith("<span")) {
        if (paragraph.startsWith("<span class=\"lessonRecordedExample\">") || paragraph.startsWith("<span class=\"exampleSentenceTranslation\">")) {
          result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span>") + 7), NoteItemType.EXAMPLE));
          parse(paragraph.substring(paragraph.indexOf("</span>") + 7), result);

        } else if (paragraph.startsWith("<span class=\"recordingsAndTranscriptions\">")) {

          if (paragraph.contains(" / ") && paragraph.contains("= ") && paragraph.indexOf(" / ") < paragraph.indexOf("= ")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf(" / ")), NoteItemType.SOUND));
            parse(paragraph.substring(paragraph.indexOf(" / ")), result);
          } else if (paragraph.contains("</span></span></span>")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span></span></span>") + 21), NoteItemType.SOUND));
            parse(paragraph.substring(paragraph.indexOf("</span></span></span>") + 21), result);
          } else if (paragraph.contains("= ")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("= ")), NoteItemType.SOUND));
            parse(paragraph.substring(paragraph.indexOf("= ")), result);
          }

        } else if (paragraph.startsWith("<span class=\"phoneticTranscription\">")) {
          if (paragraph.substring(5).indexOf("<span") > paragraph.indexOf("</span>")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span>") + 7), NoteItemType.PHONETIC_TRANSCRIPTIONS));
            parse(paragraph.substring(paragraph.indexOf("</span>") + 7), result);
          } else if (paragraph.contains("</span></span></span></span>")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span></span></span></span>") + 28), NoteItemType.PHONETIC_TRANSCRIPTIONS));
            parse(paragraph.substring(paragraph.indexOf("</span></span></span></span>") + 28), result);
          } else if (paragraph.contains("</span></span></span>")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span></span></span>") + 21), NoteItemType.PHONETIC_TRANSCRIPTIONS));
            parse(paragraph.substring(paragraph.indexOf("</span></span></span>") + 21), result);
          } else if (paragraph.contains("</span></span>")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span></span>") + 14), NoteItemType.PHONETIC_TRANSCRIPTIONS));
            parse(paragraph.substring(paragraph.indexOf("</span></span>") + 14), result);
          } else if (paragraph.contains("</span>")) {
            result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</span>") + 7), NoteItemType.PHONETIC_TRANSCRIPTIONS));
            parse(paragraph.substring(paragraph.indexOf("</span>") + 7), result);
          } else {
            throw new UnsupportedOperationException(paragraph);
          }

        } else if (paragraph.startsWith("<span class=\"hw\">")) {
          parse(paragraph.substring(17), result);

        } else if (paragraph.startsWith("<span style=\"color:null;\">")) {
          parse(paragraph.substring(26), result);

        } else if (paragraph.startsWith("<span style=\"color:#000000;\">")
                   || paragraph.startsWith("<span style=\"color:#7f8c8d;\">")
                   || paragraph.startsWith("<span style=\"color:#009900;\">")
                   || paragraph.startsWith("<span style=\"color:#2980b9;\">")
                   || paragraph.startsWith("<span style=\"color:#c0392b;\">")
        ) {
          parse(paragraph.substring(29), result);

        } else if (paragraph.startsWith("<span id=\"docs-internal-guid")
                   || paragraph.startsWith("<span style=\"font-size")
                   || paragraph.startsWith("<span class=\"dictionaryEntryHeaderAdditionalInformation\">")
                   || paragraph.startsWith("<span style=\"text-align: justify;\">")
                   || paragraph.startsWith("<span class=\"lessonRecordedExample")
                   || paragraph.startsWith("<span style=\"font-family")
                   || paragraph.startsWith("<span style=\"font-style")
        ) {
          parse(paragraph.substring(paragraph.indexOf(">") + 1)
                  .replaceFirst("</span>", ""),
                result);

        } else if (paragraph.startsWith("<span style=\"line-height: 1.5em")
                   || paragraph.startsWith("<span class=\"tL8wMe EMoHub\"")) {
          String newParagraph = paragraph.substring(paragraph.indexOf(">") + 1);
          newParagraph = newParagraph.substring(0, newParagraph.lastIndexOf("</span>"));

          parse(newParagraph, result);

        } else if (paragraph.startsWith("<span>")) {
          parse(paragraph.substring(6).replaceFirst("</span>", ""), result);

        } else {
          throw new UnsupportedOperationException(paragraph);
        }
      } else if (paragraph.startsWith("&nbsp;")) {
        parse(paragraph.substring(6), result);

      } else if (paragraph.startsWith(" ")) {
        parse(paragraph.substring(1), result);

      } else if (paragraph.startsWith("<br>")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf(">") + 1), NoteItemType.SPACE));
        parse(paragraph.substring(paragraph.indexOf(">") + 1), result);

      } else if (paragraph.startsWith("<strong><span ")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1).replaceFirst("</strong>", ""), result);

      } else if (paragraph.startsWith("<strong>")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</strong>") + 9), NoteItemType.STRONG));
        parse(paragraph.substring(paragraph.indexOf("</strong>") + 9), result);

      } else if (paragraph.startsWith("<meta")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1), result);

      } else if (paragraph.startsWith("<s>")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</s>") + 4), NoteItemType.STRIKETHROUGH));
        parse(paragraph.substring(paragraph.indexOf("</s>") + 4), result);

      } else if (paragraph.startsWith("<strike>")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</strike>") + 9), NoteItemType.STRONG));
        parse(paragraph.substring(paragraph.indexOf("</strike>") + 9), result);

      } else if (paragraph.startsWith("<img")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf(">") + 1), NoteItemType.IMAGE));
        parse(paragraph.substring(paragraph.indexOf(">") + 1), result);

      } else if (paragraph.startsWith("<u>")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</u>") + 4), NoteItemType.UNDERLINE));
        parse(paragraph.substring(paragraph.indexOf("</u>") + 4), result);

      } else if (paragraph.startsWith("<i>")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</i>") + 4), NoteItemType.ITALIC));
        parse(paragraph.substring(paragraph.indexOf("</i>") + 4), result);

      } else if (paragraph.startsWith("</span>")) {
        // end of "<span class=\"hw\">"
        // or <span style="color:null;">
        // or <span style="color:#000000;">
        parse(paragraph.substring(7), result);

      } else if (paragraph.startsWith("<em>")) {
        parse(paragraph.substring(4), result);

      } else if (paragraph.startsWith("</em>")) {
        parse(paragraph.substring(5), result);

      } else if (paragraph.startsWith("<em ")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1)
                .replaceFirst("</em>", ""), result);

      } else if (paragraph.startsWith("<p")) {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("</p>") + 4), NoteItemType.PARAGRAPH));
        parse(paragraph.substring(paragraph.indexOf("</p>") + 4), result);

      } else if (paragraph.startsWith("<div>")
                 || paragraph.startsWith("<div style=")
      ) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1)
                .replaceFirst("</div>", ""),
              result);

      } else if (paragraph.startsWith("<b")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1)
                .replaceFirst("</b>", ""),
              result);

      } else if (paragraph.startsWith("<style type=\"text/css\">")) {
        parse(paragraph.substring(paragraph.indexOf("</style>") + 8), result);

      } else if (paragraph.startsWith("<ul>")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1)
                .replaceFirst("</ul>", ""),
              result);

      } else if (paragraph.startsWith("<li style=\"text-align: justify;\">")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1)
                .replaceFirst("</li>", ""),
              result);

      } else if (paragraph.startsWith("<font color=")) {
        parse(paragraph.substring(paragraph.indexOf("</font>") + 7), result);

      } else if (paragraph.startsWith("</font>")) {
        parse(paragraph.substring(7), result);

      } else if (paragraph.startsWith("<sup>")) {
        parse(paragraph.substring(5), result);

      } else if (paragraph.startsWith("</sup>")) {
        parse(paragraph.substring(6), result);

      } else if (paragraph.startsWith("<a")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1).replace("</a>", ""), result);

      } else if (paragraph.startsWith("<u style=\"text-align: justify;\">")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1).replace("</u>", ""), result);

      } else if (paragraph.startsWith("<li>")) {
        parse(paragraph.substring(4).replace("</li>", ""), result);

      } else if (paragraph.startsWith("<u ")) {
        parse(paragraph.substring(paragraph.indexOf(">") + 1)
                .replaceFirst("</u>", ""), result);

      } else {
        if (paragraph.indexOf("<") == 0) {
          throw new UnsupportedOperationException(paragraph);
        }
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("<")), NoteItemType.PLAIN_TEXT));
        parse(paragraph.substring(paragraph.indexOf("<")), result);
      }
    }
  }

  private List<EtutorNoteItem> extractParsed(final List<Pair<String, NoteItemType>> elements,
                                             final EtutorNote etutorNote) {
    final List<EtutorNoteItem> noteExamples = new ArrayList<>();
    EtutorNoteItem noteExample = new EtutorNoteItem();
    noteExample.setNote(etutorNote);

    for (final Pair<String, NoteItemType> element : elements) {
      switch (element.getRight()) {
        case PLAIN_TEXT -> {
          noteExample.setPlainText(element.getLeft());

          noteExamples.add(noteExample);
          noteExample = new EtutorNoteItem();
          noteExample.setNote(etutorNote);
        }
        case EXAMPLE -> {
          if (StringUtils.isNotEmpty(noteExample.getExample())) {
            noteExamples.add(noteExample);
            noteExample = new EtutorNoteItem();
            noteExample.setNote(etutorNote);
          }
          noteExample.setExample(extractExampleTest(element.getLeft()));
        }
        case SOUND -> {
          noteExample.setBritishSound(extractBritishSound(element.getLeft()));
          noteExample.setAmericanSound(extractAmericanSound(element.getLeft()));
        }
        case SPACE, NEW_LINE -> {
          // do nothing
        }
        case PHONETIC_TRANSCRIPTIONS, UNDERLINE, PARAGRAPH, ITALIC -> {
          noteExample.setPlainText(extractPhoneticTranscription(element.getLeft()));
          noteExample.setBritishSound(extractBritishSound(element.getLeft()));
          noteExample.setAmericanSound(extractAmericanSound(element.getLeft()));

          noteExamples.add(noteExample);
          noteExample = new EtutorNoteItem();
          noteExample.setNote(etutorNote);
        }
        case STRONG, STRIKETHROUGH -> {
          noteExample.setPlainText(extractStrong(element.getLeft()));

          noteExamples.add(noteExample);
          noteExample = new EtutorNoteItem();
          noteExample.setNote(etutorNote);
        }
        case IMAGE -> {
          noteExample.setImage(extractImage(element.getLeft()));

          noteExamples.add(noteExample);
          noteExample = new EtutorNoteItem();
          noteExample.setNote(etutorNote);
        }
        default -> throw new UnsupportedOperationException(element.getRight().toString());
      }
    }
    if (!StringUtils.isAllEmpty(noteExample.getPlainText(), noteExample.getExample())) {
      noteExamples.add(noteExample);
    }
    noteExamples.add(EtutorNoteItem.builder().plainText("\n").note(etutorNote).build());

    return noteExamples;
  }

  private String extractExampleTest(final String element) {
    return element.substring(element.indexOf(">") + 1, element.indexOf("</")).replace("&nbsp;", " ");
  }

  private String extractBritishSound(final String element) {
    if (element.contains("/en/")) {
      final String subText = element.substring(element.indexOf("/en/"));
      return BASE_URL + "/images-common" + subText.substring(0, subText.indexOf(".mp3") + 4);
    }
    return null;
  }

  private String extractAmericanSound(final String element) {
    if (element.contains("/en-ame/")) {
      final String subText = element.substring(element.indexOf("/en-ame/"));
      return BASE_URL + "/images-common" + subText.substring(0, subText.indexOf(".mp3") + 4);
    }
    return null;
  }

  private String extractPhoneticTranscription(final String input) {
    final String element = input.substring(1);

    if (element.contains(">") && element.contains("<")) {
      final int startIndex = element.indexOf(">");
      final int endIndex = element.indexOf("<");

      return element.substring(startIndex + 1, endIndex)
               .replace("&nbsp;", " ") + extractPhoneticTranscription(element.substring(endIndex));
    }
    return "";
  }

  private String extractStrong(final String input) {
    final String element = input.substring(1);
    final int startIndex = element.indexOf(">");
    final int endIndex = element.indexOf("<");

    return element.substring(startIndex + 1, endIndex);
  }

  private String extractImage(final String element) {
    return BASE_URL + element.substring(element.indexOf("/"), element.indexOf(".jpg") + 4);
  }

}
