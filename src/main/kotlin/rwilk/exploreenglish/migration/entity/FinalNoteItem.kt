package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_note_items",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalNoteItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Type(type = "text")
    @Column(name = "example")
    var example: String? = null,

    @Type(type = "text")
    @Column(name = "plain_text")
    var plainText: String? = null,

    @Column(name = "primary_style")
    var primaryStyle: String? = null,

    @Column(name = "secondary_style")
    var secondaryStyle: String? = null,

    @Column(name = "additional")
    var additional: String? = null,

    @Column(name = "language_type")
    var languageType: String? = null,

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
    @JoinColumn(name = "media_audio_id", referencedColumnName = "id")
    val audio: FinalMedia? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_image_id", referencedColumnName = "id")
    val image: FinalMedia? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "note_id", nullable = false, referencedColumnName = "id")
    val note: FinalNote? = null,
) : Serializable {
}
