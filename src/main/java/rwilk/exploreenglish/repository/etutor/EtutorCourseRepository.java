package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;

import java.util.List;

public interface EtutorCourseRepository extends JpaRepository<EtutorCourse, Long> {

    List<EtutorCourse> findAllByIsReady(Boolean isReady);
}
