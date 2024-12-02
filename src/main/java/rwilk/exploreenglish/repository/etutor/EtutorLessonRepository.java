package rwilk.exploreenglish.repository.etutor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorLesson;

public interface EtutorLessonRepository extends JpaRepository<EtutorLesson, Long> {

  List<EtutorLesson> findAllByIsReady(Boolean isReady);

  void deleteAllByCourse_Id(Long courseId);
}
