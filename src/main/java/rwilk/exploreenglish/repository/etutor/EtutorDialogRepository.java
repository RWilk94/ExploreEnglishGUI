package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem;

import java.util.List;

public interface EtutorDialogRepository extends JpaRepository<EtutorDialogItem, Long> {

    List<EtutorDialogItem> findAllByExercise_Id(Long id);
}
