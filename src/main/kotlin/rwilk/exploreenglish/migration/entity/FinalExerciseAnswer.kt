package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_exercise_answers"
)
data class FinalExerciseAnswer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "type", nullable = false)
    val type: String? = null,

    @Column(name = "answer", nullable = false, length = 2000)
    val answer: String? = null,

    @Column(name = "translation", length = 2000)
    val translation: String? = null,

    @Column(name = "is_correct", nullable = false)
    val isCorrect: Boolean? = null,

    // e.g. etutor, langeek, ewa
    @Column(name = "source", nullable = false)
    val source: String? = null,

    // e.g. etutor_exercise_item_id, langeek_exercise_item_id, ewa_exercise_item_id
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
    @JoinColumn(name = "media_sound_id", referencedColumnName = "id")
    val sound: FinalMedia? = null,

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
    @JoinColumn(name = "media_video_id", referencedColumnName = "id")
    val video: FinalMedia? = null,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
        ]
    )
    @JoinColumn(name = "exercise_question_id", nullable = false, referencedColumnName = "id")
    var question: FinalExerciseQuestion? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FinalExerciseAnswer

        if (id != other.id) return false
        if (isCorrect != other.isCorrect) return false
        if (sourceId != other.sourceId) return false
        if (type != other.type) return false
        if (answer != other.answer) return false
        if (translation != other.translation) return false
        if (source != other.source) return false
        if (modifyDate != other.modifyDate) return false
        if (sound != other.sound) return false
        if (image != other.image) return false
        if (video != other.video) return false
        if (question != other.question) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (isCorrect?.hashCode() ?: 0)
        result = 31 * result + (sourceId?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (answer?.hashCode() ?: 0)
        result = 31 * result + (translation?.hashCode() ?: 0)
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + (modifyDate?.hashCode() ?: 0)
        result = 31 * result + (sound?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (video?.hashCode() ?: 0)
        result = 31 * result + (question?.hashCode() ?: 0)
        return result
    }
}
