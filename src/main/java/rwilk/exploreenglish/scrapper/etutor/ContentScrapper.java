package rwilk.exploreenglish.scrapper.etutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.content.*;
import rwilk.exploreenglish.scrapper.etutor.content_v2.note.NoteScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ContentScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final WordScrapper wordScrapper;
  private final NoteScrapperV2 noteScrapper;
  private final PictureListeningScrapper pictureListeningScrapper;
  private final PictureChoiceScrapper pictureChoiceScrapper;
  private final ExerciseItemScrapper exerciseItemScrapper;
  private final MatchingPairsScrapper matchingPairsScrapper;
  private final DialogScrapper dialogScrapper;
  private final ComicBookScrapper comicBookScrapper;
  private final ReadingScrapper readingScrapper;
  private final PicturesMaskedWritingScrapper picturesMaskedWritingScrapper;
  private final SpeakingScrapper speakingScrapper;
  private final GrammarListScrapper grammarListScrapper;

  public ContentScrapper(final EtutorExerciseRepository etutorExerciseRepository, final WordScrapper wordScrapper,
                         final NoteScrapperV2 noteScrapper, final PictureListeningScrapper pictureListeningScrapper,
                         final PictureChoiceScrapper pictureChoiceScrapper,
                         final ExerciseItemScrapper exerciseItemScrapper,
                         final MatchingPairsScrapper matchingPairsScrapper, final DialogScrapper dialogScrapper,
                         final ComicBookScrapper comicBookScrapper,
                         final ReadingScrapper readingScrapper,
                         final PicturesMaskedWritingScrapper picturesMaskedWritingScrapper,
                         final SpeakingScrapper speakingScrapper, final GrammarListScrapper grammarListScrapper) {
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
  }

  @Override
  public void run(final String... args) throws Exception {
//    etutorExerciseRepository.findAllByIsReady(false)
//            .forEach(this::webScrap);
  }

  private void webScrap(final EtutorExercise it) {
    log.warn("START scrapping {}", it);
    try {
      switch (Objects.requireNonNull(ExerciseType.fromString(it.getType()))) {
        case TIP,
             SCREEN,
             GRAMMAR_NOTE,
             SCREEN_CULTURAL,
             SCREEN_CULINARY,
             SCREEN_MUSIC -> noteScrapper.webScrap(it);
        case COMIC_BOOK -> comicBookScrapper.webScrap(it);
        case PICTURES_MASKED_WRITING -> picturesMaskedWritingScrapper.webScrap(it);
        case DIALOGUE -> dialogScrapper.webScrap(it);
        case MATCHING_PAIRS, MATCHING_PAIRS_GRAMMAR -> matchingPairsScrapper.webScrap(it);
        case EXERCISE -> exerciseItemScrapper.webScrapExerciseTypeExercise(it);
        case PICTURES_LISTENING -> {
          try {
            pictureListeningScrapper.webScrap(it);
          } catch (JsonProcessingException e) {
            log.error("An error occurred due to: ", e);
          }
        }
        case PICTURES_CHOICE -> pictureChoiceScrapper.webScrap(it);
        case PICTURES_WORDS_LIST, WORDS_LIST -> wordScrapper.webScrapPicturesWordsListTypeExercise(it);
        case GRAMMAR_LIST -> grammarListScrapper.webScrap(it);
        case READING -> readingScrapper.webScrap(it);

        case SPEAKING -> throw new NotImplementedException("SPEAKING temporary disabled"); // speakingScrapper.webScrap(it);
        case WRITING -> throw new NotImplementedException("WRITING hasn't supported yet."); // can be done as note
        case VIDEO -> throw new NotImplementedException("VIDEO hasn't supported yet.");
        default -> throw new NotImplementedException("default hasn't supported yet.");
      }
      log.warn("FINISH scrapping {}", it);
    } catch (NotImplementedException n) {
      log.error(ExceptionUtils.getMessage(n));
    } catch (Exception e) {
      log.error("An error occurred due to: ", e);
      // throw e;
    }
  }

}
