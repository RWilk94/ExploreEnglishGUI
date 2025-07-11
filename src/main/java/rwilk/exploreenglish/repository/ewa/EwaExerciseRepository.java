package rwilk.exploreenglish.repository.ewa;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.ewa.EwaExercise;

import java.util.List;

public interface EwaExerciseRepository extends JpaRepository<EwaExercise, Long> {
    List<EwaExercise> findAllByIsReady(Boolean isReady);

    List<EwaExercise> findAllByEwaLesson_Id(Long ewaLessonId);
}
