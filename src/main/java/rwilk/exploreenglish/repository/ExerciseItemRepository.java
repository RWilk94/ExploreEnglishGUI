package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseItemRepository extends JpaRepository<ExerciseItem, Long> {

  void deleteAllByExercise(Exercise exercise);

  List<ExerciseItem> findAllByExercise(Exercise exercise);

  long countAllByExercise(Exercise exercise);

  @Query(value = "select *" +
      "from exercise_item ei " +
      "where ei.exercise_id = :exerciseId and ei.`position` = (select max(position) from exercise_item where exercise_id = :exerciseId and `position` < :position);",
      nativeQuery = true)
  Optional<ExerciseItem> findPreviousPosition(@Param("exerciseId") Long exerciseId, @Param("position") Integer position);

  @Query(value = "select *" +
      "from exercise_item ei " +
      "where ei.exercise_id = :exerciseId and ei.`position` = (select min(position) from exercise_item where exercise_id = :exerciseId and `position` > :position);",
      nativeQuery = true)
  Optional<ExerciseItem> findNextPosition(@Param("exerciseId") Long exerciseId, @Param("position") Integer position);

}
