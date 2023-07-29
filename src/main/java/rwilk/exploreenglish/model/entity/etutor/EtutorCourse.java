package rwilk.exploreenglish.model.entity.etutor;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_courses")
public class EtutorCourse implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "name", length = 2000)
  private String name;
  @Column(name = "description", length = 2000)
  private String description;
  @Column(name = "href", length = 2000)
  private String href;
  @Column(name = "image", length = 256)
  private String image;
  @Column(name = "language")
  private String language;
}
