package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_courses",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalCourse(
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

    @Column(name = "language", nullable = false)
    val language: String? = null,

    // e.g. etutor, langeek, ewa
    @Column(name = "source", nullable = false)
    val source: String? = null,

    // e.g. etutor_course_id, langeek_course_id, ewa_course_id
    @Column(name = "source_id", nullable = false)
    val sourceId: Long? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    var modifyDate: Date? = null,

    ) : Serializable
