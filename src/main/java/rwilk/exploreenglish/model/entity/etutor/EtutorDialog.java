package rwilk.exploreenglish.model.entity.etutor;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_dialog_items")
public class EtutorDialog implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "type")
  private String type;
  @Type(type = "text")
  @Column(name = "dialog_english")
  private String dialogEnglish;
  @Type(type = "text")
  @Column(name = "dialog_polish")
  private String dialogPolish;
  @Column(name = "face_image")
  private String faceImage;
  @Column(name = "audio")
  private String audio;
  @Type(type = "text")
  @Column(name = "html")
  private String html;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;

}
