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
@Entity
@EqualsAndHashCode
@Table(name = "langeek_exercise_words", uniqueConstraints = @UniqueConstraint(columnNames = {"exercise_id", "word_id"}))
public class LangeekExerciseWord implements Serializable {
    @Serial
    private static final long serialVersionUID = -5257284171275474019L;

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
    private LangeekExercise exercise;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "word_id", nullable = false, referencedColumnName = "id")
    private LangeekWord word;

}
