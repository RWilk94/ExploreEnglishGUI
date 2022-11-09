package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.Definition;

import java.util.List;
import java.util.Optional;

public interface DefinitionRepository extends JpaRepository<Definition, Long> {

  Optional<Definition> findById(final Long id);

  List<Definition> findAllByWord_Id(final Long wordId);

  @Query(nativeQuery = true, value =
      "SELECT * FROM word_items w WHERE " +
          "w.english_name like concat('%', :pattern, '%') " +
          "or w.english_name like concat('%', :pattern) " +
          "or w.english_name like concat(:pattern, '%') " +
          "or lower(w.english_name) = lower(:pattern)")
  List<Definition> findAllByEnglishNameLike(final String pattern);

  List<Definition> findAllByEnglishName(final String englishName);

  void deleteAllByWord(final Word word);

}
