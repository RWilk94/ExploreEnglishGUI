package rwilk.exploreenglish.model.entity.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_exercises")
public final class EtutorExercise implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "type")
  private String type;
  @Column(name = "name")
  private String name;
  @Column(name = "href")
  private String href;
  @Column(name = "image")
  private String image;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;
  @Column(name = "is_ready")
  Boolean isReady;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private EtutorLesson lesson;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public Boolean getReady() {
    return isReady;
  }

  public void setIsReady(Boolean ready) {
    isReady = ready;
  }

  public EtutorLesson getLesson() {
    return lesson;
  }

  public void setLesson(EtutorLesson lesson) {
    this.lesson = lesson;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    EtutorExercise that = (EtutorExercise) o;
    return Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(href, that.href) && Objects.equals(image, that.image) && Objects.equals(modifyDate, that.modifyDate) && Objects.equals(isReady, that.isReady) && Objects.equals(lesson, that.lesson);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, name, href, image, modifyDate, isReady, lesson);
  }
}

