package rwilk.exploreenglish.scrapper.speaklanguages;

public enum SpeakLanguagesType {

  WORDS(0),
  PHRASES(1);

  private int value;

  SpeakLanguagesType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
