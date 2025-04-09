package rwilk.exploreenglish.model.entity.ewa;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ewa_exercise_items")
public class EwaExerciseItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -1020542306071030909L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "ewa_id")
    private String ewaId;
    @Column(name = "type")
    private String type;
    @Column(name = "thumbnail_s")
    private String thumbnailS;
    @Column(name = "thumbnail_m")
    private String thumbnailM;
    @Column(name = "thumbnail_l")
    private String thumbnailL;
    @Column(name = "thumbnail_xl")
    private String thumbnailXl;
    @Column(name = "video_hevc")
    private String videoHevc;
    @Column(name = "video_vp9")
    private String videoVp9;
    @Column(name = "video_av1")
    private String videoAv1;
    @Column(name = "voice_key")
    private String voiceKey;
    @Column(name = "voice_url")
    private String voiceUrl;
    @Column(name = "content_description")
    private String contentDescription;
    @Column(name = "content_text")
    private String contentText;
    @Column(name = "content_translation")
    private String contentTranslation;

    @Lob
    @Column(name = "json_data", columnDefinition = "LONGTEXT")
    private String jsonData;
    @Column(name = "is_video_downloaded")
    Boolean isVideoDownloaded;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
    private EwaExercise ewaExercise;

}
