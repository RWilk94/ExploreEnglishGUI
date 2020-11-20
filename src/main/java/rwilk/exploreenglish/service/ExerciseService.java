package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.repository.ExerciseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;
  private final ExerciseItemService exerciseItemService;

  public ExerciseService(ExerciseRepository exerciseRepository, ExerciseItemService exerciseItemService) {
    this.exerciseRepository = exerciseRepository;
    this.exerciseItemService = exerciseItemService;
  }

  public List<Exercise> getAll() {
    return exerciseRepository.findAll();
  }

  public List<Exercise> getAllByLesson(Lesson lesson) {
    return exerciseRepository.findAllByLesson(lesson);
  }

  public Optional<Exercise> getById(Long id) {
    return exerciseRepository.findById(id);
  }

  public Exercise save(Exercise exercise) {
    return exerciseRepository.save(exercise);
  }

  @Transactional
  public void delete(Exercise exercise) {
    exerciseItemService.deleteByExercise(exercise);
    exerciseRepository.delete(exercise);
  }

  @Transactional
  public void deleteByLesson(Lesson lesson) {
    List<Exercise> exercises = getAllByLesson(lesson);
    for (Exercise exercise : exercises) {
      exerciseItemService.deleteByExercise(exercise);
      exerciseRepository.delete(exercise);
    }
  }

  @Transactional
  public void deleteById(Long id) {
    exerciseRepository.deleteById(id);
  }

  public Integer getCountByLesson(Lesson lesson) {
    return (int) exerciseRepository.countAllByLesson(lesson.getId());
  }

}
