package rwilk.exploreenglish.repository.etutor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;

public interface EtutorExerciseRepository extends JpaRepository<EtutorExercise, Long> {

  List<EtutorExercise> findAllByIsReady(Boolean isReady);

  List<EtutorExercise> findAllByTypeAndIsReady(String type, Boolean isReady);

  void deleteAllByLesson_Id(Long lessonId);
}
