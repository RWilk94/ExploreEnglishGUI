package rwilk.exploreenglish.model.entity.ewa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

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

//    @Column(name = "origin")
//    private String origin;

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

//    @Column(name = "example_origin")
//    private String exampleOrigin;
//    @Column(name = "example_translation")
//    private String exampleTranslation;

    @Lob
    @Column(name = "json_data", columnDefinition = "LONGTEXT")
    private String jsonData;
    @Column(name = "is_video_downloaded")
    Boolean isVideoDownloaded;
    @Column(name = "is_voice_downloaded")
    Boolean isVoiceDownloaded;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
    private EwaExercise ewaExercise;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEwaId() {
        return ewaId;
    }

    public void setEwaId(String ewaId) {
        this.ewaId = ewaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnailS() {
        return thumbnailS;
    }

    public void setThumbnailS(String thumbnailS) {
        this.thumbnailS = thumbnailS;
    }

    public String getThumbnailM() {
        return thumbnailM;
    }

    public void setThumbnailM(String thumbnailM) {
        this.thumbnailM = thumbnailM;
    }

    public String getThumbnailL() {
        return thumbnailL;
    }

    public void setThumbnailL(String thumbnailL) {
        this.thumbnailL = thumbnailL;
    }

    public String getThumbnailXl() {
        return thumbnailXl;
    }

    public void setThumbnailXl(String thumbnailXl) {
        this.thumbnailXl = thumbnailXl;
    }

    public String getVideoHevc() {
        return videoHevc;
    }

    public void setVideoHevc(String videoHevc) {
        this.videoHevc = videoHevc;
    }

    public String getVideoVp9() {
        return videoVp9;
    }

    public void setVideoVp9(String videoVp9) {
        this.videoVp9 = videoVp9;
    }

    public String getVideoAv1() {
        return videoAv1;
    }

    public void setVideoAv1(String videoAv1) {
        this.videoAv1 = videoAv1;
    }

    public String getVoiceKey() {
        return voiceKey;
    }

    public void setVoiceKey(String voiceKey) {
        this.voiceKey = voiceKey;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentTranslation() {
        return contentTranslation;
    }

    public void setContentTranslation(String contentTranslation) {
        this.contentTranslation = contentTranslation;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Boolean getIsVideoDownloaded() {
        return isVideoDownloaded;
    }

    public void setIsVideoDownloaded(Boolean videoDownloaded) {
        isVideoDownloaded = videoDownloaded;
    }

    public Boolean getIsVoiceDownloaded() {
        return isVoiceDownloaded;
    }

    public void setIsVoiceDownloaded(Boolean voiceDownloaded) {
        isVoiceDownloaded = voiceDownloaded;
    }

    public EwaExercise getEwaExercise() {
        return ewaExercise;
    }

    public void setEwaExercise(EwaExercise ewaExercise) {
        this.ewaExercise = ewaExercise;
    }
}
