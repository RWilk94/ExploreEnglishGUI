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
@Table(name = "courses")
public final class Course implements Serializable {

  private static final long serialVersionUID = -301812296339879988L;
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

  @Transient
  private List<Lesson> lessons;

  @Override
  public String toString() {
    return id + ". " + englishName + " (" + polishName + ")";
  }
}
