package rwilk.exploreenglish.scrapper.langeek.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.LangeekPromoBook;
import rwilk.exploreenglish.repository.langeek.LangeekPromoBookRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(100)
@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekPromoBookExportService implements CommandLineRunner {

    private static final String FONT_CALIBRI = "Calibri";
    private static final String FONT_APTOS = "Aptos";
    private static final String FONT_KRISTEN_ITC = "Kristen ITC";
    private static final int FONT_SIZE_VERY_BIG = 14;
    private static final int FONT_SIZE_BIG = 12;
    private static final int FONT_SIZE_MEDIUM = 11;
    //    private static final int FONT_SIZE_SMALL = 11;
    private static final double SPACING_BETWEEN = 1.5;

    private static final String COLOR_BLUE = "0065C1";
    private static final String COLOR_BLACK = "000000";
    private static final String COLOR_GREY = "757575";
    private static final String COLOR_TEAL = "03DAC5";
    private static final String COLOR_CYTAT = "0f4761";
    private static final String EMOTICON_BOOK = "\uD83D\uDCD6";
    private static final String EMOTICON_PINES = "\uD83D\uDCCC";
    private static final String EMOTICON_PRICE = "\uD83D\uDCE2";

    private final LangeekPromoBookRepository langeekPromoBookRepository;

    @Override
    public void run(String... args) throws Exception {
        final Map<Long, List<LangeekPromoBook>> promoBooksMap = langeekPromoBookRepository
                .findAllPromoBooks()
                .stream()
                .collect(Collectors.groupingBy(LangeekPromoBook::getCourseId));

        final FileInputStream fis = new FileInputStream("template1.docx");
        final XWPFDocument document = new XWPFDocument(fis);

        for (Map.Entry<Long, List<LangeekPromoBook>> entry : promoBooksMap.entrySet()) {
            final List<LangeekPromoBook> promoBooks = entry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(LangeekPromoBook::getId))
                    .toList();

            final XWPFParagraph exerciseParagraph = document.createParagraph();
            exerciseParagraph.setSpacingBetween(SPACING_BETWEEN);
            exerciseParagraph.setStyle("Cytatintensywny");
            setWordTextBase(exerciseParagraph, entry.getValue().get(0).getCourseName(), COLOR_BLUE, true, FONT_SIZE_VERY_BIG);

            final XWPFTable table = document.createTable((int) Math.ceil(promoBooks.size() / 2.0), 2);
            table.setWidth("100%");

            int rowNumber = 0;
            int columnNumber = 0;

            for (LangeekPromoBook promoBook : promoBooks) {
                XWPFParagraph paragraph = table.getRow(rowNumber).getCell(columnNumber).getParagraphs().get(0);
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                paragraph.setSpacingBetween(SPACING_BETWEEN);
                paragraph.setSpacingBefore(240);
                table.getRow(rowNumber).getCell(columnNumber).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                createTitle(paragraph, promoBook);

                if (columnNumber == 0) {
                    columnNumber = 1;
                } else {
                    columnNumber = 0;
                    rowNumber++;
                }
            }


        }

        // Zapisanie dokumentu do pliku
        try (FileOutputStream out = new FileOutputStream("table_doc.docx")) {
            document.write(out);
            System.out.println("Dokument z tabelą został utworzony!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTitle(final XWPFParagraph paragraph, final LangeekPromoBook promoBook) {
        setWordTextBase(paragraph, EMOTICON_BOOK, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " " + promoBook.getName() + " ", COLOR_BLACK, true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, EMOTICON_BOOK, "156082", true, FONT_SIZE_BIG);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, "to", COLOR_BLACK, false, FONT_SIZE_BIG);
        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " " + promoBook.getExercisesCount().toString(), COLOR_BLACK, true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " rodziałów", COLOR_BLACK, false, FONT_SIZE_BIG);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " " + promoBook.getWordsCount().toString(), COLOR_BLACK, true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " słówek", COLOR_BLACK, false, FONT_SIZE_BIG);

        paragraph.createRun().addBreak();
        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " Cena: 15zł", COLOR_BLACK, true, FONT_SIZE_BIG);
    }

    private void setWordTextBase(XWPFParagraph paragraph, String text, String color, boolean isBold, int fontSize) {
        final XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(fontSize);
        run.setFontFamily(FONT_APTOS);
        run.setColor(color);
    }
}
