package rwilk.exploreenglish.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons", uniqueConstraints = @UniqueConstraint(columnNames={"id", "english_name", "polish_name"}))
public final class Lesson implements Serializable {

  private static final long serialVersionUID = 4141601594703098085L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "english_name")
  private String englishName;
  @Column(name = "polish_name")
  private String polishName;
  @Column(name = "image")
  private String image;
  @Column(name = "position")
  private Integer position;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
  private Course course;

  @Transient
  // One to Many
  private List<LessonWord> lessonWords;

  @Override
  public String toString() {
    return id + ". " + englishName + " (" + polishName + ") [Course: " + course + "]";
  }
}
