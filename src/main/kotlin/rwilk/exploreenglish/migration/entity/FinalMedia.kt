package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "final_media")
data class FinalMedia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "text", length = 2000)
    val text: String? = null,

    @Column(name = "type", nullable = false)
    val type: String? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    val modifyDate: Date? = null,

    @OneToMany(
        mappedBy = "media",
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
            CascadeType.REMOVE
        ]
    )
    val mediaContents: MutableList<FinalMediaContent> = mutableListOf(),
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FinalMedia

        if (id != other.id) return false
        if (text != other.text) return false
        if (type != other.type) return false
        if (modifyDate != other.modifyDate) return false
        if (mediaContents != other.mediaContents) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (modifyDate?.hashCode() ?: 0)
        result = 31 * result + mediaContents.hashCode()
        return result
    }

    override fun toString(): String {
        return "FinalMedia(id=$id, text=$text, type=$type, modifyDate=$modifyDate)"
    }


}
