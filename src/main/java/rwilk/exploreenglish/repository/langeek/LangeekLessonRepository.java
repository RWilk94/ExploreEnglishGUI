package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekLesson;

import java.util.List;

public interface LangeekLessonRepository extends JpaRepository<LangeekLesson, Long> {
    List<LangeekLesson> findAllByIsReady(Boolean isReady);

    List<LangeekLesson> findAllByCourse_Id(Long courseId);

    void deleteAllByCourse_Id(Long courseId);
}
