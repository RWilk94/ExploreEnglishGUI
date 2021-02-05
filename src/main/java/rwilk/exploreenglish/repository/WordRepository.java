package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Word;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  @Query(nativeQuery = true, value =
      "SELECT * FROM words w WHERE " +
          "w.english_names like concat('%', :pattern, '%') " +
          "or w.english_names like concat('%', :pattern) " +
          "or w.english_names like concat(:pattern, '%') " +
          "or lower(w.english_names) = lower(:pattern)")
  List<Word> findAllByEnglishNamesLike(String pattern);

}
