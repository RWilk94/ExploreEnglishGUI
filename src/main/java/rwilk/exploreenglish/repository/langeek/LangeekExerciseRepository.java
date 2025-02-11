package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise;

import java.util.List;

public interface LangeekExerciseRepository extends JpaRepository<LangeekExercise, Long> {

    List<LangeekExercise> findAllByIsReady(Boolean isReady);

    List<LangeekExercise> findAllByTypeAndIsReady(String type, Boolean isReady);

    List<LangeekExercise> findAllByLesson_Id(Long lessonId);

    void deleteAllByLesson_Id(Long lessonId);
}
