package rwilk.exploreenglish.model.entity.etutor;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "etutor_note_items")
public final class EtutorNoteItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Type(type = "text")
  @Column(name = "example")
  private String example;
  @Column(name = "primary_sound")
  private String primarySound;
  @Column(name = "secondary_sound")
  private String secondarySound;
  @Type(type = "text")
  @Column(name = "plain_text")
  private String plainText;
  @Column(name = "image")
  private String image;
  @Column(name = "primary_style")
  private String primaryStyle;
  @Column(name = "secondary_style")
  private String secondaryStyle;
  @Column(name = "additional")
  private String additional;
  @Column(name = "language_type")
  private String languageType;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "note_id", nullable = false, referencedColumnName = "id", insertable = true, updatable = true)
  private EtutorNote note;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getExample() {
    return example;
  }

  public void setExample(String example) {
    this.example = example;
  }

  public String getPrimarySound() {
    return primarySound;
  }

  public void setPrimarySound(String primarySound) {
    this.primarySound = primarySound;
  }

  public String getSecondarySound() {
    return secondarySound;
  }

  public void setSecondarySound(String secondarySound) {
    this.secondarySound = secondarySound;
  }

  public String getPlainText() {
    return plainText;
  }

  public void setPlainText(String plainText) {
    this.plainText = plainText;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getPrimaryStyle() {
    return primaryStyle;
  }

  public void setPrimaryStyle(String primaryStyle) {
    this.primaryStyle = primaryStyle;
  }

  public String getSecondaryStyle() {
    return secondaryStyle;
  }

  public void setSecondaryStyle(String secondaryStyle) {
    this.secondaryStyle = secondaryStyle;
  }

  public String getAdditional() {
    return additional;
  }

  public void setAdditional(String additional) {
    this.additional = additional;
  }

  public String getLanguageType() {
    return languageType;
  }

  public void setLanguageType(String languageType) {
    this.languageType = languageType;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public EtutorNote getNote() {
    return note;
  }

  public void setNote(EtutorNote note) {
    this.note = note;
  }
}
