package rwilk.exploreenglish.migration.entity

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "final_exercise_questions",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "source_id"])
    ]
)
data class FinalExerciseQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "type", nullable = false)
    val type: String? = null,

    @Column(name = "instruction", nullable = false)
    val instruction: String? = null,

    @Column(name = "question", nullable = false, length = 2000)
    val question: String? = null,

    @Column(name = "template", length = 2000)
    val template: String? = null,

    @Column(name = "final_answer", length = 2000)
    val finalAnswer: String? = null,

    @Column(name = "final_answer_description", length = 2000)
    val finalAnswerDescription: String? = null,

    @Column(name = "final_answer_translation", length = 2000)
    val finalAnswerTranslation: String? = null,

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
    @JoinColumn(name = "media_video_id", referencedColumnName = "id")
    val video: FinalMedia? = null,

    @OneToMany(
        mappedBy = "question",
        fetch = FetchType.LAZY,
        cascade = [
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
            CascadeType.REMOVE
        ]
    )
    val answers: List<FinalExerciseAnswer> = emptyList(),

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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FinalExerciseQuestion

        if (id != other.id) return false
        if (sourceId != other.sourceId) return false
        if (type != other.type) return false
        if (instruction != other.instruction) return false
        if (question != other.question) return false
        if (template != other.template) return false
        if (finalAnswer != other.finalAnswer) return false
        if (finalAnswerDescription != other.finalAnswerDescription) return false
        if (finalAnswerTranslation != other.finalAnswerTranslation) return false
        if (source != other.source) return false
        if (modifyDate != other.modifyDate) return false
        if (audio != other.audio) return false
        if (image != other.image) return false
        if (video != other.video) return false
        if (answers != other.answers) return false
        if (exercise != other.exercise) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (sourceId?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (instruction?.hashCode() ?: 0)
        result = 31 * result + (question?.hashCode() ?: 0)
        result = 31 * result + (template?.hashCode() ?: 0)
        result = 31 * result + (finalAnswer?.hashCode() ?: 0)
        result = 31 * result + (finalAnswerDescription?.hashCode() ?: 0)
        result = 31 * result + (finalAnswerTranslation?.hashCode() ?: 0)
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + (modifyDate?.hashCode() ?: 0)
        result = 31 * result + (audio?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (video?.hashCode() ?: 0)
        result = 31 * result + answers.hashCode()
        result = 31 * result + (exercise?.hashCode() ?: 0)
        return result
    }
}
