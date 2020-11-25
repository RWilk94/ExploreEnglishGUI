package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Term;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

  List<Term> findAllByIsIgnoredAndIsAdded(Boolean isIgnored, Boolean isAdded);

}
