package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorNote;

import java.util.List;

public interface EtutorNoteRepository extends JpaRepository<EtutorNote, Long> {

    List<EtutorNote> findAllByExercise_Id(Long exerciseId);

}
