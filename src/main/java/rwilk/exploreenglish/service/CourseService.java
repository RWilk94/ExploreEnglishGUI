package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

  private final CourseRepository courseRepository;

  public CourseService(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  public List<Course> getAll() {
    return courseRepository.findAll();
  }

  public Optional<Course> getById(Long id) {
    return courseRepository.findById(id);
  }

  public Course save(Course course) {
    return courseRepository.save(course);
  }

  public void delete(Course course) {
    courseRepository.delete(course);
  }

  public void deleteById(Long id) {
    courseRepository.deleteById(id);
  }

}
