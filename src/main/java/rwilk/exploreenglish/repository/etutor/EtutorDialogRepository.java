package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem;

public interface EtutorDialogRepository extends JpaRepository<EtutorDialogItem, Long> {

}
