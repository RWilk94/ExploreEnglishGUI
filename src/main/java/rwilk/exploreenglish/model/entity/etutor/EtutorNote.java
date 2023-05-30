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

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;
}