package rwilk.exploreenglish.export.doc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.etutor.*;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonWordRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportDocServiceV3 implements CommandLineRunner {

    // private static final String FONT_CALIBRI = "Calibri";
    private static final String FONT_CALIBRI = "Aptos";
    private static final String FONT_KRISTEN_ITC = "Kristen ITC";
    private static final int FONT_SIZE_VERY_BIG = 14;
    private static final int FONT_SIZE_BIG = 12;
    private static final int FONT_SIZE_MEDIUM = 10;
//    private static final int FONT_SIZE_SMALL = 11;
    private static final double SPACING_BETWEEN = 1.5;

    private static final String COLOR_BLUE = "0065C1";
    private static final String COLOR_BLACK = "000000";
    private static final String COLOR_GREY = "757575";
    private static final String COLOR_TEAL = "03DAC5";
    private static final String COLOR_DARK = "1A202F";

    private final EtutorCourseRepository etutorCourseRepository;
    private final EtutorLessonRepository etutorLessonRepository;
    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorLessonWordRepository etutorLessonWordRepository;

    @Override
    public void run(String... args) throws Exception {
        // generateDocument();
    }

    public void generateDocument() throws IOException {
        final List<EtutorCourse> etutorCourses = etutorCourseRepository.findAll()
                .stream()
                .filter(it -> it.getId() <= 14)
                .toList();

        for (final EtutorCourse etutorCourse : etutorCourses) {
            log.info("Generating document for course: {}", etutorCourse.getName());
            final List<EtutorLesson> etutorLessons = etutorLessonRepository.findAllByCourse_Id(etutorCourse.getId());

            final FileInputStream fis = new FileInputStream("template3.docx");
            final XWPFDocument document = new XWPFDocument(fis);

            document.getParagraphs().forEach(paragraph -> {
                setTitleOfTheBook(paragraph, StringUtils.defaultString(etutorCourse.getDescription(), etutorCourse.getName()));
            });
            createFooter(document);

            for (final EtutorLesson etutorLesson : etutorLessons) {
                final List<EtutorExercise> etutorExercises = etutorExerciseRepository.findAllByLesson_Id(etutorLesson.getId())
                        .stream()
                        .filter(it -> List.of(
                                ExerciseType.WORDS_LIST.toString(),
                                ExerciseType.PICTURES_WORDS_LIST.toString()/*,
                                ExerciseType.GRAMMAR_LIST.toString()*/).contains(it.getType()))
                        .toList();

                for (final EtutorExercise etutorExercise : etutorExercises) {
                    final List<EtutorLessonWord> etutorLessonWords = etutorLessonWordRepository.findAllByExercise_Id(etutorExercise.getId());

                    if (etutorExercises.indexOf(etutorExercise) > 0 ) {
                        final int currentIndex = etutorExercises.indexOf(etutorExercise);
                        final String previousName = etutorExercises.get(currentIndex - 1).getName();
                        final String currentName = etutorExercise.getName();
                        if (previousName.startsWith(currentName.split(" - ")[0])) {
                        } else {
                            createLessonDescriptionTextStyle(document.createParagraph(), etutorExercise.getName().split(" - ")[0]);
                        }

                    } else {
                        createLessonDescriptionTextStyle(document.createParagraph(), etutorExercise.getName().split(" - ")[0]);
                    }

                    for (final EtutorLessonWord etutorLessonWord : etutorLessonWords) {
                        final EtutorWord etutorWord = etutorLessonWord.getWord();
                        final List<EtutorDefinition> etutorDefinitions = etutorWord.getDefinitions();

                        appendPrimaryDefinitions(document, etutorWord, etutorDefinitions);
                        appendAdditionalInfo(document, etutorDefinitions);
                        appendSentences(document, etutorDefinitions);
                    }
                }
            }
            saveToFile(document, etutorCourse);
        }
    }

    private void appendPrimaryDefinitions(final XWPFDocument document, final EtutorWord etutorWord,
                                          final List<EtutorDefinition> etutorDefinitions) {
        final List<EtutorDefinition> primaryDefinitions = etutorDefinitions.stream()
                .filter(it -> it.getType().equals("WORD"))
                .toList();

        XWPFParagraph primaryParagraph = document.createParagraph();
        primaryParagraph.setSpacingBetween(SPACING_BETWEEN);
        primaryParagraph.setSpacingBefore(0);
        primaryParagraph.setSpacingAfter(0);

        for (final EtutorDefinition primaryDefinition : primaryDefinitions) {
            if (primaryDefinitions.indexOf(primaryDefinition) > 0) {
                createForeignWordTextStyle(primaryParagraph, " / ");
            }
            createForeignWordTextStyle(primaryParagraph, primaryDefinition.getForeignTranslation());
            if (StringUtils.isNoneBlank(primaryDefinition.getAdditionalInformation())) {
                createAdditionalTextStyle(primaryParagraph, primaryDefinition.getAdditionalInformation().trim());
            }
        }
        createNativeWordTextStyle(primaryParagraph, " = ");

        createNativeWordTextStyle(primaryParagraph, etutorWord.getNativeTranslation());
        if (StringUtils.isNoneBlank(etutorWord.getAdditionalInformation())) {
            createAdditionalTextStyle(primaryParagraph, etutorWord.getAdditionalInformation());
        }
    }

    private void appendAdditionalInfo(final XWPFDocument document, final List<EtutorDefinition> etutorDefinitions) {
        final List<String> order = List.of("PAST_TENSE", "PAST_PARTICIPLE", "PRESENT_PARTICIPLE", "COMPARATIVE",
                "SUPERLATIVE", "PLURAL", "SYNONYM", "OPPOSITE");
        final Map<String, List<EtutorDefinition>> secondaryDefinitionsMap = etutorDefinitions.stream()
                .filter(it -> order.contains(it.getType()))
                .collect(groupingBy(EtutorDefinition::getType, LinkedHashMap::new, toList()));

        final Map<String, List<EtutorDefinition>> sortedMap = secondaryDefinitionsMap.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> order.indexOf(e.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        if (sortedMap.isEmpty()) {
            return;
        }
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
                case "COMPARATIVE":
                    createSentenceTextStyle(sentenceParagraph, appendSpace + "comparative: ");
                    break;
                case "SUPERLATIVE":
                    createSentenceTextStyle(sentenceParagraph, appendSpace + "superlative: ");
                    break;
                case "PLURAL":
                    createSentenceTextStyle(sentenceParagraph, appendSpace + "plural: ");
                    break;
                case "SYNONYM":
                    createSentenceTextStyle(sentenceParagraph, appendSpace + "synonym: ");
                    break;
                case "OPPOSITE":
                    createSentenceTextStyle(sentenceParagraph, appendSpace + "opposite: ");
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
    }

    private void appendSentences(final XWPFDocument document, final List<EtutorDefinition> etutorDefinitions) {
        final List<EtutorDefinition> sentenceDefinitions = etutorDefinitions.stream()
                .filter(it -> it.getType().equals("SENTENCE"))
                .toList();

        if (CollectionUtils.isNotEmpty(sentenceDefinitions)) {
            final XWPFParagraph sentenceParagraph = document.createParagraph();
            sentenceParagraph.setSpacingBetween(SPACING_BETWEEN);
            sentenceParagraph.setSpacingBefore(0);
            sentenceParagraph.setSpacingAfter(0);

            final List<EtutorDefinition> sentenceDefinitionsToShow;
            if (sentenceDefinitions.size() > 2) {
                sentenceDefinitionsToShow = sentenceDefinitions.subList(0, 2);
            } else {
                sentenceDefinitionsToShow = sentenceDefinitions;
            }

            for (final EtutorDefinition secondaryDefinition : sentenceDefinitionsToShow) {
                createSentenceTextStyle(sentenceParagraph, secondaryDefinition.getForeignTranslation());
                if (StringUtils.isNoneBlank(secondaryDefinition.getAdditionalInformation())) {
                    createAdditionalSentenceTextStyle(sentenceParagraph, secondaryDefinition.getAdditionalInformation().trim());
                }
                if (sentenceDefinitionsToShow.indexOf(secondaryDefinition) < sentenceDefinitionsToShow.size() - 1) {
                    sentenceParagraph.createRun().addBreak();
                }
            }
        }
    }

    private void createLessonDescriptionTextStyle(XWPFParagraph paragraph, String text) {
        paragraph.setSpacingBetween(SPACING_BETWEEN);
        paragraph.setSpacingBefore(240);
        paragraph.setStyle("Cytatintensywny");
        setWordTextBase(paragraph, text + "\n", COLOR_BLUE, true, FONT_SIZE_VERY_BIG);
    }

    private void createLessonDescriptionTextStyleDark(XWPFParagraph paragraph, String text) {
        paragraph.setSpacingBetween(SPACING_BETWEEN);
        paragraph.setSpacingBefore(240);
        paragraph.setStyle("Cytatintensywny");
        setWordTextBase(paragraph, text + "\n", COLOR_BLUE, true, FONT_SIZE_VERY_BIG);
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
        setWordTextBase(paragraph, " = " + text.trim(), COLOR_GREY, false, FONT_SIZE_MEDIUM);
    }

    private void setWordTextBase(XWPFParagraph paragraph, String text, String color, boolean isBold, int fontSize) {
        final XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(fontSize);
        run.setFontFamily(FONT_CALIBRI);
        run.setColor(color);
    }


    private void setTitleOfTheBook(final XWPFParagraph paragraph, final String title) {
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
                        CTP ctp = embeddedPara.getCTP();

                        while (ctp.sizeOfRArray() > 0) {
                            ctp.removeR(0);
                        }

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

                        paraObjects[j].set(ctp);
                    }
                }

            } catch (XmlException e) {
            }
        }
    }

    private void saveToFile(final XWPFDocument document, final EtutorCourse etutorCourse) {
        final String directoryPath = "generated/etutor/" + etutorCourse.getName().replaceAll("[\"/:*?<>|]", "");
        final File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        final File file = new File(directory, StringUtils.defaultString(etutorCourse.getDescription(), etutorCourse.getName()).replaceAll("[\"/:*?<>|]", "") + ".docx");

        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        } catch (Exception e) {
            log.error("Failed to save to file", e);
        }
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
}
