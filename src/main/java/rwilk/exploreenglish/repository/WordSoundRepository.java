package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSound;

import java.util.List;
import java.util.Optional;

public interface WordSoundRepository extends JpaRepository<WordSound, Long> {

  Optional<WordSound> findById(final Long id);

  List<WordSound> findAllByWord_Id(final Long wordId);

  @Query(nativeQuery = true, value =
      "SELECT * FROM word_items w WHERE " +
          "w.english_name like concat('%', :pattern, '%') " +
          "or w.english_name like concat('%', :pattern) " +
          "or w.english_name like concat(:pattern, '%') " +
          "or lower(w.english_name) = lower(:pattern)")
  List<WordSound> findAllByEnglishNameLike(final String pattern);

  List<WordSound> findAllByEnglishName(final String englishName);

  void deleteAllByWord(final Word word);

}
