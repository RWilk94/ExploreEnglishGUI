package rwilk.exploreenglish.export.doc;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.TextRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.etutor.*;
import rwilk.exploreenglish.repository.etutor.*;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.awt.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportDocServiceV2 implements CommandLineRunner {

    private static final String FONT_CALIBRI = "Calibri";
    private static final float FONT_SIZE_VERY_BIG = 18.0f;
    private static final float FONT_SIZE_BIG = 14.0f;
    private static final float FONT_SIZE_MEDIUM = 12.0f;
    private static final float FONT_SIZE_SMALL = 11.0f;

    private static final Color COLOR_BLUE = new Color(0, 101, 193);
    private static final Color COLOR_BLACK = new Color(0, 0, 0);
    private static final Color COLOR_GREY = new Color(117, 117, 117);
    private static final Color COLOR_TEAL = new Color(3, 218, 197);


    private final EtutorCourseRepository etutorCourseRepository;
    private final EtutorLessonRepository etutorLessonRepository;
    private final EtutorExerciseRepository etutorExerciseRepository;
    private final EtutorLessonWordRepository etutorLessonWordRepository;

    @Override
    public void run(String... args) throws Exception {
        // generateDocument();
    }

    public void generateDocument() {

        final EtutorCourse etutorCourse = etutorCourseRepository.findById(1L).orElseThrow();
        final List<EtutorLesson> etutorLessons = etutorLessonRepository.findAllByCourse_Id(etutorCourse.getId());

        final Document document = createDocument();

        final Section section = createSection(document);
        for (final EtutorLesson etutorLesson : etutorLessons) {

            final Paragraph paragraph = createParagraph(section);
            createLessonDescriptionTextStyle(paragraph, etutorLesson.getDescription());

            if (etutorLessons.indexOf(etutorLesson) > 10) {
                continue;
            }

            final List<EtutorExercise> etutorExercises = etutorExerciseRepository.findAllByLesson_Id(etutorLesson.getId())
                    .stream()
                    .filter(it -> List.of(
                            ExerciseType.WORDS_LIST.toString(),
                            ExerciseType.PICTURES_WORDS_LIST.toString(),
                            ExerciseType.GRAMMAR_LIST.toString()).contains(it.getType()))
                    .toList();

            for (final EtutorExercise etutorExercise : etutorExercises) {
                final List<EtutorLessonWord> etutorLessonWords = etutorLessonWordRepository.findAllByExercise_Id(etutorExercise.getId());

                for (final EtutorLessonWord etutorLessonWord : etutorLessonWords) {
                    final EtutorWord etutorWord = etutorLessonWord.getWord();
                    final List<EtutorDefinition> etutorDefinitions = etutorWord.getDefinitions();

                    final List<EtutorDefinition> primaryDefinitions = etutorDefinitions.stream()
                            .filter(it -> it.getType().equals("WORD"))
                            .toList();
                    final List<EtutorDefinition> secondaryDefinitions = etutorDefinitions.stream()
                            .filter(it -> !it.getType().equals("WORD"))
                            .toList();

                    for (final EtutorDefinition primaryDefinition : primaryDefinitions) {
                        if (primaryDefinitions.indexOf(primaryDefinition) > 0) {
                            createForeignWordTextStyle(paragraph, " / ");
                        }
                        createForeignWordTextStyle(paragraph, primaryDefinition.getForeignTranslation());
                        if (StringUtils.isNoneBlank(primaryDefinition.getAdditionalInformation())) {
                            createAdditionalTextStyle(paragraph, primaryDefinition.getAdditionalInformation().trim());
                        }
                    }
                    createNativeWordTextStyle(paragraph, " = ");

                    createNativeWordTextStyle(paragraph, etutorWord.getNativeTranslation());
                    if (StringUtils.isNoneBlank(etutorWord.getAdditionalInformation())) {
                        createAdditionalTextStyle(paragraph, etutorWord.getAdditionalInformation());
                    }
                    paragraph.appendText("\n");

                    for (final EtutorDefinition secondaryDefinition : secondaryDefinitions) {
                        createSentenceTextStyle(paragraph, secondaryDefinition.getForeignTranslation());
                        if (StringUtils.isNoneBlank(secondaryDefinition.getAdditionalInformation())) {
                            createAdditionalTextStyle(paragraph, secondaryDefinition.getAdditionalInformation().trim());
                        }
                        paragraph.appendText("\n");
                    }
                }
                createNewLineParagraph(section);

            }


        }

        saveToFile(document);
    }

    private Document createDocument() {
        return new Document();
    }

    private Section createSection(final Document document) {
        return document.addSection();
    }

    private Paragraph createParagraph(final Section section) {
        final Paragraph paragraph = section.addParagraph();
        paragraph.getFormat().setLineSpacing(18.0f);
        // paragraph.getFormat().setBackColor(new Color(242, 242, 242));
        return paragraph;
    }

    private void createNewLineParagraph(final Section section) {
        final Paragraph spaceParagraph = section.addParagraph();
        spaceParagraph.appendText("\n");
    }

    private void createLessonDescriptionTextStyle(final Paragraph paragraph, final String text) {
        setWordTextBase(paragraph, text + "\n", COLOR_TEAL, true, FONT_SIZE_VERY_BIG);
    }

    private void createForeignWordTextStyle(final Paragraph paragraph, final String text) {
        setWordTextBase(paragraph, text, COLOR_BLUE, true, FONT_SIZE_BIG);
    }

    private void createAdditionalTextStyle(final Paragraph paragraph, final String text) {
        final String additionalText = "(" + text.trim() + ")";
        setWordTextBase(paragraph, additionalText, COLOR_GREY, false, FONT_SIZE_MEDIUM);
    }

    private void createNativeWordTextStyle(final Paragraph paragraph, final String text) {
        setWordTextBase(paragraph, text, COLOR_BLACK, false, FONT_SIZE_BIG);
    }

    private void createSentenceTextStyle(final Paragraph paragraph, final String text) {
        setWordTextBase(paragraph, "\t" + text, COLOR_BLACK, true, FONT_SIZE_MEDIUM);
    }

    private TextRange setWordTextBase(final Paragraph paragraph, final String text, final Color color,
                                      final boolean isBold, final float fontSize) {
        final TextRange textRange = paragraph.appendText(text);
        textRange.getCharacterFormat().setTextColor(color);
        textRange.getCharacterFormat().setBold(isBold);
        textRange.getCharacterFormat().setFontName(FONT_CALIBRI);
        textRange.getCharacterFormat().setFontSize(fontSize);
        if (!textRange.getText().endsWith(" ")) {
            textRange.setText(textRange.getText().concat(" "));
        }
        return textRange;
    }

    private void saveToFile(final Document document) {
        document.saveToFile("Output.docx", FileFormat.Docx);
    }
}
