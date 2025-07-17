package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_lessons",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalLesson(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 2000)
    val name: String? = null,

    @Column(name = "description", length = 2000)
    val description: String? = null,

    @Column(name = "image", length = 2000)
    val image: String? = null,

    // e.g. etutor, langeek, ewa
    @Column("source", nullable = false)
    val source: String? = null,

    // e.g. etutor_lesson_id, langeek_lesson_id, ewa_lesson_id
    @Column("source_id", nullable = false)
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
    ) @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
    val course: FinalCourse? = null

) : Serializable
