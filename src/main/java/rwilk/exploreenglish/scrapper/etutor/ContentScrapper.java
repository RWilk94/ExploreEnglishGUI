package rwilk.exploreenglish.scrapper.etutor;

import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.scrapper.etutor.content.ComicBookScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.DialogScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.ExerciseItemScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.GrammarListScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.GrammarNoteScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.MatchingPairsScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.NoteScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.PictureChoiceScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.PictureListeningScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.PicturesMaskedWritingScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.ReadingScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.SpeakingScrapper;
import rwilk.exploreenglish.scrapper.etutor.content.WordScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class ContentScrapper implements CommandLineRunner {

  private final EtutorExerciseRepository etutorExerciseRepository;
  private final WordScrapper wordScrapper;
  private final NoteScrapper noteScrapper;
  private final PictureListeningScrapper pictureListeningScrapper;
  private final PictureChoiceScrapper pictureChoiceScrapper;
  private final ExerciseItemScrapper exerciseItemScrapper;
  private final MatchingPairsScrapper matchingPairsScrapper;
  private final DialogScrapper dialogScrapper;
  private final ComicBookScrapper comicBookScrapper;
  private final GrammarNoteScrapper grammarNoteScrapper;
  private final ReadingScrapper readingScrapper;
  private final PicturesMaskedWritingScrapper picturesMaskedWritingScrapper;
  private final SpeakingScrapper speakingScrapper;
  private final GrammarListScrapper grammarListScrapper;

  public ContentScrapper(final EtutorExerciseRepository etutorExerciseRepository, final WordScrapper wordScrapper,
                         final NoteScrapper noteScrapper, final PictureListeningScrapper pictureListeningScrapper,
                         final PictureChoiceScrapper pictureChoiceScrapper,
                         final ExerciseItemScrapper exerciseItemScrapper,
                         final MatchingPairsScrapper matchingPairsScrapper, final DialogScrapper dialogScrapper,
                         final ComicBookScrapper comicBookScrapper, final GrammarNoteScrapper grammarNoteScrapper,
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
    this.grammarNoteScrapper = grammarNoteScrapper;
    this.readingScrapper = readingScrapper;
    this.picturesMaskedWritingScrapper = picturesMaskedWritingScrapper;
    this.speakingScrapper = speakingScrapper;
    this.grammarListScrapper = grammarListScrapper;
  }

  @Override
  public void run(final String... args) throws Exception {


  }

  private void webScrap() {
    etutorExerciseRepository.findAllByIsReady(false)
      .forEach(it -> {
                 switch (Objects.requireNonNull(ExerciseType.fromString(it.getType()))) {
                   case TIP -> System.out.println();
                   case PICTURES_WORDS_LIST -> wordScrapper.webScrapPicturesWordsListTypeExercise(it);
                   case SCREEN -> noteScrapper.webScrap(it);
                   case PICTURES_LISTENING -> {
                     try {
                       pictureListeningScrapper.webScrap(it);
                     } catch (JsonProcessingException e) {
                       System.err.println(e);
                     }
                   }
                   case PICTURES_CHOICE -> pictureChoiceScrapper.webScrap(it);
                   case EXERCISE -> exerciseItemScrapper.webScrapExerciseTypeExercise(it);
                   case MATCHING_PAIRS -> matchingPairsScrapper.webScrap(it);
                   case DIALOGUE -> dialogScrapper.webScrap(it);
                   case COMIC_BOOK -> comicBookScrapper.webScrap(it);
                   case GRAMMAR_NOTE -> grammarNoteScrapper.webScrap(it);
                   case READING -> readingScrapper.webScrap(it);
                   case PICTURES_MASKED_WRITING -> picturesMaskedWritingScrapper.webScrap(it);
                   case SPEAKING -> speakingScrapper.webScrap(it);
                   case GRAMMAR_LIST -> grammarListScrapper.webScrap(it);
                   case WRITING -> System.out.println("TODO");
                   case VIDEO -> System.out.println("TODO");
                   case WORDS_LIST -> wordScrapper.webScrapPicturesWordsListTypeExercise(it);
                   default -> System.err.println();
                 }
               }
      );
  }

}
