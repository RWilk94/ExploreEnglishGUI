package rwilk.exploreenglish.model.entity.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_definitions")
public final class EtutorDefinition implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "type")
  private String type;
  @Column(name = "foreign_translation", length = 2000)
  private String foreignTranslation;
  @Column(name = "additional_information", length = 2000)
  private String additionalInformation;
  @Column(name = "primary_sound", length = 2000)
  private String primarySound;   // British Sound
  @Column(name = "secondary_sound", length = 2000)
  private String secondarySound;   // American Sound

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "word_id", nullable = false, referencedColumnName = "id", insertable = true, updatable = true)
  private EtutorWord word;

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

  public String getForeignTranslation() {
    return foreignTranslation;
  }

  public void setForeignTranslation(String foreignTranslation) {
    this.foreignTranslation = foreignTranslation;
  }

  public String getAdditionalInformation() {
    return additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
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

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public EtutorWord getWord() {
    return word;
  }

  public void setWord(EtutorWord word) {
    this.word = word;
  }
}
