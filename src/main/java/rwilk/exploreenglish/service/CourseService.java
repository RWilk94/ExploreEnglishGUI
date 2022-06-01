package rwilk.exploreenglish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;
  private final LessonService lessonService;

  public List<Course> getAll() {
    return courseRepository.findAll();
  }

  public Optional<Course> getById(Long id) {
    return courseRepository.findById(id);
  }

  public Course save(Course course) {
    return courseRepository.save(course);
  }

  @Transactional
  public void delete(Course course) {
    lessonService.deleteByCourse(course);
    courseRepository.delete(course);
  }

  @Transactional
  public void deleteById(Long id) {
    courseRepository.deleteById(id);
  }

  public Integer getCount() {
    return (int) courseRepository.count();
  }

  public Optional<Course> getPreviousCourse(Integer position) {
    return courseRepository.findPreviousPosition(position);
  }

  public Optional<Course> getNextCourse(Integer position) {
    return courseRepository.findNextPosition(position);
  }

}
