package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.repository.LessonRepository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LessonService {

  private final LessonRepository lessonRepository;
  private final LessonWordService lessonWordService;
  private final NoteService noteService;
  private final ExerciseService exerciseService;

  public List<Lesson> getAll() {
    return lessonRepository.findAll();
  }

  public List<Lesson> getAllByCourse(final Course course) {
    return lessonRepository.findAllByCourseOrderByPosition(course);
  }

  public Optional<Lesson> getById(final Long id) {
    return lessonRepository.findById(id);
  }

  public Lesson save(final Lesson lesson) {
    return lessonRepository.save(lesson);
  }

  @Transactional
  public void delete(final Lesson lesson) {
    lessonWordService.deleteByLesson(lesson);
    noteService.deleteByLesson(lesson);
    exerciseService.deleteByLesson(lesson);
    lessonRepository.delete(lesson);
  }

  @Transactional
  public void deleteByCourse(final Course course) {
    List<Lesson> lessons = getAllByCourse(course);
    for (Lesson lesson : lessons) {
      lessonWordService.deleteByLesson(lesson);
      noteService.deleteByLesson(lesson);
      exerciseService.deleteByLesson(lesson);
      lessonRepository.delete(lesson);
    }
  }

  @Transactional
  public void deleteById(Long id) {
    lessonRepository.deleteById(id);
  }

  public Integer getNextPosition(Long courseId) {
    Integer maxPosition = lessonRepository.maxPosition(courseId);
    if (maxPosition == null) {
      maxPosition = 0;
    }
    return maxPosition + 1;
  }

  public Optional<Lesson> getPreviousLesson(Long courseId, Integer position) {
    return lessonRepository.findPreviousPosition(courseId, position);
  }

  public Optional<Lesson> getNextLesson(Long courseId, Integer position) {
    return lessonRepository.findNextPosition(courseId, position);
  }
  
}
