package rwilk.exploreenglish.model.entity.release;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "release_lessons")
public class ReleaseLesson implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "english_name", length = 2000)
  String enName;
  @Column(name = "polish_name", length = 2000)
  String plName;
  @Column(name = "href", length = 256)
  String href;

  @Column(name = "is_ready")
  Boolean isReady;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
  ReleaseCourse course;

//  @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = { CascadeType.ALL})
//  private List<WordRelease> words = new ArrayList<>();
//
//  @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
//  private List<NoteRelease> notes = new ArrayList<>();
//
//  @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
//  private List<ExerciseRelease> exercises = new ArrayList<>();

}