package rwilk.exploreenglish.scrapper.langeek.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.*;
import rwilk.exploreenglish.repository.langeek.LangeekCourseRepository;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseRepository;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseWordRepository;
import rwilk.exploreenglish.repository.langeek.LangeekLessonRepository;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Order(100)
@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekDocumentServiceImpl implements LangeekDocumentService, CommandLineRunner {

    private static final String FONT_CALIBRI = "Calibri";
    private static final String FONT_APTOS = "Aptos";
    private static final String FONT_KRISTEN_ITC = "Kristen ITC";
    private static final int FONT_SIZE_VERY_BIG = 14;
    private static final int FONT_SIZE_BIG = 11;
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
                // .filter(course -> course.getId() <= 1)
                .toList();

        for (final LangeekCourse course : courses) {
            final List<LangeekLesson> lessons = langeekLessonRepository.findAllByCourse_Id(course.getId())
                    .stream()
                    .toList();

            for (final LangeekLesson lesson : lessons) {
                log.info("Generating document for lesson: {}", lesson.getName());
                final FileInputStream fis = new FileInputStream("template2.docx");
                final XWPFDocument document = new XWPFDocument(fis);
                // createFirstPageFooter(document, lesson);

                document.getParagraphs().forEach(paragraph -> {
                    printContentsOfTextBox(paragraph, lesson.getName());
                });

                createFooter(document);
                // createSecurityHeader(document);
                // createHeader(document, lesson.getName());

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
                            if ("british".equals(primaryDefinition.getAdditionalInformation())) {
                                createAdditionalLanguageVariantTextStyle(primaryParagraph, primaryDefinition.getAdditionalInformation().trim());
                            }
                        }
                        createNativeWordTextStyle(primaryParagraph, " = ");

                        createNativeWordTextStyle(primaryParagraph, word.getNativeTranslation());
                        if (StringUtils.isNoneBlank(word.getAdditionalInformation())) {
                            createAdditionalTextStyle(primaryParagraph, word.getAdditionalInformation());
                        }

                        if (StringUtils.isNotEmpty(word.getPartOfSpeech())) {
                            createForeignWordTextStyle(primaryParagraph, " ");
                            createPartOfSpeechTextStyle(primaryParagraph, word.getPartOfSpeech());
                        }

//                        for (final LangeekDefinition secondaryDefinition : secondaryDefinitions) {
//                            createNativeWordTextStyle(primaryParagraph, "(" + secondaryDefinition.getType().toLowerCase() + ": ");
//                            createForeignWordTextStyle(primaryParagraph, secondaryDefinition.getForeignTranslation());
//                            createNativeWordTextStyle(primaryParagraph, ")");
//                        }

//                        XWPFParagraph sentenceParagraph = null; // = document.createParagraph();
//                        sentenceParagraph.setSpacingBetween(SPACING_BETWEEN);
//                        sentenceParagraph.setSpacingBefore(0);
//                        sentenceParagraph.setSpacingAfter(0);

                        final List<String> order = List.of("PAST_TENSE", "PAST_PARTICIPLE", "PRESENT_PARTICIPLE");
                        final Map<String, List<LangeekDefinition>> secondaryDefinitionsMap = definitions.stream()
                                .filter(it -> List.of("PAST_TENSE", "PAST_PARTICIPLE", "PRESENT_PARTICIPLE"/*, "SYNONYM", "OPPOSITE"*/).contains(it.getType()))
                                .collect(groupingBy(LangeekDefinition::getType, LinkedHashMap::new, toList()));
                        final Map<String, List<LangeekDefinition>> sortedMap = secondaryDefinitionsMap.entrySet().stream()
                                .sorted(Comparator.comparingInt(e -> order.indexOf(e.getKey())))
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new
                                ));

                        List<LangeekDefinition> sentences = definitions.stream()
                                .filter(it -> it.getType().equals("SENTENCE"))
                                .limit(2)
                                .toList();
                        if (sentences.isEmpty() && sortedMap.isEmpty()) {

                        } else {
                            final XWPFParagraph sentenceParagraph = document.createParagraph();
                            sentenceParagraph.setSpacingBetween(SPACING_BETWEEN);
                            sentenceParagraph.setSpacingBefore(0);
                            sentenceParagraph.setSpacingAfter(0);


                        AtomicInteger index = new AtomicInteger(0);
                        sortedMap.forEach((s, langeekDefinitions) -> {
                            String appendSpace = "";
                            if (index.get() > 0) {
                                appendSpace = " ";
                            }
                            switch (s) {
                                case "PAST_TENSE":
                                    createSentenceTextStyle(sentenceParagraph, appendSpace + "past tense: ");
                                    break;
                                case "PAST_PARTICIPLE":
                                    createSentenceTextStyle(sentenceParagraph, appendSpace + "past participle: ");
                                    break;
                                case "PRESENT_PARTICIPLE":
                                    createSentenceTextStyle(sentenceParagraph, appendSpace + "present participle: ");
                                    break;
                                default:
                                    createSentenceTextStyle(sentenceParagraph, appendSpace + s + ": ");
                            }

                            langeekDefinitions.forEach(secondaryDefinition -> {
                                createForeignWordTextStyle(sentenceParagraph, secondaryDefinition.getForeignTranslation());
                                if (langeekDefinitions.indexOf(secondaryDefinition) < langeekDefinitions.size() - 1) {
                                    createSentenceTextStyle(sentenceParagraph, ", ");
                                }
                            });
                            index.getAndIncrement();
                        });

//                        final List<LangeekDefinition> primaryDefinitionsWithAdditionalInfo = primaryDefinitions.stream()
//                                .filter(it -> StringUtils.isNotEmpty(it.getAdditionalInformation()))
//                                .filter(it -> !it.getAdditionalInformation().equals("british"))
//                                .toList();

//                        if (!secondaryDefinitionsMap.isEmpty() && !primaryDefinitionsWithAdditionalInfo.isEmpty()) {
//                            sentenceParagraph.createRun().addBreak();
//                        }

//                        for (final LangeekDefinition primaryDefinition : primaryDefinitionsWithAdditionalInfo) {
//                            if (StringUtils.isNoneBlank(primaryDefinition.getAdditionalInformation())) {
//                                createAdditionalSentenceTextStyle(sentenceParagraph, primaryDefinition.getAdditionalInformation().trim());
//                            }
//                            if (primaryDefinitions.indexOf(primaryDefinition) < primaryDefinitionsWithAdditionalInfo.size() - 1) {
//                                sentenceParagraph.createRun().addBreak();
//                            }
//                        }


                            for (final LangeekDefinition sentence : sentences) {
                                if (sentences.indexOf(sentence) == 0 && !sortedMap.isEmpty()) {
                                    sentenceParagraph.createRun().addBreak();
                                }
                                createSentenceTextStyle(sentenceParagraph, sentence.getForeignTranslation(), sentences.indexOf(sentence) + 1);
                                if (sentences.indexOf(sentence) == 0 && sentences.size() > 1) {
                                    sentenceParagraph.createRun().addBreak();
                                }
                            }
                        }
                    }
                }
                saveToFile(document, lesson);
            }
        }
    }

    private void printContentsOfTextBox(final XWPFParagraph paragraph, final String title) {
        XmlObject[] textBoxObjects =  paragraph.getCTP().selectPath("""
                declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'
                declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape'
                declare namespace v='urn:schemas-microsoft-com:vml'
                        .//*/wps:txbx/w:txbxContent | .//*/v:textbox/w:txbxContent""");

        for (int i =0; i < textBoxObjects.length; i++) {
            XWPFParagraph embeddedPara = null;
            try {
                XmlObject[] paraObjects = textBoxObjects[i].
                        selectChildren(
                                new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));

                for (int j=0; j<paraObjects.length; j++) {
                    embeddedPara = new XWPFParagraph(
                            CTP.Factory.parse(paraObjects[j].xmlText()), paragraph.getBody());

                    if (embeddedPara.getText().equals("This is a title of this book.")) {
                        // Get the underlying CTP (Content Type Paragraph)
                        CTP ctp = embeddedPara.getCTP();

                        // Clear existing runs
                        while (ctp.sizeOfRArray() > 0) {
                            ctp.removeR(0);
                        }

                        // Create a new run with the new text
                        CTR run = ctp.addNewR();
                        CTRPr runProperties = run.addNewRPr();
                        CTHpsMeasure fontSize = runProperties.addNewSz();
                        fontSize.setVal(new BigInteger("56"));
                        CTColor color = runProperties.addNewColor();
                        color.setVal("FFFFFF");
                        runProperties.addNewB();
                        CTFonts fonts = runProperties.addNewRFonts();
                        fonts.setAscii(FONT_KRISTEN_ITC);
                        fonts.setHAnsi(FONT_KRISTEN_ITC);

                        CTText text = run.addNewT();
                        text.setStringValue(title);

                        // Update the original XML in the document
                        paraObjects[j].set(ctp);
                    }

                    // System.out.println(embeddedPara.getText());
                }

            } catch (XmlException e) {
                //handle
            }
        }
    }



