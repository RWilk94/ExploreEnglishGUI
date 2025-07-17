package rwilk.exploreenglish.model.entity.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
  @Column(name = "instruction", length = 2000)
  private String instruction;
  @Column(name = "question", length = 2000)
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
  @Column(name = "translation", length = 2000)
  private String translation;
  @Column(name = "description", length = 2000)
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getInstruction() {
    return instruction;
  }

  public void setInstruction(String instruction) {
    this.instruction = instruction;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getQuestionTemplate() {
    return questionTemplate;
  }

  public void setQuestionTemplate(String questionTemplate) {
    this.questionTemplate = questionTemplate;
  }

  public String getQuestionPrimarySound() {
    return questionPrimarySound;
  }

  public void setQuestionPrimarySound(String questionPrimarySound) {
    this.questionPrimarySound = questionPrimarySound;
  }

  public String getQuestionSecondarySound() {
    return questionSecondarySound;
  }

  public void setQuestionSecondarySound(String questionSecondarySound) {
    this.questionSecondarySound = questionSecondarySound;
  }

  public String getFirstPossibleAnswer() {
    return firstPossibleAnswer;
  }

  public void setFirstPossibleAnswer(String firstPossibleAnswer) {
    this.firstPossibleAnswer = firstPossibleAnswer;
  }

  public String getSecondPossibleAnswer() {
    return secondPossibleAnswer;
  }

  public void setSecondPossibleAnswer(String secondPossibleAnswer) {
    this.secondPossibleAnswer = secondPossibleAnswer;
  }

  public String getThirdPossibleAnswer() {
    return thirdPossibleAnswer;
  }

  public void setThirdPossibleAnswer(String thirdPossibleAnswer) {
    this.thirdPossibleAnswer = thirdPossibleAnswer;
  }

  public String getForthPossibleAnswer() {
    return forthPossibleAnswer;
  }

  public void setForthPossibleAnswer(String forthPossibleAnswer) {
    this.forthPossibleAnswer = forthPossibleAnswer;
  }

  public String getCorrectAnswer() {
    return correctAnswer;
  }

  public void setCorrectAnswer(String correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public String getAnswerPrimarySound() {
    return answerPrimarySound;
  }

  public void setAnswerPrimarySound(String answerPrimarySound) {
    this.answerPrimarySound = answerPrimarySound;
  }

  public String getAnswerSecondarySound() {
    return answerSecondarySound;
  }

  public void setAnswerSecondarySound(String answerSecondarySound) {
    this.answerSecondarySound = answerSecondarySound;
  }

  public String getFinalAnswer() {
    return finalAnswer;
  }

  public void setFinalAnswer(String finalAnswer) {
    this.finalAnswer = finalAnswer;
  }

  public String getTranslation() {
    return translation;
  }

  public void setTranslation(String translation) {
    this.translation = translation;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public EtutorExercise getExercise() {
    return exercise;
  }

  public void setExercise(EtutorExercise exercise) {
    this.exercise = exercise;
  }
}
