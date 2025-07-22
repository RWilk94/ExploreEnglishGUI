package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;

import java.util.List;

public interface EtutorExerciseRepository extends JpaRepository<EtutorExercise, Long> {

  List<EtutorExercise> findAllByIsReady(Boolean isReady);

  List<EtutorExercise> findAllByTypeAndIsReady(String type, Boolean isReady);

  List<EtutorExercise> findAllByLesson_Id(Long lessonId);

  void deleteAllByLesson_Id(Long lessonId);
}
