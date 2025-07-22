package rwilk.exploreenglish.model.entity.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "etutor_lesson_word", uniqueConstraints = @UniqueConstraint(columnNames = {"exercise_id", "word_id"}))
public final class EtutorLessonWord implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "position")
  private Integer position;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "word_id", nullable = false, referencedColumnName = "id")
  private EtutorWord word;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
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

  public EtutorWord getWord() {
    return word;
  }

  public void setWord(EtutorWord word) {
    this.word = word;
  }
}
