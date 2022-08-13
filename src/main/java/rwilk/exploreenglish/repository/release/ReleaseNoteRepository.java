package rwilk.exploreenglish.repository.release;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.release.ReleaseNote;

public interface ReleaseNoteRepository extends JpaRepository<ReleaseNote, Long> {

}
