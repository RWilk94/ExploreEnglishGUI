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
    val name: String,

    @Column(name = "description", length = 2000)
    val description: String? = null,

    @Column(name = "image", length = 2000)
    val image: String? = null,

    @Column(name = "type", nullable = false)
    val type: String,

    // e.g. etutor, langeek, ewa
    @Column("source", nullable = false)
    val source: String,

    // e.g. etutor_exercise_id, langeek_exercise_id, ewa_exercise_id
    @Column("source_id", nullable = false)
    val sourceId: Long,

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
    ) @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
    val lesson: FinalLesson? = null

) : Serializable
