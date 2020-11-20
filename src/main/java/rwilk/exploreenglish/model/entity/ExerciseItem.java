package rwilk.exploreenglish.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exercise_item")
public final class ExerciseItem implements Serializable, LearnItemChildren {

  private static final long serialVersionUID = 2360065403395688983L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "question")
  private String question;
  @Column(name = "correct_answer")
  private String correctAnswer;
  @Column(name = "final_answer")
  private String finalAnswer;
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

  @Column(name = "position")
  private Integer position;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private Exercise exercise;

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[E]").append(id).append(". ");
    if (exercise.getType().equals("DIALOGUE")) {
      stringBuilder.append(dialogueEnglish).append(" (").append(dialoguePolish).append(")");
    } else {
      if (StringUtils.isNoneEmpty(question)) {
        stringBuilder.append(question).append("; ");
      }
      if (StringUtils.isNoneEmpty(correctAnswer)) {
        stringBuilder.append(correctAnswer).append("; ");
      }
    }
    return stringBuilder.toString();
  }

}
