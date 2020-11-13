package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Term;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

}
