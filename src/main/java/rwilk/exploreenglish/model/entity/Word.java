package rwilk.exploreenglish.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
  @Column(name = "past_participle")
  private String pastParticiple;
  @Column(name = "past_tense")
  private String pastTense;
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

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private Lesson lesson;

  @Transient
  private List<Sentence> sentences;

}
