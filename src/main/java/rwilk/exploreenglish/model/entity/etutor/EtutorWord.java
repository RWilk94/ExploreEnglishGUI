package rwilk.exploreenglish.model.entity.etutor;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "etutor_words")
public class EtutorWord implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "native_translation", length = 2000)
  private String nativeTranslation;
  @Column(name = "additional_information")
  private String additionalInformation;
  @Column(name = "part_of_speech")
  private String partOfSpeech;
  @Column(name = "article")
  private String article;
  @Column(name = "grammar_type")
  private String grammarType;
  @Column(name = "image")
  private String image;
  @Type(type = "text")
  @Column(name = "html")
  private String html;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modify_date")
  private Date modifyDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  private EtutorExercise exercise;

  @ToString.Exclude
  @OneToMany(mappedBy = "word", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
  private List<EtutorLessonWord> etutorLessonWords = new ArrayList<>();

  @ToString.Exclude
  @OneToMany(mappedBy = "word", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
  private List<EtutorDefinition> definitions = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNativeTranslation() {
    return nativeTranslation;
  }

  public void setNativeTranslation(String nativeTranslation) {
    this.nativeTranslation = nativeTranslation;
  }

  public String getAdditionalInformation() {
    return additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  public String getPartOfSpeech() {
    return partOfSpeech;
  }

  public void setPartOfSpeech(String partOfSpeech) {
    this.partOfSpeech = partOfSpeech;
  }

  public String getArticle() {
    return article;
  }

  public void setArticle(String article) {
    this.article = article;
  }

  public String getGrammarType() {
    return grammarType;
  }

  public void setGrammarType(String grammarType) {
    this.grammarType = grammarType;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
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

  public List<EtutorLessonWord> getEtutorLessonWords() {
    return etutorLessonWords;
  }

  public void setEtutorLessonWords(List<EtutorLessonWord> etutorLessonWords) {
    this.etutorLessonWords = etutorLessonWords;
  }

  public List<EtutorDefinition> getDefinitions() {
    return definitions;
  }

  public void setDefinitions(List<EtutorDefinition> definitions) {
    this.definitions = definitions;
  }
}
