package rwilk.exploreenglish.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import rwilk.exploreenglish.model.LearnItem;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "words")
public final class Word implements Serializable, LearnItem {

  private static final long serialVersionUID = -4492733736602366272L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "english_name")
  private String englishName;
  @Column(name = "american_name")
  private String americanName;
  @Column(name = "other_name")
  private String otherName;
  @Column(name = "polish_name")
  private String polishName;
  @Column(name = "part_of_speech")
  private String partOfSpeech;
  @Column(name = "sound")
  private String sound;

  @Column(name = "article")
  private String article;
  @Column(name = "comparative")
  private String comparative;
  @Column(name = "superlative")
  private String superlative;
  @Column(name = "past_tense")
  private String pastTense;
  @Column(name = "past_participle")
  private String pastParticiple;
  @Column(name = "grammar_type")
  private String grammarType;
  @Column(name = "level")
  private String level;
  @Column(name = "plural")
  private String plural;
  @Column(name = "position")
  private Integer position;
  @Column(name = "synonym")
  private String synonym;
  @Column(name = "opposite")
  private String opposite;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private Lesson lesson;

  @Transient
  private List<Sentence> sentences;

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[W]").append(id).append(". ");
    if (StringUtils.isNoneEmpty(englishName)) {
      stringBuilder.append(englishName).append("; ");
    }
    if (StringUtils.isNoneEmpty(americanName)) {
      stringBuilder.append(americanName).append("; ");
    }
    if (StringUtils.isNoneEmpty(otherName)) {
      stringBuilder.append(otherName).append("; ");
    }
    stringBuilder.append("(").append(polishName).append(")");
    return stringBuilder.toString();
  }
}
