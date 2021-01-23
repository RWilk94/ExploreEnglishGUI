package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
}
