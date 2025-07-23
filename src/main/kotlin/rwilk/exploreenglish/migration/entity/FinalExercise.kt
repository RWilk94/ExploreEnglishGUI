package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_exercises",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalExercise(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 2000)
    val name: String? = null,

    @Column(name = "description", length = 2000)
    val description: String? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_image_id", referencedColumnName = "id")
    val image: FinalMedia? = null,

    @Column(name = "type", nullable = false)
    val type: String? = null,

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
    @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
    val lesson: FinalLesson? = null

) : Serializable
