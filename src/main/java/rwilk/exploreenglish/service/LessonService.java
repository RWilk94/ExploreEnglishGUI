package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.LessonRepository;

@Service
public class LessonService {

  private final LessonRepository lessonRepository;

  public LessonService(LessonRepository lessonRepository) {
    this.lessonRepository = lessonRepository;
  }

}
