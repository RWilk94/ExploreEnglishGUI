package rwilk.exploreenglish.scrapper.langeek.export;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ListStyles {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("test.docx");
             XWPFDocument document = new XWPFDocument(fis)) {

//            A1 Level Wordlist
            XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);

            // Dodajemy akapit do nagłówka
            XWPFParagraph headerParagraph = header.createParagraph();
            headerParagraph.setAlignment(ParagraphAlignment.CENTER); // Ustawienie wyrównania

            // Tworzymy tekst w nagłówku
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setText("A1 LEVEL WORDLIST");
            headerRun.setBold(true); // Pogrubienie
            headerRun.setFontSize(48); // Ustawienie wielkości czcionki


            // Tworzymy stopkę dla dokumentu
            XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

            // Dodajemy akapit do stopki
            XWPFParagraph footerParagraph = footer.createParagraph();
            footerParagraph.setAlignment(ParagraphAlignment.CENTER); // Wyrównanie do środka

            // Dodajemy numer strony jako pole PAGE
            XWPFRun footerRun = footerParagraph.createRun();
            footerRun.setText("Strona ");

            footerRun = footerParagraph.createRun();
            footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN); // Początek pola
            footerRun = footerParagraph.createRun();
            footerRun.getCTR().addNewInstrText().setStringValue(" PAGE "); // Pole numeru strony
            footerRun = footerParagraph.createRun();
            footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.END); // Koniec pola

            // Dodajemy separator "/"
            footerRun = footerParagraph.createRun();
            footerRun.setText(" z ");

            // Dodajemy całkowitą liczbę stron jako pole NUMPAGES
            footerRun = footerParagraph.createRun();
            footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
            footerRun = footerParagraph.createRun();
            footerRun.getCTR().addNewInstrText().setStringValue(" NUMPAGES "); // Pole liczby stron
            footerRun = footerParagraph.createRun();
            footerRun.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);







            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setStyle("Nagwek1");
            paragraph.createRun().setText("Hello World!");

            XWPFParagraph paragraph2 = document.createParagraph();
            paragraph2.setStyle("Nagłówek 1 Znak");
            paragraph2.createRun().setText("Hello World!");

            XWPFParagraph paragraph3 = document.createParagraph();
            paragraph3.setStyle("CytatintensywnyZnak");
            paragraph3.createRun().setText("Hello World!");

//            document.getStyles().getStyle()

            XWPFParagraph paragraph4 = document.createParagraph();
            paragraph4.setStyle("Cytatintensywny");
            paragraph4.createRun().setText("Hello World!");
//            XWPFParagraph paragraph2 = document.createParagraph();
//            paragraph.setStyle("Nagłówek1");
//            paragraph.createRun().setText("Hello World!");

//            XWPFStyles styles = document.getStyles();
//            styles.getUsedStyleList()
//            if (styles != null) {
//                Collection<XWPFStyle> styleCollection = styles.getStyles(); // Pobiera dostępne style
//                for (XWPFStyle style : styleCollection) {
//                    System.out.println("Styl: " + style.getName() + " | ID: " + style.getStyleId());
//                }
//            } else {
//                System.out.println("Brak stylów w dokumencie.");
//            }

            try (FileOutputStream out = new FileOutputStream("template1.docx")) {
                document.write(out);
            }

            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
