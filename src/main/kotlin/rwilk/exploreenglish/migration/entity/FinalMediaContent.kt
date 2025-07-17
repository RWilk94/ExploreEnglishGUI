package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "final_media_content")
data class FinalMediaContent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "url", nullable = false, length = 2000)
    val url: String? = null,

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "language")
    val language: String? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "media_id", referencedColumnName = "id", nullable = false)
    var media: FinalMedia? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    val modifyDate: Date? = null,

    ) : Serializable
