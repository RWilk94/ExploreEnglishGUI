package rwilk.exploreenglish.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exercise_rows")
public final class ExerciseRow implements Serializable {

  private static final long serialVersionUID = 2360065403395688983L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "question")
  private String question;
  @Column(name = "correct_answer")
  private String correctAnswer;
  @Column(name = "correct_answer_after_choice")
  private String correctAnswerAfterChoice;
  @Column(name = "first_possible_answer")
  private String firstPossibleAnswer;
  @Column(name = "second_possible_answer")
  private String secondPossibleAnswer;
  @Column(name = "third_possible_answer")
  private String thirdPossibleAnswer;
  @Column(name = "forth_possible_answer")
  private String forthPossibleAnswer;

  @Column(name = "dialogue_english")
  private String dialogueEnglish;
  @Column(name = "dialogue_polish")
  private String dialoguePolish;
  @Column(name = "description")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private Exercise exercise;

}
