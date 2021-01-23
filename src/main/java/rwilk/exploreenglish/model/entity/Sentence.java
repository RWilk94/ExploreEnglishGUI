package rwilk.exploreenglish.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rwilk.exploreenglish.model.LearnItemChildren;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sentences")
public final class Sentence implements Serializable, LearnItemChildren {

  private static final long serialVersionUID = -8129298224912951576L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;
  @Column(name = "english_name", length = 2000)
  private String englishName;
  @Column(name = "polish_name", length = 2000)
  private String polishName;
  @Column(name = "sound", length = 2000)
  private String sound;

  @OneToMany(mappedBy = "sentence")
  private List<WordSentence> wordSentences;

  @Override
  public String toString() {
    return "[S]" + id + ". " + englishName + " (" + polishName + ")";
  }
}
