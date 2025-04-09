package rwilk.exploreenglish.repository.ewa;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.ewa.EwaCourse;

import java.util.List;

public interface EwaCourseRepository extends JpaRepository<EwaCourse, Long> {
    List<EwaCourse> findAllByIsReady(Boolean isReady);
}
