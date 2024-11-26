package rwilk.exploreenglish.model.entity.etutor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
}
