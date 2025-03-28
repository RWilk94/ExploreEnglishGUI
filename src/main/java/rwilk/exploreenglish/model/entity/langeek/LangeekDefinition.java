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
@EqualsAndHashCode
@Entity
@Table(name = "langeek_definitions")
public class LangeekDefinition implements Serializable {
    @Serial
    private static final long serialVersionUID = -4644235291112900779L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "type")
    private String type;
    @Column(name = "foreign_translation", length = 2000)
    private String foreignTranslation;
    @Column(name = "additional_information", length = 2000)
    private String additionalInformation;
    @Column(name = "primary_sound", length = 2000)
    private String primarySound;   // British Sound
    @Column(name = "secondary_sound", length = 2000)
    private String secondarySound;   // American Sound
    @Column(name = "language")
    private String language;
    @Column(name = "main_word_indexes")
    private String mainWordIndexes;

    @ToString.Exclude
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "word_id", nullable = false, referencedColumnName = "id", insertable = true, updatable = true)
    private LangeekWord word;

}
