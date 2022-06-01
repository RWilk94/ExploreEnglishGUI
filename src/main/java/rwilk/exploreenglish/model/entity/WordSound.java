package rwilk.exploreenglish.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "word_sounds")
public final class WordSound implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "type")
  private String type;

  @Column(name = "english_name", length = 2000)
  private String englishName;

  @Column(name = "additional_information", length = 2000)
  private String additionalInformation;

  @Column(name = "british_sound", length = 2000)
  private String britishSound;

  @Column(name = "american_sound", length = 2000)
  private String americanSound;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "word_id", nullable = false, referencedColumnName = "id")
  private Word word;

  @Override
  public String toString() {
    return id +
        ". [" +
        type +
        "] " +
        englishName +
        " (" +
        additionalInformation +
        ") [AME=" +
        americanSound +
        "] " +
        "[BRE=" +
        britishSound +
        "]";
  }
}
