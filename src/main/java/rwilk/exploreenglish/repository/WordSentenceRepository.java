package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSentence;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordSentenceRepository extends JpaRepository<WordSentence, Long> {

  void deleteAllByWord(Word word);

  List<WordSentence> findAllByWord(Word word);

  List<WordSentence> findAllBySentence(Sentence sentence);

  Optional<WordSentence> findByWord_IdAndSentence_Id(Long wordId, Long sentenceId);

  long countAllByWord(Word word);

  @Query(value = "select *" +
      "from word_sentence ws " +
      "where ws.word_id = :wordId and ws.`position` = (select max(position) from word_sentence where word_id = :wordId and `position` < :position);",
      nativeQuery = true)
  Optional<WordSentence> findPreviousPosition(@Param("wordId") Long wordId, @Param("position") Integer position);

  @Query(value = "select *" +
      "from word_sentence ws " +
      "where ws.word_id = :wordId and ws.`position` = (select min(position) from word_sentence where word_id = :wordId and `position` > :position);",
      nativeQuery = true)
  Optional<WordSentence> findNextPosition(@Param("wordId") Long wordId, @Param("position") Integer position);

}
