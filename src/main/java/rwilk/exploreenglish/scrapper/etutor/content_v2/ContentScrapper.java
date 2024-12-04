package rwilk.exploreenglish.scrapper.etutor.content_v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.BaseScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.*;
import rwilk.exploreenglish.scrapper.etutor.content_v2.grammarlist.GrammarListScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.content_v2.note.NoteScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.content_v2.reading.ReadingScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.content_v2.speaking.SpeakingScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.content_v2.video.VideoScrapper;
import rwilk.exploreenglish.scrapper.etutor.content_v2.writing.WritingScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.Objects;

@Slf4j
@Component
public class ContentScrapper extends BaseScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final WordScrapper wordScrapper;
  private final NoteScrapperV2 noteScrapper;
  private final PictureListeningScrapper pictureListeningScrapper;
  private final PictureChoiceScrapper pictureChoiceScrapper;
  private final ExerciseItemScrapper exerciseItemScrapper;
  private final MatchingPairsScrapper matchingPairsScrapper;
  private final DialogScrapper dialogScrapper;
  private final ComicBookScrapper comicBookScrapper;
  private final ReadingScrapperV2 readingScrapper;
  private final PicturesMaskedWritingScrapper picturesMaskedWritingScrapper;
  private final SpeakingScrapperV2 speakingScrapper;
  private final GrammarListScrapperV2 grammarListScrapper;
  private final WritingScrapperV2 writingScrapper;
  private final VideoScrapper videoScrapper;

  public ContentScrapper(final EtutorExerciseRepository etutorExerciseRepository, final WordScrapper wordScrapper,
                         final NoteScrapperV2 noteScrapper, final PictureListeningScrapper pictureListeningScrapper,
                         final PictureChoiceScrapper pictureChoiceScrapper,
                         final ExerciseItemScrapper exerciseItemScrapper,
                         final MatchingPairsScrapper matchingPairsScrapper, final DialogScrapper dialogScrapper,
                         final ComicBookScrapper comicBookScrapper,
                         final ReadingScrapperV2 readingScrapper,
                         final PicturesMaskedWritingScrapper picturesMaskedWritingScrapper,
                         final SpeakingScrapperV2 speakingScrapper, final GrammarListScrapperV2 grammarListScrapper,
                         final WritingScrapperV2 writingScrapper, final VideoScrapper videoScrapper) {
    this.etutorExerciseRepository = etutorExerciseRepository;
    this.wordScrapper = wordScrapper;
    this.noteScrapper = noteScrapper;
    this.pictureListeningScrapper = pictureListeningScrapper;
    this.pictureChoiceScrapper = pictureChoiceScrapper;
    this.exerciseItemScrapper = exerciseItemScrapper;
    this.matchingPairsScrapper = matchingPairsScrapper;
    this.dialogScrapper = dialogScrapper;
    this.comicBookScrapper = comicBookScrapper;
    this.readingScrapper = readingScrapper;
    this.picturesMaskedWritingScrapper = picturesMaskedWritingScrapper;
    this.speakingScrapper = speakingScrapper;
    this.grammarListScrapper = grammarListScrapper;
    this.writingScrapper = writingScrapper;
    this.videoScrapper = videoScrapper;
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByIsReady(false)
//            .stream()
//            .filter(it -> it.getId() > 1585L)
//            .forEach(this::webScrap);
  }

  public void webScrap(final EtutorExercise it) {
    log.info("START scrapping {}", it);

    final WebDriver driver = super.getDriver();

    try {
      switch (Objects.requireNonNull(ExerciseType.fromString(it.getType()))) {
        case TIP,
             SCREEN,
             GRAMMAR_NOTE,
             SCREEN_CULTURAL,
             SCREEN_CULINARY,
             SCREEN_MUSIC -> noteScrapper.webScrap(it, driver); // DONE in UI
        case COMIC_BOOK -> comicBookScrapper.webScrap(it, driver); // DONE in UI
        case PICTURES_MASKED_WRITING -> picturesMaskedWritingScrapper.webScrap(it, driver); // DONE in UI
        case DIALOG -> dialogScrapper.webScrap(it, driver); // DONE in UI
        case MATCHING_PAIRS, MATCHING_PAIRS_GRAMMAR -> matchingPairsScrapper.webScrap(it, driver); // DONE in UI
        case EXERCISE -> exerciseItemScrapper.webScrapExerciseTypeExercise(it, driver); // DONE in UI
        case PICTURES_LISTENING -> {
          try {
            pictureListeningScrapper.webScrap(it, driver);
          } catch (JsonProcessingException e) {
            log.error("An error occurred due to: ", e);
          }
        } // DONE in UI
        case PICTURES_CHOICE -> pictureChoiceScrapper.webScrap(it, driver); // DONE in UI
        case PICTURES_WORDS_LIST, WORDS_LIST -> wordScrapper.webScrapPicturesWordsListTypeExercise(it, driver); // DONE in UI
        case GRAMMAR_LIST -> grammarListScrapper.webScrap(it, driver); // DONE IN UI
        case READING -> readingScrapper.webScrap(it, driver); // DONE IN UI
        case SPEAKING -> speakingScrapper.webScrap(it, driver);
        case WRITING -> writingScrapper.webScrap(it, driver); // DONE in UI
        case VIDEO -> videoScrapper.webScrap(it, driver); // DONE in UI
        default -> throw new NotImplementedException("default hasn't supported yet.");
      }
      log.info("FINISH scrapping {}", it);
    } catch (NotImplementedException n) {
      log.error(ExceptionUtils.getMessage(n));
    } catch (Exception e) {
      log.error("An error occurred due to: ", e);
      // throw e;
    }
    driver.quit();
  }

}
