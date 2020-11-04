package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
