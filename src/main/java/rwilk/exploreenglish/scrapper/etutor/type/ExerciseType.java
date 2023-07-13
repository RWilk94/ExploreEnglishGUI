package rwilk.exploreenglish.scrapper.etutor.type;

import lombok.Getter;

public enum ExerciseType {

  TIP("tip"),
  PICTURES_WORDS_LIST("pictures_words_list"), // DONE
  SCREEN("screen"), // DONE
  PICTURES_LISTENING("pictures_listening"), // DONE
  PICTURES_CHOICE("pictures_choice"), // DONE
  EXERCISE("exercise"), // DONE
  MATCHING_PAIRS("matching_pairs"), // DONE
  DIALOGUE("dialogue"), // DONE
  COMIC_BOOK("comic_book"), // DONE
  GRAMMAR_NOTE("grammar_note"), // DONE
  READING("reading"), // DONE
  PICTURES_MASKED_WRITING("pictures_masked_writing"), // DONE
  SPEAKING("speaking"), // DONE
  GRAMMAR_LIST("grammar_list"),
  WRITING("writing"),
  VIDEO("video"),
  WORDS_LIST("words_list"); // can be scrapped by WordScrapper

  @Getter
  private final String value;

  ExerciseType(final String value) {
    this.value = value;
  }

  public static ExerciseType fromString(final String text) {
    for (final ExerciseType type : ExerciseType.values()) {
      if (type.getValue().equalsIgnoreCase(text)) {
        return type;
      }
    }
    return null;
  }

}
