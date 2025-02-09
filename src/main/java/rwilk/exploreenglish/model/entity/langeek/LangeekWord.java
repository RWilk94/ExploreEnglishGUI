package rwilk.exploreenglish.model.entity.langeek;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "langeek_words")
public class LangeekWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "native_translation", length = 2000)
    private String nativeTranslation;
    @Column(name = "additional_information")
    private String additionalInformation;
    @Column(name = "part_of_speech")
    private String partOfSpeech;
    @Column(name = "article")
    private String article;
    @Column(name = "grammar_type")
    private String grammarType;
    @Column(name = "image")
    private String image;
    @Type(type = "text")
    @Column(name = "html")
    private String html;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
    private LangeekExercise exercise;

    @ToString.Exclude
    @OneToMany(mappedBy = "word", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<LangeekExerciseWord> etutorLessonWords = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "word", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<LangeekDefinition> definitions = new ArrayList<>();

}
