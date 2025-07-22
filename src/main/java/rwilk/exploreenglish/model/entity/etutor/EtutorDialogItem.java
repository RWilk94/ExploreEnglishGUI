package rwilk.exploreenglish.model.entity.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_dialog_items")
public class EtutorDialogItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "type")
  private String type;
  @Type(type = "text")
  @Column(name = "dialog_foreign")
  private String dialogForeign;
  @Type(type = "text")
  @Column(name = "dialog_native")
  private String dialogNative;
  @Column(name = "face_image")
  private String faceImage;
  @Column(name = "audio")
  private String audio;
  @Type(type = "text")
  @Column(name = "html")
  private String html;
  @Column(name = "comic_image")
  private String comicImage;
  @Column(name = "sound_seek_second")
  private BigDecimal soundSeekSecond;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDialogForeign() {
    return dialogForeign;
  }

  public void setDialogForeign(String dialogForeign) {
    this.dialogForeign = dialogForeign;
  }

  public String getDialogNative() {
    return dialogNative;
  }

  public void setDialogNative(String dialogNative) {
    this.dialogNative = dialogNative;
  }

  public String getFaceImage() {
    return faceImage;
  }

  public void setFaceImage(String faceImage) {
    this.faceImage = faceImage;
  }

  public String getAudio() {
    return audio;
  }

  public void setAudio(String audio) {
    this.audio = audio;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public String getComicImage() {
    return comicImage;
  }

  public void setComicImage(String comicImage) {
    this.comicImage = comicImage;
  }

  public BigDecimal getSoundSeekSecond() {
    return soundSeekSecond;
  }

  public void setSoundSeekSecond(BigDecimal soundSeekSecond) {
    this.soundSeekSecond = soundSeekSecond;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public EtutorExercise getExercise() {
    return exercise;
  }

  public void setExercise(EtutorExercise exercise) {
    this.exercise = exercise;
  }
}
