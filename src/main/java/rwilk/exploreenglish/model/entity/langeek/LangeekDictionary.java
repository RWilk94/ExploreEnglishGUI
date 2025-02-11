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
@Table(name = "langeek_dictionaries", uniqueConstraints = @UniqueConstraint(columnNames = {"langeek_id", "language"}))
public class LangeekDictionary implements Serializable {
    @Serial
    private static final long serialVersionUID = -5205822470795971849L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "langeek_id")
    private Long langeekId;

    @Column(name = "language")
    private String language;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String jsonData;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
}