//    private void replaceTextInTextBoxes(XWPFDocument document, String oldText, String newText) {
//        // Iterate through all paragraphs in the document
//        for (XWPFParagraph paragraph : document.getParagraphs()) {
//            // Get all runs in the paragraph
//            List<XWPFRun> runs = paragraph.getRuns();
//
//            for (XWPFRun run : runs) {
//                // Check for drawings in this run
//                CTDrawing drawing = run.getCTR().getDrawingArray(0);
//                if (drawing != null) {
//                    // Process the drawing to find and replace text
//                    processDrawing(drawing, oldText, newText);
//                }
//            }
//        }
//    }
//
//    private static void processDrawing(CTDrawing drawing, String oldText, String newText) {
//        // This is a simplified example. In a real implementation, you would:
//        // 1. Navigate through the drawing to find the text body
//        // 2. Check if the text matches what you're looking for
//        // 3. Replace the text if it matches
//
//        // The exact code depends on the structure of your document
//        // This would typically involve working with the drawing's inline or anchor,
//        // then accessing the shape and its text body
//
//        // Example of accessing text in a shape (actual implementation will be more complex):
//         CTTextBody textBody = drawing.getInlineList().get(0).getGraphic().getGraphicData()
//             .getShape().getTxBody();
//
//         for (CTTextParagraph textParagraph : textBody.getPList()) {
//             String text = textParagraph.getTextString();
//             if (text.contains(oldText)) {
//                 // Replace the text
//                 // This is simplified; actual text replacement would be more complex
//             }
//         }
//    }

    private void createHeader(final XWPFDocument document, final String headerText) {
        final XWPFHeader header = document.createHeader(HeaderFooterType.FIRST);

        final XWPFParagraph headerParagraph = header.createParagraph();
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);

        final XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText(headerText.toUpperCase(Locale.ROOT));
        headerRun.setBold(true);
        headerRun.setColor(COLOR_BLUE);
        headerRun.setFontSize(36);
    }

    private void createSecurityHeader(final XWPFDocument document) {
        final XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);

        final XWPFParagraph headerParagraph = header.createParagraph();
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);

        final XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText("Created by R.Wilk, All rights reserved");
        headerRun.setBold(true);
        headerRun.setColor("FFFFFF");
        headerRun.setFontSize(18);
    }

    private void createFirstPageFooter(final XWPFDocument document, final LangeekLesson lesson) {
        XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
        XWPFFooter footer = policy.createFooter(STHdrFtr.FIRST);

        XWPFParagraph paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun run = paragraph.createRun();
        run.setText(lesson.getName());
        run.setFontFamily(FONT_KRISTEN_ITC);
        run.setColor(COLOR_BLUE);
        run.setFontSize(28);

        XWPFRun run2 = paragraph.createRun();
        run2.addBreak();
        run2.setText("By R.Wilk");
        run2.setFontFamily(FONT_KRISTEN_ITC);
        run2.setColor(COLOR_BLUE);
        run2.setFontSize(28);
    }

    private void createFooter(final XWPFDocument document) {
        final XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);
        final XWPFParagraph footerParagraph = footer.createParagraph();
        footerParagraph.setAlignment(ParagraphAlignment.CENTER);

        final String color = COLOR_BLUE;

        XWPFRun footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewInstrText().setStringValue(" PAGE ");
        footerRun.setColor(color);
        footerRun = footerParagraph.createRun();
        footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);

        footerRun = footerParagraph.createRun();
        footerRun.setText(" / ");
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
        setWordTextBase(paragraph, text + "\n", COLOR_BLUE, true, FONT_SIZE_VERY_BIG);
    }

    private void createForeignWordTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text, COLOR_BLUE, true, FONT_SIZE_BIG);
    }

    private void createAdditionalTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, " (" + text.trim() + ") ", COLOR_GREY, false, FONT_SIZE_MEDIUM);
    }

    private void createPartOfSpeechTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, "[" + text.trim() + "]", COLOR_GREY, false, FONT_SIZE_BIG);
    }

    private void createNativeWordTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text, COLOR_BLACK, true, FONT_SIZE_BIG);
    }

    private void createSentenceTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text, COLOR_GREY, false, FONT_SIZE_MEDIUM);
        // setWordTextBase(paragraph, text, COLOR_GREY, true, FONT_SIZE_MEDIUM);
        paragraph.setIndentationLeft(360);
    }

    private void createSentenceTextStyle(XWPFParagraph paragraph, String text, int index) {
        setWordTextBase(paragraph, index + ") " + text, COLOR_BLACK, false, FONT_SIZE_MEDIUM);
        // setWordTextBase(paragraph, text, COLOR_GREY, true, FONT_SIZE_MEDIUM);
        paragraph.setIndentationLeft(360);
    }

    private void createAdditionalSentenceTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text.trim(), COLOR_GREY, false, FONT_SIZE_MEDIUM);
        paragraph.setIndentationLeft(360);
    }

    private void createAdditionalLanguageVariantTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, " (" + text.trim() + ")", COLOR_GREY, false, FONT_SIZE_MEDIUM);
    }

    private void setWordTextBase(XWPFParagraph paragraph, String text, String color, boolean isBold, int fontSize) {
        final XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(fontSize);
        run.setFontFamily(FONT_APTOS);
        run.setColor(color);
    }

    private void saveToFile(XWPFDocument document, LangeekLesson langeekLesson) {
        String directoryPath = "generated/langeek-pl/" + langeekLesson.getCourse().getName().replaceAll("[\"/:*?<>|]", "");
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, langeekLesson.getName().replaceAll("[\"/:*?<>|]", "") + ".docx");

        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        } catch (Exception e) {
            log.error("Failed to save to file", e);
        }
    }
}
