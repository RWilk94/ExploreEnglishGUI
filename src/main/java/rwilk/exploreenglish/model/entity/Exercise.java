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
@Table(name = "exercises")
public final class Exercise implements Serializable {

  private static final long serialVersionUID = 6210570119816267463L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "type")
  private String type;
  @Column(name = "position")
  private Integer position;
  @Column(name = "name")
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private Lesson lesson;

  @Transient
  private List<ExerciseRow> exerciseRows;

}
