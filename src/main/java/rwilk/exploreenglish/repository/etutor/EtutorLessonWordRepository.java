package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord;

import java.util.List;

public interface EtutorLessonWordRepository extends JpaRepository<EtutorLessonWord, Long> {

    List<EtutorLessonWord> findAllByExercise_Id(Long exerciseId);

}
