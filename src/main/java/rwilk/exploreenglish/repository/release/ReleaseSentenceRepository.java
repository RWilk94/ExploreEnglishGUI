package rwilk.exploreenglish.repository.release;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.release.ReleaseSentence;

public interface ReleaseSentenceRepository extends JpaRepository<ReleaseSentence, Long> {

  List<ReleaseSentence> findAllByWord_Id(final Long wordId);

}
