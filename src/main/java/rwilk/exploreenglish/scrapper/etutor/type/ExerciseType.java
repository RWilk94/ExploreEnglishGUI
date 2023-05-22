package rwilk.exploreenglish.scrapper.etutor.type;

import lombok.Getter;

public enum ExerciseType {

  TIP("tip"),
  PICTURES_WORDS_LIST("pictures_words_list"), // DONE
  SCREEN("screen"), // DONE
  PICTURES_LISTENING("pictures_listening"),
  PICTURES_CHOICE("pictures_choice"),
  EXERCISE("exercise"),
  MATCHING_PAIRS("matching_pairs"),
  DIALOGUE("dialogue"),
  COMIC_BOOK("comic_book"),
  GRAMMAR_NOTE("grammar_note"),
  READING("reading"),
  PICTURES_MASKED_WRITING("pictures_masked_writing"),
  SPEAKING("speaking"),
  GRAMMAR_LIST("grammar_list"),
  WRITING("writing"),
  VIDEO("video"),
  WORDS_LIST("words_list");

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