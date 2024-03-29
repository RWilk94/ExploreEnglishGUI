package rwilk.exploreenglish.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import rwilk.exploreenglish.model.WordTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "terms")
public final class Term implements Serializable {
  private static final long serialVersionUID = 5811769653353702806L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "english_name", length = 2000)
  private String englishName;
  @Column(name = "american_name", length = 2000)
  private String americanName;
  @Column(name = "other_name", length = 2000)
  private String otherName;
  @Column(name = "polish_name", length = 2000)
  private String polishName;
  @Column(name = "comparative")
  private String comparative;
  @Column(name = "superlative")
  private String superlative;
  @Column(name = "past_tense")
  private String pastTense;
  @Column(name = "past_participle")
  private String pastParticiple;
  @Column(name = "plural")
  private String plural;
  @Column(name = "synonym")
  private String synonym;
  @Column(name = "part_of_speech")
  private String partOfSpeech;
  @Column(name = "popularity")
  private String popularity;

  @Column(name = "english_sentence", length = 2000)
  private String englishSentence;
  @Column(name = "polish_sentence", length = 2000)
  private String polishSentence;

  @Column(name = "source")
  private String source;
  @Column(name = "category")
  private String category;

  @Column(name = "is_added")
  private Boolean isAdded;
  @Column(name = "is_ignored")
  private Boolean isIgnored;

  public Term(final Word word) {
    this.englishName = extract(word, WordTypeEnum.WORD);
    this.polishName = word.getPolishName();
    this.comparative = extract(word, WordTypeEnum.COMPARATIVE);
    this.superlative = extract(word, WordTypeEnum.SUPERLATIVE);
    this.pastTense = extract(word, WordTypeEnum.PAST_TENSE);
    this.pastParticiple = extract(word, WordTypeEnum.PAST_PARTICIPLE);
    this.plural = extract(word, WordTypeEnum.PLURAL);
    this.synonym = extract(word, WordTypeEnum.SYNONYM);
    this.partOfSpeech = word.getPartOfSpeech();
    this.isAdded = true;
  }

  private String extract(final Word word, final WordTypeEnum wordType) {
    return String.join(";", ListUtils.emptyIfNull(word.getDefinitions()
                                               .stream()
                                               .filter(wordSound -> wordType.toString().equals(wordSound.getType()))
                                               .map(Definition::getEnglishName)
                                               .toList()));
  }
}
