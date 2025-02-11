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
@Table(name = "langeek_lessons")
public class LangeekLesson implements Serializable {
    @Serial
    private static final long serialVersionUID = 8870137178077958046L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "name", length = 2000)
    private String name;
    @Column(name = "description", length = 2000)
    private String description;
    @Column(name = "href", length = 256)
    private String href;
    @Column(name = "image", length = 256)
    private String image;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready")
    Boolean isReady;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
    private LangeekCourse course;
}
