package rwilk.exploreenglish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.migration.entity.FinalCourse;
import rwilk.exploreenglish.migration.repository.FinalCourseRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinalCourseService {

  private final FinalCourseRepository courseRepository;
  private final LessonService lessonService;

  public List<FinalCourse> getAll() {
    return courseRepository.findAll();
  }

  public Optional<FinalCourse> getById(Long id) {
    return courseRepository.findById(id);
  }

  public FinalCourse save(FinalCourse course) {
    return courseRepository.save(course);
  }

//  @Transactional
//  public void delete(FinalCourse course) {
//    lessonService.deleteByCourse(course);
//    courseRepository.delete(course);
//  }

//  @Transactional
//  public void deleteById(Long id) {
//    courseRepository.deleteById(id);
//  }

  public Integer getCount() {
    return (int) courseRepository.count();
  }
}
