package rwilk.exploreenglish.repository.etutor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorWord;

public interface EtutorWordRepository extends JpaRepository<EtutorWord, Long> {

  List<EtutorWord> findAllByNativeTranslationLikeOrNativeTranslationIsNull(String polishName);

}
