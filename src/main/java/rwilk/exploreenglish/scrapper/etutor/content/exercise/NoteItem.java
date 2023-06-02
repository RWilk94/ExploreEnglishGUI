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
        if (paragraph.startsWith("<span class=\"lessonRecordedExample\">")) {
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

      } else {
        result.add(Pair.of(paragraph.substring(0, paragraph.indexOf("<")), NoteItemType.PLAIN_TEXT));
        parse(paragraph.substring(paragraph.indexOf("<")), result);
      }
    }
  }

  private static List<EtutorNoteItem> extractParsed(final List<Pair<String, NoteItemType>> elements,
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
        case EXAMPLE -> noteExample.setExample(extractExampleTest(element.getLeft()));
        case SOUND -> {
          noteExample.setBritishSound(extractBritishSound(element.getLeft()));
          noteExample.setAmericanSound(extractAmericanSound(element.getLeft()));
        }
        case SPACE, NEW_LINE -> {
          // do nothing
        }
      }
    }
    if (!StringUtils.isAllEmpty(noteExample.getPlainText(), noteExample.getExample())) {
      noteExamples.add(noteExample);
    }
    noteExamples.add(EtutorNoteItem.builder().plainText("\n").note(etutorNote).build());

    return noteExamples;
  }

  private static String extractExampleTest(final String element) {
    return element.substring(element.indexOf(">") + 1, element.indexOf("</"));
  }

  private static String extractBritishSound(final String element) {
    if (element.contains("/en/")) {
      final String subText = element.substring(element.indexOf("/en/"));
      return BASE_URL + "/images-common" + subText.substring(0, subText.indexOf(".mp3") + 4);
    }
    return null;
  }

  private static String extractAmericanSound(final String element) {
    if (element.contains("/en-ame/")) {
      final String subText = element.substring(element.indexOf("/en-ame/"));
      return BASE_URL + "/images-common" + subText.substring(0, subText.indexOf(".mp3") + 4);
    }
    return null;
  }

}
