package rwilk.exploreenglish.service.export;


import com.spire.doc.Break;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.BreakType;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.ParagraphStyle;
import com.spire.doc.fields.TextRange;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class DocService implements CommandLineRunner {

  private static final String FONT_CALIBRI = "Calibri";
  private static final String SPACE = " ";
  private static final String SEPARATOR_COLON = "także:";
  private static final String SEPARATOR_OR = "lub:";
  private static final String EQUALS_SIGN = " = ";
  private final CourseService courseService;
  private final LessonService lessonService;
  private final LessonWordService lessonWordService;

  public void generateDocument() {

    final Optional<Course> courseOptional = courseService.getById(1L);
    courseOptional.ifPresent(course -> {
      final Document document = createDocument();
      final Section section = createSection(document);

      final ParagraphStyle paragraphStyle = new ParagraphStyle(document);
      paragraphStyle.setName("Doc Header");
      paragraphStyle.getCharacterFormat().setTextColor(Color.WHITE);
      paragraphStyle.getCharacterFormat().setTextBackgroundColor(new Color(68, 114, 196));
      paragraphStyle.getCharacterFormat().setBold(true);
      paragraphStyle.getCharacterFormat().setFontName(FONT_CALIBRI);
      paragraphStyle.getCharacterFormat().setFontSize(14.0f);
      document.getStyles().add(paragraphStyle);

      AtomicInteger index = new AtomicInteger(1);

      final List<Lesson> lessons = lessonService.getAllByCourse(course).subList(0, 15);
      lessons.forEach(lesson -> {
        header(createParagraph(section), String.valueOf(index.getAndIncrement()).concat(". ").concat(lesson.getPolishName()));

        final Paragraph paragraph = createParagraph(section);

        final List<LessonWord> lessonWords = lessonWordService.getAllByLesson(lesson);
        lessonWords.forEach(lessonWord -> {

          final Word word = lessonWord.getWord();
          final String engName = getActiveWordVersion(word);
          final List<String> otherNames = getOtherNames(word);

          setEnglishWordText(paragraph, engName);
          if (!otherNames.isEmpty()) {
            for (String otherName : otherNames) {
              if (otherNames.indexOf(otherName) == 0) {
                setEnglishWordSeparator(paragraph, SEPARATOR_OR);
              } else {
                setEnglishWordSeparator(paragraph, SEPARATOR_COLON);
              }
              setEnglishWordText(paragraph, otherName);
            }
          }
          setPolishWordText(paragraph, EQUALS_SIGN);
          setPolishWordText(paragraph, word.getPolishName());
          paragraph.appendText("\n");

        });
        final Paragraph spaceParagraph = section.addParagraph();
        spaceParagraph.appendText("\n");
        spaceParagraph.appendText("\n");

      });

      saveToFile(document);
    });

  }

  private Document createDocument() {
    return new Document();
  }

  private void saveToFile(final Document document) {
    document.saveToFile("Output.docx", FileFormat.Docx);
  }

  private Section createSection(final Document document) {
    return document.addSection();
  }

  private Paragraph createParagraph(final Section section) {
    final Paragraph paragraph = section.addParagraph();
    paragraph.getFormat().setLineSpacing(18.0f);
    paragraph.getFormat().setBackColor(new Color(242, 242, 242));
    return paragraph;
  }

  private static TextRange header(final Paragraph paragraph, final String text) {
    paragraph.getFormat().setBackColor(new Color(68, 114, 196));
    paragraph.getFormat().setLineSpacing(18.0f);

    final TextRange textRange = paragraph.appendText(text);
    paragraph.applyStyle("Doc Header");
    return textRange;
  }

  private TextRange setEnglishWordText(final Paragraph paragraph, final String text) {
    final TextRange textRange = paragraph.appendText(text);
    textRange.getCharacterFormat().setTextColor(new Color(68, 114, 196));
    textRange.getCharacterFormat().setBold(true);
    textRange.getCharacterFormat().setFontName(FONT_CALIBRI);
    textRange.getCharacterFormat().setFontSize(11.0f);
    if (!textRange.getText().endsWith(" ")) {
      textRange.setText(textRange.getText().concat(" "));
    }
    return textRange;
  }

  private TextRange setEnglishWordSeparator(final Paragraph paragraph, final String separator) {
    final TextRange textRange = paragraph.appendText(separator);
    textRange.getCharacterFormat().setTextColor(new Color(128, 128, 128));
    textRange.getCharacterFormat().setBold(false);
    textRange.getCharacterFormat().setFontName(FONT_CALIBRI);
    textRange.getCharacterFormat().setFontSize(8.0f);
    if (!textRange.getText().endsWith(" ")) {
      textRange.setText(textRange.getText().concat(" "));
    }
    return textRange;
  }

  private TextRange setPolishWordText(final Paragraph paragraph, final String text) {
    final TextRange textRange = paragraph.appendText(text);
    textRange.getCharacterFormat().setTextColor(Color.BLACK);
    textRange.getCharacterFormat().setBold(true);
    textRange.getCharacterFormat().setFontName(FONT_CALIBRI);
    textRange.getCharacterFormat().setFontSize(11.0f);
    return textRange;
  }

  private void pageBreak(final Document document, final Paragraph paragraph) {
    Break pageBreak = new Break(document, BreakType.Page_Break);
    paragraph.getChildObjects().insert(
            paragraph.getChildObjects().indexOf(paragraph.getChildObjects().getLastItem()) + 1, pageBreak);
  }

  public List<String> getOtherNames(Word word) {
    String activeWordVersion = getActiveWordVersion(word);
    List<String> otherNames = new ArrayList<>();
    for (String enName : word.englishNamesAsString().split(";")) {
      if (!StringUtils.trim(enName).equals(activeWordVersion)) {
        otherNames.add(StringUtils.trim(enName));
      }
    }
    return otherNames;
  }

  public String getActiveWordVersion(Word word) {
    final String englishVariety = "British English";
    final String britishEnglishTag = "(" + "British English".toLowerCase() + ")";
    final String americanEnglishTag = "(" + "American English".toLowerCase() + ")";

    for (String enName : word.englishNamesAsString().split(";")) {
      if ("British English".equalsIgnoreCase(englishVariety)) {
        if (enName.toLowerCase().contains(britishEnglishTag) ||
                !enName.toLowerCase().contains(americanEnglishTag)) {
          return StringUtils.trim(enName);
        }
      } else if ("American English".equalsIgnoreCase(englishVariety)) {
        if (enName.toLowerCase().contains(americanEnglishTag) ||
                !enName.toLowerCase().contains(britishEnglishTag)) {
          return StringUtils.trim(enName);
        }
      }
    }
    return word.englishNamesAsString().split(";")[0];
  }

  @Override
  public void run(final String... args) throws Exception {
    // generateDocument();
  }
}
