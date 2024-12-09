package rwilk.exploreenglish.model.entity.etutor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_exercise_items")
public final class EtutorExerciseItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "instruction")
  private String instruction;
  @Column(name = "question")
  private String question;
  @Column(name = "question_template")
  private String questionTemplate;
  @Column(name = "question_primary_sound")
  private String questionPrimarySound; // British English
  @Column(name = "question_secondary_sound")
  private String questionSecondarySound; // American English
  @Column(name = "first_possible_answer")
  private String firstPossibleAnswer;
  @Column(name = "second_possible_answer")
  private String secondPossibleAnswer;
  @Column(name = "third_possible_answer")
  private String thirdPossibleAnswer;
  @Column(name = "forth_possible_answer")
  private String forthPossibleAnswer;
  @Column(name = "correct_answer")
  private String correctAnswer;
  @Column(name = "answer_primary_sound")
  private String answerPrimarySound; // British English
  @Column(name = "answer_secondary_sound")
  private String answerSecondarySound; // American English

  @Column(name = "final_answer", length = 2000)
  private String finalAnswer;
  @Column(name = "translation")
  private String translation;
  @Column(name = "description")
  private String description;
  @Column(name = "image")
  private String image;
  @Type(type = "text")
  @Column(name = "html")
  private String html;
  @Column(name = "type")
  private String type;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;

}
