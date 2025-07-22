package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_dialog_items",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalDialogItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "type")
    var type: String? = null,

    @Type(type = "text")
    @Column(name = "dialog_foreign")
    var dialogForeign: String? = null,

    @Type(type = "text")
    @Column(name = "dialog_native")
    var dialogNative: String? = null,

    @Column(name = "face_image")
    var faceImage: String? = null,

    @Column(name = "comic_image")
    var comicImage: String? = null,

    @Column(name = "sound_seek_second")
    var soundSeekSecond: BigDecimal? = null,

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
