package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekCourse;

import java.util.List;

public interface LangeekCourseRepository extends JpaRepository<LangeekCourse, Long> {
    List<LangeekCourse> findAllByIsReadyOrIsReadyIsNull(Boolean isReady);
}
