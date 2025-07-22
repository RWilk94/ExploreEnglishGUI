package rwilk.exploreenglish.model.entity.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_notes")
public final class EtutorNote implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "native_title")
  private String nativeTitle;
  @Type(type = "text")
  @Column(name = "native_content")
  private String nativeContent;
  @Type(type = "text")
  @Column(name = "native_html")
  private String nativeHtml;
  @Column(name = "foreign_title")
  private String foreignTitle;
  @Type(type = "text")
  @Column(name = "foreign_content")
  private String foreignContent;
  @Type(type = "text")
  @Column(name = "foreign_html")
  private String foreignHtml;
  @Column(name = "audio")
  private String audio;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;

  @ToString.Exclude
  @OneToMany(mappedBy = "note", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private List<EtutorNoteItem> noteItems;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNativeTitle() {
    return nativeTitle;
  }

  public void setNativeTitle(String nativeTitle) {
    this.nativeTitle = nativeTitle;
  }

  public String getNativeContent() {
    return nativeContent;
  }

  public void setNativeContent(String nativeContent) {
    this.nativeContent = nativeContent;
  }

  public String getNativeHtml() {
    return nativeHtml;
  }

  public void setNativeHtml(String nativeHtml) {
    this.nativeHtml = nativeHtml;
  }

  public String getForeignTitle() {
    return foreignTitle;
  }

  public void setForeignTitle(String foreignTitle) {
    this.foreignTitle = foreignTitle;
  }

  public String getForeignContent() {
    return foreignContent;
  }

  public void setForeignContent(String foreignContent) {
    this.foreignContent = foreignContent;
  }

  public String getForeignHtml() {
    return foreignHtml;
  }

  public void setForeignHtml(String foreignHtml) {
    this.foreignHtml = foreignHtml;
  }

  public String getAudio() {
    return audio;
  }

  public void setAudio(String audio) {
    this.audio = audio;
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

  public List<EtutorNoteItem> getNoteItems() {
    return noteItems;
  }

  public void setNoteItems(List<EtutorNoteItem> noteItems) {
    this.noteItems = noteItems;
  }
}
