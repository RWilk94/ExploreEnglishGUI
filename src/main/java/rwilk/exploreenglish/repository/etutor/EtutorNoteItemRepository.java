package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem;

import java.util.List;

public interface EtutorNoteItemRepository extends JpaRepository<EtutorNoteItem, Long> {

    List<EtutorNoteItem> findAllByNote_Id(Long id);
}
