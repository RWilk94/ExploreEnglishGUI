package rwilk.exploreenglish.model.entity.ewa;

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
@Table(name = "ewa_lessons")
public class EwaLesson implements Serializable {
    @Serial
    private static final long serialVersionUID = -703675527637784682L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "ewa_id")
    private String ewaId;
    @Column(name = "channel")
    private Integer channel;
    @Column(name = "is_adult")
    private Boolean isAdult;
    @Column(name = "number")
    private Integer number;
    @Column(name = "title")
    private String title;
    @Column(name = "image_id")
    private String imageId;
    @Column(name = "image_s")
    private String imageS;
    @Column(name = "image_m")
    private String imageM;
    @Column(name = "image_l")
    private String imageL;
    @Column(name = "image_xl")
    private String imageXl;
    @Column(name = "course_role")
    private String courseRole;
    @Column(name = "background_image_")
    private String backgroundImage;
    @Column(name = "description")
    private String description;
    @Column(name = "goal_description")
    private String goalDescription;

    @Lob
    @Column(name = "json_data", columnDefinition = "LONGTEXT")
    private String jsonData;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready")
    Boolean isReady;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
    private EwaCourse course;

}
