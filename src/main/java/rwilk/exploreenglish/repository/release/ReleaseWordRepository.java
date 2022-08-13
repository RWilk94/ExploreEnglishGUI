package rwilk.exploreenglish.repository.release;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.exploreenglish.model.entity.release.ReleaseWord;

@Repository
public interface ReleaseWordRepository extends JpaRepository<ReleaseWord, Long> {

  List<ReleaseWord> findAllBySourceIsNull();
  List<ReleaseWord> findAllByLessonId(Long lessonId);

  List<ReleaseWord> findAllByPartOfSpeechIsNull();

}
