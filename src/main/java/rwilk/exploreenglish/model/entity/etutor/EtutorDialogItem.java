package rwilk.exploreenglish.model.entity.etutor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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

}
