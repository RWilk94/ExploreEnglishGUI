package rwilk.exploreenglish.scrapper.etutor.content.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import rwilk.exploreenglish.model.entity.etutor.EtutorNote;
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;

class NoteItemTest {

  @Test
  void shouldParseParagraph() {
    // given
    final EtutorNote etutorNote = new EtutorNote();
    final String paragraph = "<span class=\"phoneticTranscription\"><span class=\"lessonRecordedExample\">By</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/by.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/by.mp3\"></span></span></span></span> używamy, gdy chcemy wyrazić, że coś ma nastąpić do określonego momentu w przyszłości. <span class=\"phoneticTranscription\"><span class=\"lessonRecordedExample\">By</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/by.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/by.mp3\"></span></span></span></span> stosujemy także mówiąc o ostatecznym terminie, do którego coś ma zostać wykonane bądź ma się wydarzyć. <span class=\"phoneticTranscription\"><span class=\"lessonRecordedExample\">By</span>&nbsp;<span class=\"recordingsAndTranscriptions\"><span class=\"en-GB hasRecording\" title=\"British English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en/mp3/by.mp3\"></span></span><span class=\"en-US hasRecording\" title=\"American English\"><span class=\"audioIcon icon-sound dontprint soundOnClick\" tabindex=\"-1\" data-audio-url=\"/images-common/en-ame/mp3/by.mp3\"></span></span></span></span> w pewien sposób wyraża zobowiązanie, które ma być wypełnione i skończone do konkretnego momentu: roku, dnia, godziny, pory.";

    // when
    final List<EtutorNoteItem> etutorNoteItems = NoteItem.webScrap(etutorNote, paragraph);

    // then
    assertThat(etutorNoteItems).hasSize(7);
    assertThat(etutorNoteItems.get(0)).isEqualTo(fixture(etutorNote));
    assertThat(etutorNoteItems.get(1)).isEqualTo(fixture(etutorNote, "używamy, gdy chcemy wyrazić, że coś ma nastąpić do określonego momentu w przyszłości. "));
    assertThat(etutorNoteItems.get(2)).isEqualTo(fixture(etutorNote));
    assertThat(etutorNoteItems.get(3)).isEqualTo(fixture(etutorNote, "stosujemy także mówiąc o ostatecznym terminie, do którego coś ma zostać wykonane bądź ma się wydarzyć. "));
    assertThat(etutorNoteItems.get(4)).isEqualTo(fixture(etutorNote));
    assertThat(etutorNoteItems.get(5)).isEqualTo(fixture(etutorNote, " w pewien sposób wyraża zobowiązanie, które ma być wypełnione i skończone do konkretnego momentu: roku, dnia, godziny, pory."));
    assertThat(etutorNoteItems.get(6)).isEqualTo(fixture(etutorNote, "\n"));
  }

  private EtutorNoteItem fixture(final EtutorNote etutorNote) {
    return EtutorNoteItem.builder()
      .britishSound("https://www.etutor.pl/images-common/en/mp3/by.mp3")
      .americanSound("https://www.etutor.pl/images-common/en-ame/mp3/by.mp3")
      .plainText("By ")
      .note(etutorNote)
      .build();
  }

  private EtutorNoteItem fixture(final EtutorNote etutorNote, final String text) {
    return EtutorNoteItem.builder()
      .plainText(text)
      .note(etutorNote)
      .build();
  }

}