package rwilk.exploreenglish.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames={"english_name", "polish_name"}))
public final class Course implements Serializable {

  private static final long serialVersionUID = -301812296339879988L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "english_name", length = 2000)
  private String englishName;
  @Column(name = "polish_name", length = 2000)
  private String polishName;
  @Column(name = "image")
  private String image;
  @Column(name = "position")
  private Integer position;

  @Transient
  private List<Lesson> lessons;

  @Override
  public String toString() {
    return id + ". " + englishName + " (" + polishName + ")";
  }
}
