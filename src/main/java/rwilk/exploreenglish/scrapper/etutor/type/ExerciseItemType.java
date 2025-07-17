package rwilk.exploreenglish.scrapper.etutor.type;

import lombok.Getter;

public enum ExerciseItemType {
  CHOICE("choice"),
  WRITING("masked-writing"),
  CLOZE("cloze"),
  PICTURES_LISTENING("pictures-listening"),
  PICTURES_CHOICE("pictures-choice"),
  MATCHING_PAIRS("matching_pairs"),
  PICTURES_MASKED_WRITING("pictures_masked_writing");

  @Getter
  private final String value;

  ExerciseItemType(final String value) {
    this.value = value;
  }

  public static ExerciseItemType fromString(final String text) {
    for (final ExerciseItemType type : ExerciseItemType.values()) {
      if (type.getValue().equalsIgnoreCase(text)) {
        return type;
      }
    }
    return null;
  }

}
