package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.repository.LessonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

  private final LessonRepository lessonRepository;
  private final WordService wordService;
  private final NoteService noteService;
  private final ExerciseService exerciseService;

  public LessonService(LessonRepository lessonRepository, WordService wordService, NoteService noteService, ExerciseService exerciseService) {
    this.lessonRepository = lessonRepository;
    this.wordService = wordService;
    this.noteService = noteService;
    this.exerciseService = exerciseService;
  }

  public List<Lesson> getAll() {
    return lessonRepository.findAll();
  }

  public List<Lesson> getAllByCourse(Course course) {
    return lessonRepository.findAllByCourse(course);
  }

  public Optional<Lesson> getById(Long id) {
    return lessonRepository.findById(id);
  }

  public Lesson save(Lesson Lesson) {
    return lessonRepository.save(Lesson);
  }

  @Transactional
  public void delete(Lesson lesson) {
    wordService.deleteByLesson(lesson);
    noteService.deleteByLesson(lesson);
    exerciseService.deleteByLesson(lesson);
    lessonRepository.delete(lesson);
  }

  @Transactional
  public void deleteByCourse(Course course) {
    List<Lesson> lessons = getAllByCourse(course);
    for (Lesson lesson : lessons) {
      wordService.deleteByLesson(lesson);
      noteService.deleteByLesson(lesson);
      exerciseService.deleteByLesson(lesson);
      lessonRepository.delete(lesson);
    }
  }

  @Transactional
  public void deleteById(Long id) {
    lessonRepository.deleteById(id);
  }

  public Integer getCountByCourse(Course course) {
    return (int) lessonRepository.countAllByCourse(course);
  }

  public Optional<Lesson> getPreviousLesson(Long courseId, Integer position) {
    return lessonRepository.findPreviousPosition(courseId, position);
  }

  public Optional<Lesson> getNextLesson(Long courseId, Integer position) {
    return lessonRepository.findNextPosition(courseId, position);
  }
  
}
