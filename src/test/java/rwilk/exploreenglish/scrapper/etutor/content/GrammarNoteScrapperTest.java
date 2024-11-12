package rwilk.exploreenglish.scrapper.etutor.content;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GrammarNoteScrapperTest {

    @Mock
    EtutorExerciseRepository etutorExerciseRepository;

    @Mock
    EtutorNoteRepository etutorNoteRepository;

    private GrammarNoteScrapper grammarNoteScrapper;

    private final List<EtutorNoteItem> noteItems = new ArrayList<>();

    @BeforeEach
    void setUp() {
        grammarNoteScrapper = new GrammarNoteScrapper(etutorExerciseRepository, etutorNoteRepository);
    }

    @Test
    void test() {
        // given
        final String html = "<h1>Friend vs colleague</h1><div class=\"contentBox\"><h2 style=\"text-align: justify;\">Friend vs colleague</h2>\n" +
                "\n" +
                "<p style=\"text-align: justify;\"><span class=\"lessonRecordedExample\">Colleague </span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/colleague.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/colleague.mp3\"></span></span></span>od razu nasuwa nam skojarzenie z polskim kolegą/koleżanką, ale w języku angielskim ma inne znaczenie. Jest to klasyczny przypadek tzw. false friend, czyli słowa, które brzmi podobnie w obu wybranych&nbsp;językach, ale w każdym oznacza coś innego.</p>\n" +
                "\n" +
                "<p style=\"text-align: justify;\">W języku angielskim <span class=\"lessonRecordedExample\">colleague</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/colleague.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/colleague.mp3\"></span></span></span> oznacza <u>kolegę/koleżankę, ale wyłącznie z pracy lub po fachu</u>. Nie ma tu większej zażyłości w emocjach.</p>\n" +
                "\n" +
                "<p style=\"margin-left: 40px; text-align: justify;\"><span class=\"lessonRecordedExample\">She's not a friend, she's just a colleague.</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/shes_not_a_friend_shes_just_a_colleague.mp3\"></span></span></span> = Ona nie jest przyjaciółką, jest tylko koleżanką z pracy.</p>\n" +
                "\n" +
                "<p style=\"text-align: justify;\"><span class=\"lessonRecordedExample\">Friend</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/friend.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/friend.mp3\"></span></span></span> ma już większy ładunek emocjonalny. To osoba, z którą łączą nas bliskie, koleżeńskie bądź przyjacielskie relacje.</p>\n" +
                "\n" +
                "<p style=\"margin-left: 40px; text-align: justify;\"><span class=\"lessonRecordedExample\">She's more than a colleague. She's a friend.</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/shes_more_than_a_colleague_shes_a_friend.mp3\"></span></span></span> = Ona jest kimś więcej niż koleżanką z pracy. Jest przyjaciółką.</p>\n" +
                "\n" +
                "<h3 style=\"text-align: justify;\">Mate / buddy / pal</h3>\n" +
                "\n" +
                "<p style=\"text-align: justify;\">W brytyjskim angielskim o swoim kumplu bądź przyjacielu możemy również powiedzieć <span class=\"lessonRecordedExample\">mate</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/mate.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/mate.mp3\"></span></span></span>. Jest to luźna, nieformalna wersja.</p>\n" +
                "\n" +
                "<p style=\"margin-left: 40px; text-align: justify;\"><span class=\"lessonRecordedExample\">I'm going out with my mates.</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/im_going_out_with_my_mates.mp3\"></span></span></span> = Wychodzę z kumplami.</p>\n" +
                "\n" +
                "<p style=\"text-align: justify;\"><span class=\"lessonRecordedExample\">Buddy</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/buddy.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/buddy.mp3\"></span></span></span> i <span class=\"lessonRecordedExample\">pal</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/pal.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/pal.mp3\"></span></span></span> są natomiast bardziej uniwersalnymi określeniami. Można się spotkać z nimi zarówno w amerykańskim, jak i brytyjskim angielskim,&nbsp;w nieformalnym języku.</p>\n" +
                "\n" +
                "<p style=\"margin-left: 40px; text-align: justify;\"><span class=\"lessonRecordedExample\">Jack and I are good buddies.</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/jack_and_i_are_good_buddies.mp3\"></span></span></span> = Jack i ja jesteśmy dobrymi kumplami.</p>\n" +
                "\n" +
                "<p style=\"margin-left: 40px; text-align: justify;\"><span class=\"lessonRecordedExample\">Hey, pal. What are you doing?</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/hey_pal_what_are_you_doing.mp3\"></span></span></span> = Cześć, kolego. Co porabiasz?</p>\n" +
                "\n" +
                "<div class=\"lessonImportantNote\" style=\"text-align: justify;\">\n" +
                "  <span class=\"lessonRecordedExample\">Mate</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/mate.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/mate.mp3\"></span></span></span>, <span class=\"lessonRecordedExample\">buddy</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/buddy.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/buddy.mp3\"></span></span></span>, <span class=\"lessonRecordedExample\">pal </span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/pal.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/pal.mp3\"></span></span></span>mogą również być użyte w sposób nieco agresywny, kiedy zwracamy się do obcych, którzy coś przeskrobali: <span class=\"lessonRecordedExample\">It's not your turn, pal.</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/its_not_your_turn_pal.mp3\"></span></span></span> = Nie twoja kolej, koleżko.\n" +
                "</div></div>";

        Document document = Jsoup.parse(html);


        printLeafNodes(document);

        System.err.println("FINISH");
        System.err.println(noteItems);
    }

    public void printLeafNodes(Document document) {
        // Pobranie pierwszego elementu z dokumentu
        Element rootElement = document.getAllElements().first();

        if (rootElement != null) {
            // Rekurencyjne przejście po drzewie DOM od pierwszego elementu
            printLeafNodesRecursive(rootElement);
        }
    }

    private void printLeafNodesRecursive(Element element) {
        if (element.children().isEmpty()) {
            System.out.println(element.text());
        } else {
            for (Node child : element.childNodes()) {
                printLeafNodesRecursive(child);
            }
        }
    }

    private void printLeafNodesRecursive(Node node) {
        if (node.childNodes().isEmpty()) {
            if (node instanceof TextNode textNode) {
                final String text = textNode.text();

                if (StringUtils.isNoneBlank(text)) {
                    noteItems.add(
                            EtutorNoteItem.builder()
                                    .plainText(text)
                                    .example(null)
                                    .britishSound(null)
                                    .americanSound(null)
                                    .primaryStyle(getPrimaryStyle(node))
                                    .secondaryStyle(getSecondaryStyle(node))
                                    .build()
                    );
                }

            } else if (node instanceof Element element) {
                if (element.tagName().equals("span")) {
                    final String dataAudioUrl = element.attributes().get("data-audio-url");
                    if (StringUtils.isNoneBlank(dataAudioUrl)) {
                        if (dataAudioUrl.contains("en-ame")) {
                            noteItems.get(noteItems.size() - 1).setAmericanSound(dataAudioUrl);
                        } else {
                            noteItems.get(noteItems.size() - 1).setBritishSound(dataAudioUrl);
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException(node.nodeName());
            }
        } else {
            for (Node child : node.childNodes()) {
                printLeafNodesRecursive(child);
            }
        }
    }

    private String getPrimaryStyle(final Node node) {
        if (node.parent() != null && node.parent() instanceof Element e) {
            return e.tagName();
        }
        return null;
    }

    private String getSecondaryStyle(final Node node) {
        if (node.parent() != null && node.parent() instanceof Element e) {
            if (e.parent() != null) {
                return e.parent().tagName();
            }
        }
        return null;
    }
}
