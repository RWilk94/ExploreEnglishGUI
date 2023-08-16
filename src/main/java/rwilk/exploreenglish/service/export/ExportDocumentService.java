package rwilk.exploreenglish.service.export;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.xmlbeans.XmlException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import rwilk.exploreenglish.model.WordTypeEnum;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Definition;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.scrapper.longman.LongmanScrapper;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.DefinitionService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.WordService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportDocumentService implements CommandLineRunner {

  private static final String COLOR_BLUE = "4472C4";
  private final CourseService courseService;
  private final LessonService lessonService;
  private final LessonWordService lessonWordService;
  private final WordService wordService;
  private final LongmanScrapper longmanScrapper;
  private final DefinitionService definitionService;

  @Override
  public void run(final String... args) throws Exception {
    // generate();

/*    final List<Word> words = wordService.getAll().stream()
                                        .filter(word -> word.getIsReady() == 0)
                                        .toList();
    log.info("Scrapping word definition sounds... Found {} words", words.size());
    for (final Word word : words) {
      final List<Definition> definitions = word.getDefinitions().stream()
                                               .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
                                               .filter(definition -> definition.getBritishSound().contains("[")
                                                                     && definition.getBritishSound().contains("]")
                                                                     && definition.getBritishSound().contains("="))
                                               .toList();

      log.info("Scrapping word {} definition sounds... Found {} definitions", word, definitions.size());

      for (final Definition definition : definitions) {
        final List<Term> terms = longmanScrapper.webScrap(definition.getEnglishName(), true);

        final List<String> sounds = new ArrayList<>();
        sounds.addAll(terms.stream()
                           .map(Term::getEnglishName)
                           .filter(text -> text.contains("www.ldoceonline.com") && text.contains(".mp3"))
                           .toList());
        sounds.addAll(terms.stream()
                           .map(Term::getAmericanName)
                           .filter(text -> text.contains("www.ldoceonline.com") && text.contains(".mp3"))
                           .toList());
        sounds.addAll(terms.stream()
                           .map(Term::getOtherName)
                           .filter(text -> text.contains("www.ldoceonline.com") && text.contains(".mp3"))
                           .toList());

        if (CollectionUtils.isNotEmpty(sounds)) {
          definition.setBritishSound(definition.getBritishSound()
                                               .concat(String.join("; ", sounds)));
          log.info("Scrapping word {} definition sounds... Save new {} sounds", word, sounds.size());
          definitionService.save(definition);
        }
      }
      word.setIsReady(1);
      wordService.save(word);
    }*/

  }

  public void generate() {
    log.info("START GENERATING .docx document");
    final Course course = courseService.getById(1L).orElseThrow(() -> new IllegalArgumentException(""));
    final List<Lesson> lessons = lessonService.getAllByCourse(course);

    try (XWPFDocument template = new XWPFDocument(new FileInputStream("template.docx"));
         FileOutputStream out = new FileOutputStream("Output.docx");
         XWPFDocument document = new XWPFDocument()) {

      final XWPFStyles styles = document.createStyles();
      styles.setStyles(template.getStyle());

      lessons.forEach(lesson -> {
        createHeaderParagraph(document, lesson);
        final List<LessonWord> lessonWords = lessonWordService.getAllByLesson(lesson);

        lessonWords.forEach(lessonWord -> appendEnglishWord(document, lessonWord));
        // lessonWords.forEach(lessonWord -> appendSentences(document, lessonWord.getWord()));
      });

      document.write(out);
      log.info("FINISH GENERATING .docx document");
    } catch (XmlException | IOException e) {
      log.error("An exception occurred during generating a .docx document", e);
    }
  }

  private void createHeaderParagraph(final XWPFDocument document, final Lesson lesson) {
    final XWPFParagraph headerParagraph = document.createParagraph();
    headerParagraph.setStyle("Cytatintensywny");
    final XWPFRun run = headerParagraph.createRun();
    run.setText(lesson.getEnglishName());
  }

  private void appendEnglishWord(final XWPFDocument document, final LessonWord lessonWord) {
    final Word word = lessonWord.getWord();

    final XWPFParagraph paragraph = document.createParagraph();

    appendArticle(paragraph, word.getArticle());
    appendEnglishWord(paragraph,
                      String.join(" / ",
                                  ListUtils.emptyIfNull(word.getDefinitions()
                                                          .stream()
                                                          .filter(wordSound -> wordSound.getType()
                                                            .equals(WordTypeEnum.WORD.toString()))
                                                          .map(Definition::getEnglishName)
                                                          .toList())));
    appendEqualSign(paragraph);
    appendPolishWord(paragraph, word.getPolishName());
    appendPartOfSpeech(paragraph, word.getPartOfSpeech());
    appendGrammarTag(paragraph, word.getGrammarType());
    appendAdditional(paragraph, word, WordTypeEnum.PLURAL);
    appendAdditional(paragraph, word, WordTypeEnum.PAST_TENSE);
    appendAdditional(paragraph, word, WordTypeEnum.PAST_PARTICIPLE);
    appendAdditional(paragraph, word, WordTypeEnum.PRESENT_PARTICIPLE);
    appendAdditional(paragraph, word, WordTypeEnum.COMPARATIVE);
    appendAdditional(paragraph, word, WordTypeEnum.SUPERLATIVE);
    appendAdditional(paragraph, word, WordTypeEnum.SYNONYM);
    appendAdditional(paragraph, word, WordTypeEnum.OPPOSITE);
    appendSentences(document, word);
  }

  private void appendArticle(final XWPFParagraph paragraph, final String text) {
    if (StringUtils.isNoneBlank(text)) {
      final XWPFRun run = paragraph.createRun();
      run.setBold(true);
      run.setColor(COLOR_BLUE);
      run.setText("[" + text + "] ");
    }
  }

  private void appendEnglishWord(final XWPFParagraph paragraph, final String text) {
    if (StringUtils.isNoneBlank(text)) {
      final XWPFRun run = paragraph.createRun();
      run.setBold(true);
      run.setColor(COLOR_BLUE);
      run.setText(text);
    }
  }

  private void appendEqualSign(final XWPFParagraph paragraph) {
    final XWPFRun run = paragraph.createRun();
    run.setText(" = ");
  }

  private void appendPolishWord(final XWPFParagraph paragraph, final String text) {
    if (StringUtils.isNoneBlank(text)) {
      final XWPFRun run = paragraph.createRun();
      run.setBold(true);
      run.setText(text + " ");
    }
  }

  private void appendPartOfSpeech(final XWPFParagraph paragraph, final String text) {
    if (StringUtils.isNoneBlank(text)) {
      final XWPFRun run = paragraph.createRun();
      run.setText("[" + text + "] ");
    }
  }

  private void appendGrammarTag(final XWPFParagraph paragraph, final String text) {
    if (StringUtils.isNoneBlank(text)) {
      final XWPFRun run = paragraph.createRun();
      run.setText("[" + text + "] ");
    }
  }

  private void appendAdditional(final XWPFParagraph paragraph, final Word word, final WordTypeEnum wordType) {
    final List<Definition> definitions = word.getDefinitions()
      .stream()
      .filter(wordSound -> wordSound.getType().equals(wordType.toString()))
      .toList();
    if (CollectionUtils.isNotEmpty(definitions)) {
      final XWPFRun run = paragraph.createRun();
      final StringBuilder sb = new StringBuilder();
      sb.append("[");
      definitions.forEach(wordSound -> {
        if (sb.length() > 1) {
          sb.append("; ");
        }
        sb.append(wordType.toString().replace("_", " ").toLowerCase())
          .append(": ")
          .append(wordSound.getEnglishName());
        if (StringUtils.isNoneBlank(wordSound.getAdditionalInformation())) {
          sb.append(" (")
            .append(wordSound.getAdditionalInformation())
            .append(")");
        }
      });
      sb.append("] ");
      run.setText(sb.toString());
    }
  }

  private void appendSentences(final XWPFDocument document, final Word word) {
    final List<Definition> definitions = word.getDefinitions()
      .stream()
      .filter(wordSound -> wordSound.getType().equals(WordTypeEnum.SENTENCE.toString()))
      .toList();
    if (CollectionUtils.isNotEmpty(definitions)) {
      definitions.forEach(wordSound -> {
        final XWPFParagraph paragraph = document.createParagraph();
        paragraph.setIndentFromLeft(707);
        final XWPFRun englishSentence = paragraph.createRun();
        englishSentence.setItalic(true);
        englishSentence.setText(wordSound.getEnglishName());

        if (StringUtils.isNoneBlank(wordSound.getAdditionalInformation())) {
          final XWPFRun polishSentence = paragraph.createRun();
          polishSentence.setItalic(true);
          polishSentence.setText(" = " + wordSound.getAdditionalInformation());
        }
      });
    }
  }

}
