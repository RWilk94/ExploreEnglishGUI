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
@Table(name = "ewa_courses")
public class EwaCourse implements Serializable {
    @Serial
    private static final long serialVersionUID = 5107043430339822308L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "ewa_id")
    private String ewaId;
    @Column(name = "name", length = 2000)
    private String name;
    @Column(name = "description", length = 2000)
    private String description;
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

    @Lob
    @Column(name = "json_data", columnDefinition = "LONGTEXT")
    private String jsonData;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready", columnDefinition = "int default 0")
    Boolean isReady;
}
