package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;

import java.util.List;

@Repository
public interface ExerciseItemRepository extends JpaRepository<ExerciseItem, Long> {

  void deleteAllByExercise(Exercise exercise);

  List<ExerciseItem> findAllByExercise(Exercise exercise);

  long countAllByExercise(Exercise exercise);

}
