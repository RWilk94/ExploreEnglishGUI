package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.repository.ExerciseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;

  public ExerciseService(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

  public List<Exercise> getAll() {
    return exerciseRepository.findAll();
  }

  public Optional<Exercise> getById(Long id) {
    return exerciseRepository.findById(id);
  }

  public Exercise save(Exercise exercise) {
    return exerciseRepository.save(exercise);
  }

  public void delete(Exercise exercise) {
    exerciseRepository.delete(exercise);
  }

  public void deleteById(Long id) {
    exerciseRepository.deleteById(id);
  }

}
