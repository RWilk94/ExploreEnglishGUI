package rwilk.exploreenglish.model.entity.etutor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
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

}
