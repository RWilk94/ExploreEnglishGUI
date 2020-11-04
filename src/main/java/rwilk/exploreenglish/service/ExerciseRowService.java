package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.ExerciseRowRepository;

@Service
public class ExerciseRowService {

  private final ExerciseRowRepository exerciseRowRepository;

  public ExerciseRowService(ExerciseRowRepository exerciseRowRepository) {
    this.exerciseRowRepository = exerciseRowRepository;
  }

}
