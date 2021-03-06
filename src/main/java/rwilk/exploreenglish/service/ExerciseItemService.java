package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.repository.ExerciseItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseItemService {

  private final ExerciseItemRepository exerciseItemRepository;

  public ExerciseItemService(ExerciseItemRepository exerciseItemRepository) {
    this.exerciseItemRepository = exerciseItemRepository;
  }

  public List<ExerciseItem> getAll() {
    return exerciseItemRepository.findAll();
  }

  public List<ExerciseItem> getAllByExercise(Exercise exercise) {
    return exerciseItemRepository.findAllByExercise(exercise);
  }

  public Optional<ExerciseItem> getById(Long id) {
    return exerciseItemRepository.findById(id);
  }

  public ExerciseItem save(ExerciseItem exercise) {
    return exerciseItemRepository.save(exercise);
  }

  @Transactional
  public void delete(ExerciseItem exercise) {
    exerciseItemRepository.delete(exercise);
  }

  @Transactional
  public void deleteByExercise(Exercise exercise) {
    exerciseItemRepository.deleteAllByExercise(exercise);
  }

  @Transactional
  public void deleteById(Long id) {
    exerciseItemRepository.deleteById(id);
  }

  public Integer getCountByExercise(Exercise exercise) {
    return (int) exerciseItemRepository.countAllByExercise(exercise);
  }

  public Optional<ExerciseItem> getPreviousExerciseItem(Long exerciseId, Integer position) {
    return exerciseItemRepository.findPreviousPosition(exerciseId, position);
  }

  public Optional<ExerciseItem> getNextExerciseItem(Long exerciseId, Integer position) {
    return exerciseItemRepository.findNextPosition(exerciseId, position);
  }

}
