package rwilk.exploreenglish.export.doc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.etutor.*;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonWordRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportDocServiceV3 implements CommandLineRunner {

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

    private final EtutorCourseRepository etutorCourseRepository;
    private final EtutorLessonRepository etutorLessonRepository;
    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorLessonWordRepository etutorLessonWordRepository;

    @Override
    public void run(String... args) throws Exception {
        // generateDocument();
    }

    public void generateDocument() throws IOException {
        final EtutorCourse etutorCourse = etutorCourseRepository.findById(1L).orElseThrow();
        final List<EtutorLesson> etutorLessons = etutorLessonRepository.findAllByCourse_Id(etutorCourse.getId());

        XWPFDocument document = new XWPFDocument();
        for (final EtutorLesson etutorLesson : etutorLessons) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setSpacingBetween(SPACING_BETWEEN);
            paragraph.setSpacingBefore(0);
            paragraph.setSpacingAfter(0);
            createLessonDescriptionTextStyle(paragraph, etutorLesson.getDescription());

            final List<EtutorExercise> etutorExercises = etutorExerciseRepository.findAllByLesson_Id(etutorLesson.getId())
                    .stream()
                    .filter(it -> List.of(
                            ExerciseType.WORDS_LIST.toString(),
                            ExerciseType.PICTURES_WORDS_LIST.toString(),
                            ExerciseType.GRAMMAR_LIST.toString()).contains(it.getType()))
                    .toList();

            for (final EtutorExercise etutorExercise : etutorExercises) {
                final List<EtutorLessonWord> etutorLessonWords = etutorLessonWordRepository.findAllByExercise_Id(etutorExercise.getId());

                createLessonDescriptionTextStyle(paragraph, etutorExercise.getName());

                for (final EtutorLessonWord etutorLessonWord : etutorLessonWords) {
                    final EtutorWord etutorWord = etutorLessonWord.getWord();
                    final List<EtutorDefinition> etutorDefinitions = etutorWord.getDefinitions();

                    final List<EtutorDefinition> primaryDefinitions = etutorDefinitions.stream()
                            .filter(it -> it.getType().equals("WORD"))
                            .toList();
                    final List<EtutorDefinition> secondaryDefinitions = etutorDefinitions.stream()
                            .filter(it -> !it.getType().equals("WORD"))
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

                    if (CollectionUtils.isNotEmpty(secondaryDefinitions)) {
                        final XWPFParagraph sentenceParagraph = document.createParagraph();
                        sentenceParagraph.setSpacingBetween(SPACING_BETWEEN);
                        sentenceParagraph.setSpacingBefore(0);
                        sentenceParagraph.setSpacingAfter(0);
                        for (final EtutorDefinition secondaryDefinition : secondaryDefinitions) {
                            createSentenceTextStyle(sentenceParagraph, secondaryDefinition.getForeignTranslation());
                            if (StringUtils.isNoneBlank(secondaryDefinition.getAdditionalInformation())) {
                                createAdditionalSentenceTextStyle(sentenceParagraph, secondaryDefinition.getAdditionalInformation().trim());
                            }
                            if (secondaryDefinitions.indexOf(secondaryDefinition) < secondaryDefinitions.size() - 1) {
                                sentenceParagraph.createRun().addBreak();
                            }
                        }
                    }
                }
            }
        }
        saveToFile(document);
    }

    private void createLessonDescriptionTextStyle(XWPFParagraph paragraph, String text) {
        setWordTextBase(paragraph, text + "\n", COLOR_TEAL, true, FONT_SIZE_VERY_BIG);
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

    private void saveToFile(XWPFDocument document) throws IOException {
        try (FileOutputStream out = new FileOutputStream("Output1.docx")) {
            document.write(out);
        }
    }
}
