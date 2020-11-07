package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
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

  public Optional<ExerciseItem> getById(Long id) {
    return exerciseItemRepository.findById(id);
  }

  public ExerciseItem save(ExerciseItem exercise) {
    return exerciseItemRepository.save(exercise);
  }

  public void delete(ExerciseItem exercise) {
    exerciseItemRepository.delete(exercise);
  }

  public void deleteById(Long id) {
    exerciseItemRepository.deleteById(id);
  }

}
