package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Sentence;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

}
