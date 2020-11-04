package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.ExerciseRepository;

@Service
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;

  public ExerciseService(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

}
