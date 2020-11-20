package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

  void deleteAllByWord(Word word);

  List<Sentence> findAllByWord(Word word);

  long countAllByWord(Word word);

}
