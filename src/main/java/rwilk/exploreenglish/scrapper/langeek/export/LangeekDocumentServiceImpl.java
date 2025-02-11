package rwilk.exploreenglish.scrapper.langeek.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.*;
import rwilk.exploreenglish.repository.langeek.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Order(100)
@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekDocumentServiceImpl implements LangeekDocumentService, CommandLineRunner {

    private static final String FONT_CALIBRI = "Calibri";
    private static final int FONT_SIZE_VERY_BIG = 14;
    private static final int FONT_SIZE_BIG = 12;
    private static final int FONT_SIZE_MEDIUM = 10;
    //    private static final int FONT_SIZE_SMALL = 11;
    private static final double SPACING_BETWEEN = 1.5;

    private static final String COLOR_BLUE = "0065C1";
    private static final String COLOR_BLACK = "000000";
    private static final String COLOR_GREY = "757575";
    private static final String COLOR_TEAL = "03DAC5";
    private static final String COLOR_CYTAT = "0f4761";

    private final LangeekCourseRepository langeekCourseRepository;
    private final LangeekLessonRepository langeekLessonRepository;
    private final LangeekExerciseRepository langeekExerciseRepository;
    private final LangeekExerciseWordRepository langeekExerciseWordRepository;

    @Override
    public void run(String... args) throws Exception {
        // generateDocument();
    }

    @Override
    public void generateDocument() throws IOException {
        final List<LangeekCourse> courses = langeekCourseRepository.findAll()
                .stream()
                .filter(course -> course.getId() <= 7)
                .toList();

        for (final LangeekCourse course : courses) {
            final List<LangeekLesson> lessons = langeekLessonRepository.findAllByCourse_Id(course.getId())
                    .stream()
                    .toList();

            for (final LangeekLesson lesson : lessons) {
                final FileInputStream fis = new FileInputStream("template.docx");
                final XWPFDocument document = new XWPFDocument(fis);
                createFooter(document);

                createHeader(document, lesson.getName());

                final List<LangeekExercise> exercises = langeekExerciseRepository.findAllByLesson_Id(lesson.getId());

                for (final LangeekExercise exercise : exercises) {
                    final List<LangeekExerciseWord> langeekExerciseWords = langeekExerciseWordRepository.findAllByExercise_Id(exercise.getId());

                    final XWPFParagraph exerciseParagraph = document.createParagraph();
                    exerciseParagraph.setSpacingBetween(SPACING_BETWEEN);
                    exerciseParagraph.setStyle("Cytatintensywny");
                    createLessonDescriptionTextStyle(exerciseParagraph, exercise.getName());

                    for (final LangeekExerciseWord langeekExerciseWord : langeekExerciseWords) {
                        final LangeekWord word = langeekExerciseWord.getWord();
                        final List<LangeekDefinition> definitions = word.getDefinitions();

                        final List<LangeekDefinition> primaryDefinitions = definitions.stream()
                                .filter(it -> it.getType().equals("WORD"))
                                .toList();

                        final XWPFParagraph primaryParagraph = document.createParagraph();
                        primaryParagraph.setSpacingBetween(SPACING_BETWEEN);
                        primaryParagraph.setSpacingBefore(0);
                        primaryParagraph.setSpacingAfter(0);

                        for (final LangeekDefinition primaryDefinition : primaryDefinitions) {
                            if (primaryDefinitions.indexOf(primaryDefinition) > 0) {
                                createForeignWordTextStyle(primaryParagraph, " / ");
                            }
                            createForeignWordTextStyle(primaryParagraph, primaryDefinition.getForeignTranslation());
                        }
                        createNativeWordTextStyle(primaryParagraph, " = ");

                        createNativeWordTextStyle(primaryParagraph, word.getNativeTranslation());
                        if (StringUtils.isNoneBlank(word.getAdditionalInformation())) {
                            createAdditionalTextStyle(primaryParagraph, word.getAdditionalInformation());
                        }

                        final XWPFParagraph sentenceParagraph = document.createParagraph();
                        sentenceParagraph.setSpacingBetween(SPACING_BETWEEN);
                        sentenceParagraph.setSpacingBefore(0);
                        sentenceParagraph.setSpacingAfter(0);

                        final List<LangeekDefinition> primaryDefinitionsWithAdditionalInfo = primaryDefinitions.stream()
                                .filter(it -> StringUtils.isNotEmpty(it.getAdditionalInformation()))
                                .toList();

                        for (final LangeekDefinition primaryDefinition : primaryDefinitionsWithAdditionalInfo) {
                            if (StringUtils.isNoneBlank(primaryDefinition.getAdditionalInformation())) {
                                createAdditionalSentenceTextStyle(sentenceParagraph, primaryDefinition.getAdditionalInformation().trim());
                            }
                            if (primaryDefinitions.indexOf(primaryDefinition) < primaryDefinitionsWithAdditionalInfo.size() - 1) {
                                sentenceParagraph.createRun().addBreak();
                            }
                        }
                    }
                }
                saveToFile(document, lesson);
            }
        }
    }

    private void createHeader(final XWPFDocument document, final String headerText) {
        final XWPFHeader header = document.createHeader(HeaderFooterType.FIRST);

        final XWPFParagraph headerParagraph = header.createParagraph();
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);

        final XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText(headerText.toUpperCase(Locale.ROOT));
        headerRun.setBold(true);
        headerRun.setColor(COLOR_CYTAT);
        headerRun.setFontSize(36);
    }

    private void createFooter(final XWPFDocument document) {
        final XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

        final XWPFParagraph footerParagraph = footer.createParagraph();
        footerParagraph.setAlignment(ParagraphAlignment.CENTER);

        final String color = COLOR_CYTAT;

        XWPFRun footerRun = footerParagraph.createRun();
        footerRun.setText("Strona ");
        footerRun.setColor(color);

        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewInstrText().setStringValue(" PAGE ");
        footerRun.setColor(color);
        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);

        footerRun = footerParagraph.createRun();
        footerRun.setText(" z ");
        footerRun.setColor(color);

        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewInstrText().setStringValue(" NUMPAGES ");
        footerRun.setColor(color);
        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
    }

    private void createLessonDescriptionTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text + "\n", COLOR_CYTAT, true, FONT_SIZE_VERY_BIG);
    }

    private void createForeignWordTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text, COLOR_BLUE, true, FONT_SIZE_BIG);
    }

    private void createAdditionalTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, " (" + text.trim() + ") ", COLOR_GREY, false, FONT_SIZE_MEDIUM);
    }

    private void createNativeWordTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text, COLOR_BLACK, false, FONT_SIZE_BIG);
    }

    private void createSentenceTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text, COLOR_BLACK, true, FONT_SIZE_MEDIUM);
        paragraph.setIndentationLeft(360);
    }

    private void createAdditionalSentenceTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text.trim(), COLOR_GREY, false, FONT_SIZE_MEDIUM);
        paragraph.setIndentationLeft(360);
    }

    private void setWordTextBase(XWPFParagraph paragraph, String text, String color, boolean isBold, int fontSize) {
        final XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(fontSize);
        run.setFontFamily(FONT_CALIBRI);
        run.setColor(color);
    }

    private void saveToFile(XWPFDocument document, LangeekLesson langeekLesson) throws IOException {
        try (FileOutputStream out = new FileOutputStream("generated/" + langeekLesson.getName() + ".docx")) {
            document.write(out);
        }
    }
}
