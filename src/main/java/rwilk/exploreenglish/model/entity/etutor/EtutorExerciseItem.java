package rwilk.exploreenglish.model.entity.etutor;

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

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import rwilk.exploreenglish.model.LearnItemChild;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_exercise_items")
public final class EtutorExerciseItem implements Serializable, LearnItemChild {

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
  @Column(name = "question_british_sound")
  private String questionBritishSound;
  @Column(name = "question_american_sound")
  private String questionAmericanSound;
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
  @Column(name = "answer_british_sound")
  private String answerBritishSound;
  @Column(name = "answer_american_sound")
  private String answerAmericanSound;

  @Column(name = "final_answer")
  private String finalAnswer;
  @Column(name = "translation")
  private String translation;
  @Column(name = "description")
  private String description;
  @Type(type = "text")
  @Column(name = "html")
  private String html;
  @Column(name = "type")
  private String type;


/*  @Type(type = "text")
  @Column(name = "dialogue_english")
  private String dialogueEnglish;
  @Type(type = "text")
  @Column(name = "dialogue_polish")
  private String dialoguePolish;

  @Column(name = "position")
  private Integer position;*/

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;


}
