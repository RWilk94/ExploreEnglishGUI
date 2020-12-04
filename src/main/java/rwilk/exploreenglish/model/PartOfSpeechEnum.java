package rwilk.exploreenglish.model;

public enum PartOfSpeechEnum {

  RZECZOWNIK("rzeczownik"),
  CZASOWNIK("czasownik"),
  PRZYMIOTNIK("przymiotnik"),
  PRZYSLOWEK("przysłówek"),
  PHRASAL_VERB("phrasal verb"),
  WYRAZENIE("wyrażenie"),
  IDIOM("idiom"),
  EMPTY("");

  private String value;

  PartOfSpeechEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
