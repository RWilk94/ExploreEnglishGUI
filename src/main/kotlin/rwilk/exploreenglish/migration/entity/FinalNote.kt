package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_notes",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "native_title")
    var nativeTitle: String? = null,

    @Type(type = "text")
    @Column(name = "native_content")
    var nativeContent: String? = null,

    @Column(name = "foreign_title")
    var foreignTitle: String? = null,

    @Type(type = "text")
    @Column(name = "foreign_content")
    var foreignContent: String? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_audio_id", referencedColumnName = "id")
    val audio: FinalMedia? = null,

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
    @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
    val exercise: FinalExercise? = null,
) : Serializable {
}
