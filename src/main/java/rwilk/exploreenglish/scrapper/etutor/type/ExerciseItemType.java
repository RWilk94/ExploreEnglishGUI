package rwilk.exploreenglish.scrapper.etutor.type;

import lombok.Getter;

public enum ExerciseItemType {
  CHOICE("choice"),
  WRITING("masked-writing");

  @Getter
  private final String value;

  ExerciseItemType(final String value) {
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
