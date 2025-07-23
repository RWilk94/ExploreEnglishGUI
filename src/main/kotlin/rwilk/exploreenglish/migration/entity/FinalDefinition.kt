package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_definitions",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalDefinition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "foreign_translation", nullable = false, length = 2000)
    val foreignTranslation: String? = null,

    @Column(name = "additional_information", length = 2000)
    val additionalInformation: String? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_audio_id", referencedColumnName = "id")
    val audio: FinalMedia? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_video_id", referencedColumnName = "id")
    val video: FinalMedia? = null,

    @Column(name = "source", nullable = false)
    val source: String? = null,

    @Column(name = "source_id", nullable = false)
    val sourceId: Long? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    val modifyDate: Date? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    val word: FinalWord? = null,

) : Serializable {

}
