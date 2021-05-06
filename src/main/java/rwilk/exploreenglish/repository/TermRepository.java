package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Term;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

  List<Term> findAllBySourceNotIn(List<String> excludedSources);

  List<Term> findAllByIsIgnoredAndIsAddedAndSourceNotIn(Boolean isIgnored, Boolean isAdded, List<String> excludedSources);

  List<Term> findAllByCategoryAndSource(String category, String source);

  List<Term> findAllBySourceAndCategoryLike(String source, String category);

}
