package rwilk.exploreenglish.model.entity.release;

import java.io.Serializable;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "release_words")
public class ReleaseWord implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "polish_name", length = 2000)
  private String plName;
  @Column(name = "english_name", length = 2000)
  private String enName;
  @Column(name = "american_name", length = 2000)
  private String usName;
  @Column(name = "other_name", length = 2000)
  private String otherNames;
  @Column(name = "part_of_speech")
  private String partOfSpeech;
  @Column(name = "article")
  private String article;
  @Column(name = "grammar_type")
  private String grammarType;
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
  @Column(name = "level")
  private String level;
  @Column(name = "source")
  private String source;
  @Column(name = "sound", length = 2000)
  private String sound;
  @Column(name = "position")
  private Long position;
  @Column(name = "lesson_id")
  private Long lessonId;

  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
  private ReleaseLesson lesson;

}