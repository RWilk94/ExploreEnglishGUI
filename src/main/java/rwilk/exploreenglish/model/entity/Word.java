package rwilk.exploreenglish.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import rwilk.exploreenglish.model.WordTypeEnum;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "words")
public final class Word implements Serializable {

  private static final long serialVersionUID = -4492733736602366272L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "polish_name", length = 2000)
  private String polishName;
  @Column(name = "part_of_speech")
  private String partOfSpeech;

  @Column(name = "article")
  private String article;
  //  @Column(name = "comparative")
//  private String comparative;
//  @Column(name = "superlative")
//  private String superlative;
//  @Column(name = "past_tense")
//  private String pastTense;
//  @Column(name = "past_participle")
//  private String pastParticiple;
  @Column(name = "grammar_type")
  private String grammarType;
  @Column(name = "level")
  private String level;
//  @Column(name = "plural")
//  private String plural;
//  @Column(name = "synonym")
//  private String synonym;
//  @Column(name = "opposite")
//  private String opposite;

  @Column(name = "is_ready", columnDefinition = "int default 0")
  private Integer isReady;

  @OneToMany(mappedBy = "word")
  private List<LessonWord> lessonWords;

  @OneToMany(mappedBy = "word", fetch = FetchType.EAGER)
  private List<Definition> definitions;

  @Transient
  private List<WordSentence> wordSentences;

  public String englishNamesAsString() {
    final List<String> definitions = this.definitions
      .stream()
      .filter(definition -> WordTypeEnum.WORD.toString().equals(definition.getType()))
      .map(Definition::getEnglishName)
      .toList();

    return String.join("; ", ListUtils.emptyIfNull(definitions));
  }

  public String englishSentencesAsString() {
    final List<String> sentences = this.definitions
      .stream()
      .filter(definition -> !WordTypeEnum.WORD.toString().equals(definition.getType()))
      .map(Definition::getEnglishName)
      .toList();

    return String.join("; ", ListUtils.emptyIfNull(sentences));
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[W]").append(id).append(". ");
    if (StringUtils.isNoneEmpty(article)) {
      stringBuilder.append(article).append(" ");
    }
    stringBuilder.append(englishNamesAsString()).append(" ");
    stringBuilder.append("(").append(polishName).append(")");
    if (StringUtils.isNoneEmpty(grammarType)) {
      stringBuilder.append(" [").append(grammarType).append("]");
    }
    stringBuilder.append(" ").append(englishSentencesAsString());
    return stringBuilder.toString();
  }
}
