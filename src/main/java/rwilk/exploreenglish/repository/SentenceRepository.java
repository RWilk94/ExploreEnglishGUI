package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;

import java.util.List;
import java.util.Optional;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

  void deleteAllByWord(Word word);

  List<Sentence> findAllByWord(Word word);

  long countAllByWord(Word word);

  @Query(value = "select *" +
      "from sentences s " +
      "where s.word_id = :wordId and s.`position` = (select max(position) from sentences where word_id = :wordId and `position` < :position);",
      nativeQuery = true)
  Optional<Sentence> findPreviousPosition(@Param("wordId") Long wordId, @Param("position") Integer position);

  @Query(value = "select *" +
      "from sentences s " +
      "where s.word_id = :wordId and s.`position` = (select min(position) from sentences where word_id = :wordId and `position` > :position);",
      nativeQuery = true)
  Optional<Sentence> findNextPosition(@Param("wordId") Long wordId, @Param("position") Integer position);

}
