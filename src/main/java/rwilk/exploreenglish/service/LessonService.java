package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.repository.LessonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

  private final LessonRepository lessonRepository;

  public LessonService(LessonRepository lessonRepository) {
    this.lessonRepository = lessonRepository;
  }

  public List<Lesson> getAll() {
    return lessonRepository.findAll();
  }

  public Optional<Lesson> getById(Long id) {
    return lessonRepository.findById(id);
  }

  public Lesson save(Lesson Lesson) {
    return lessonRepository.save(Lesson);
  }

  public void delete(Lesson Lesson) {
    lessonRepository.delete(Lesson);
  }

  public void deleteById(Long id) {
    lessonRepository.deleteById(id);
  }
  
}
