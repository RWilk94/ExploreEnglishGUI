package rwilk.exploreenglish.repository.ewa;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.ewa.EwaLesson;

import java.util.List;

public interface EwaLessonRepository extends JpaRepository<EwaLesson, Long> {
    List<EwaLesson> findAllByIsReady(Boolean isReady);
}
