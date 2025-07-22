package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_words",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalWord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "native_translation", nullable = false, length = 2000)
    val nativeTranslation: String? = null,

    @Column(name = "additional_information")
    val additionalInformation: String? = null,

    @Column(name = "part_of_speech")
    val partOfSpeech: String? = null,

    @Column(name = "article")
    val article: String? = null,

    @Column(name = "grammar_type")
    val grammarType: String? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_image_id", referencedColumnName = "id")
    val image: FinalMedia? = null,

    @Column(name = "source", nullable = false)
    val source: String? = null,

    @Column(name = "source_id", nullable = false)
    val sourceId: Long? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    val modifyDate: Date? = null,

    @OneToMany(
        mappedBy = "word",
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
            CascadeType.REMOVE
        ]
    )
    var definitions: List<FinalDefinition> = emptyList(),

    @OneToMany(
        mappedBy = "word",
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
            CascadeType.REMOVE
        ]
    )
    val exerciseWords: List<FinalExerciseWord> = emptyList(),

): Serializable {
}
