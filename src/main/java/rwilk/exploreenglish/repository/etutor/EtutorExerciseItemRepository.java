package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorExerciseItem;

import java.util.List;

public interface EtutorExerciseItemRepository extends JpaRepository<EtutorExerciseItem, Long> {

    List<EtutorExerciseItem> findAllByExercise_Id(Long exerciseId);

}
