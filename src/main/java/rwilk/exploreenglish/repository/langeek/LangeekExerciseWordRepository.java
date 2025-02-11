package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekExerciseWord;

import java.util.List;


public interface LangeekExerciseWordRepository extends JpaRepository<LangeekExerciseWord, Long> {
    LangeekExerciseWord findByExercise_IdAndWord_Id(Long exerciseId, Long wordId);
    List<LangeekExerciseWord> findAllByExercise_Id(Long exerciseId);
}
