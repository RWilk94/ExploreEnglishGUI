package rwilk.exploreenglish.model.entity.langeek;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EqualsAndHashCode
@Table(name = "langeek_exercises")
public class LangeekExercise implements Serializable {
    @Serial
    private static final long serialVersionUID = 3033250878364052487L;

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
    @Column(name = "description", length = 2000)
    private String description;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready")
    Boolean isReady;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
    private LangeekLesson lesson;
}
