package rwilk.exploreenglish.scrapper.langeek.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.LangeekPromoBook;
import rwilk.exploreenglish.repository.langeek.LangeekPromoBookRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
    private static final String COLOR_RED = "FF0000";
    private static final String COLOR_DARK = "1A202F";
    private static final String COLOR_WHITE = "FFFFFF";

    private static final String COLOR_TEXT = "000000";
    private static final String COLOR_PRICE = COLOR_RED;

    private static final String EMOTICON_BOOK = "\uD83D\uDCD6";
    private static final String EMOTICON_PINES = "\uD83D\uDCCC";
    private static final String EMOTICON_PRICE = "\uD83D\uDCE2";

    private final LangeekPromoBookRepository langeekPromoBookRepository;

    @Override
    public void run(String... args) throws Exception {
        final Map<Long, List<LangeekPromoBook>> promoBooksMap = langeekPromoBookRepository
                .findAllPromoBooks()
                .stream()
                // .filter(promoBook -> promoBook.getId() >=7 && promoBook.getId() <= 87)
                .filter(promoBook -> promoBook.getPrice() != null)
                .collect(Collectors.groupingBy(LangeekPromoBook::getCourseId));

        final FileInputStream fis = new FileInputStream("template2.docx");
        final XWPFDocument document = new XWPFDocument(fis);

        for (Map.Entry<Long, List<LangeekPromoBook>> entry : promoBooksMap.entrySet()) {
            final List<LangeekPromoBook> promoBooks = entry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(LangeekPromoBook::getId))
                    .toList();

            // create table
            final XWPFTable table = document.createTable(promoBooks.size() + 2, 2);
            table.setWidth("100%");
            CTTblBorders borders = table.getCTTbl().getTblPr().addNewTblBorders();
            // Usunięcie wszystkich krawędzi
            borders.addNewTop().setVal(STBorder.NONE);
            borders.addNewBottom().setVal(STBorder.NONE);
            borders.addNewLeft().setVal(STBorder.NONE);
            borders.addNewRight().setVal(STBorder.NONE);
            borders.addNewInsideV().setVal(STBorder.NONE); // Usunięcie pionowych

            // Ustawienie tylko wewnętrznych linii poziomych
            borders.addNewInsideH().setVal(STBorder.NONE);
            //borders.getInsideH().setSz(BigInteger.valueOf(10)); // Grubość 1 pt (10 = 0.5 pt)


            // create header row
            XWPFTableCell mergedCell = table.getRow(0).getCell(0);
            XWPFParagraph titleParagraph = mergedCell.getParagraphs().get(0);
            titleParagraph.setStyle("Nagwek1");
            setWordTextBase(titleParagraph, entry.getValue().get(0).getCourseName(), COLOR_WHITE, true, 36);
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            mergedCell.setColor(COLOR_DARK);
            mergedCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            mergedCell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.TWO);
            table.getRow(0).getCtRow().removeTc(1);
            addTableBorders(mergedCell);

            // create content rows
            for (LangeekPromoBook promoBook : promoBooks) {
                int index = promoBooks.indexOf(promoBook) + 1;
                int textIndex = index % 2 == 0 ? 0 : 1;
                int imageIndex = index % 2 == 0 ? 1 : 0;

                XWPFParagraph paragraph = table.getRow(index).getCell(textIndex).getParagraphs().get(0);
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                paragraph.setSpacingBetween(SPACING_BETWEEN);
                paragraph.setSpacingBefore(240);
                table.getRow(index).getCell(textIndex).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                // createTitle(paragraph, promoBook, textIndex == 0 ? COLOR_WHITE : COLOR_TEXT);
                createTitle(paragraph, promoBook, COLOR_WHITE);
                // table.getRow(index).getCell(textIndex).setColor(textIndex == 0 ? COLOR_DARK : COLOR_WHITE);
                table.getRow(index).getCell(textIndex).setColor(COLOR_DARK);

                XWPFParagraph imageParagraph = table.getRow(index).getCell(imageIndex).getParagraphs().get(0);
                imageParagraph.setAlignment(ParagraphAlignment.CENTER);
                imageParagraph.setSpacingBetween(SPACING_BETWEEN);
                imageParagraph.setSpacingBefore(240);
                table.getRow(index).getCell(imageIndex).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                 table.getRow(index).getCell(imageIndex).setColor(textIndex == 0 ? COLOR_DARK : COLOR_WHITE);

                FileInputStream inputStream = new FileInputStream("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\book images\\" + promoBook.getId() + ".jpg");
                int maxWidth = Units.toEMU(84);
                XWPFRun run = imageParagraph.createRun();
                run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG, promoBook.getId() + ".jpg",
                        maxWidth, Units.toEMU(118)); // Szerokość 100 EMU (dopasowanie)
                inputStream.close();
            }

            // create footer row
            createFooterRow(table, promoBooks);

            document.createParagraph().setPageBreak(true);
        }

        // Zapisanie dokumentu do pliku
        try (FileOutputStream out = new FileOutputStream("table_doc.docx")) {
            document.write(out);
            System.out.println("Dokument z tabelą został utworzony!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTitle(final XWPFParagraph paragraph, final LangeekPromoBook promoBook, final String colorText) {
        setWordTextBase(paragraph, EMOTICON_BOOK, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " " + promoBook.getName() + " ", colorText, true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, EMOTICON_BOOK, "156082", true, FONT_SIZE_BIG);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, "to", colorText, false, FONT_SIZE_BIG);
        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " " + promoBook.getExercisesCount().toString(), colorText, true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " rozdziałów", colorText, false, FONT_SIZE_BIG);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " " + promoBook.getWordsCount().toString(), colorText, true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " słówek", colorText, false, FONT_SIZE_BIG);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PRICE, "156082", true, FONT_SIZE_BIG);
        setWordTextBase(paragraph, " Cena: " + promoBook.getPrice() + "zł", COLOR_PRICE, true, FONT_SIZE_VERY_BIG);
    }

    private void createFooterRow(final XWPFTable table, final List<LangeekPromoBook> promoBooks) {
        XWPFTableCell mergedFooterCell = table.getRow(promoBooks.size() + 1).getCell(0);
        XWPFParagraph paragraph = mergedFooterCell.getParagraphs().get(0);
        paragraph.createRun().addBreak();
        mergedFooterCell.setColor(COLOR_DARK);
        addTableBorders(mergedFooterCell);

        final int textSize = 16;

        setWordTextBase(paragraph, EMOTICON_BOOK, "156082", true, textSize);
        setWordTextBase(paragraph, " Zawartość zestawu ", COLOR_WHITE, true, textSize);
        setWordTextBase(paragraph, EMOTICON_BOOK, "156082", true, textSize);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, "to", COLOR_WHITE, false, textSize);
        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, textSize);
        setWordTextBase(paragraph, " " + promoBooks.stream().mapToLong(LangeekPromoBook::getExercisesCount).sum(), COLOR_WHITE, true, textSize);
        setWordTextBase(paragraph, " rozdziałów", COLOR_WHITE, false, textSize);

        paragraph.createRun().addBreak();
        setWordTextBase(paragraph, EMOTICON_PINES, "156082", true, textSize);
        setWordTextBase(paragraph, " " + promoBooks.stream().mapToLong(LangeekPromoBook::getWordsCount).sum(), COLOR_WHITE, true, textSize);
        setWordTextBase(paragraph, " słówek", COLOR_WHITE, false, textSize);
        paragraph.createRun().addBreak();

        setWordTextBase(paragraph, EMOTICON_PRICE , COLOR_RED, true, 36);
        setWordTextBase(paragraph, "Cena zestawu: " + promoBooks.get(0).getCoursePrice() + "zł", COLOR_RED, true, 28);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        mergedFooterCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        mergedFooterCell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.TWO);
        table.getRow(promoBooks.size() + 1).getCtRow().removeTc(1);
    }

    private void setWordTextBase(XWPFParagraph paragraph, String text, String color, boolean isBold, int fontSize) {
        final XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(fontSize);
        run.setFontFamily(FONT_APTOS);
        run.setColor(color);
    }

    private void addTableBorders(XWPFTableCell mergedCell) {
        CTTcPr tcPr = mergedCell.getCTTc().getTcPr();
        if (tcPr == null) {
            tcPr = mergedCell.getCTTc().addNewTcPr();
        }

        CTTcBorders borders = tcPr.addNewTcBorders();

        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.getTop().setSz(BigInteger.valueOf(24)); // 24 = 3 pt
        borders.getTop().setColor(COLOR_TEAL);

        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.getBottom().setSz(BigInteger.valueOf(24));
        borders.getBottom().setColor(COLOR_TEAL);

        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.getLeft().setSz(BigInteger.valueOf(24));
        borders.getLeft().setColor(COLOR_TEAL);

        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.getRight().setSz(BigInteger.valueOf(24));
        borders.getRight().setColor(COLOR_TEAL);
    }

}
