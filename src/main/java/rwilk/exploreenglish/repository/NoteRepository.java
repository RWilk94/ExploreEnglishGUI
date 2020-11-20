package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Note;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

  void deleteAllByLesson(Lesson lesson);

  List<Note> findAllByLesson(Lesson lesson);

  @Query(value = "select (" +
      "(select count(*) from explore_english.words w where w.lesson_id = :lessonId) +" +
      "(select count(*) from explore_english.notes n where n.lesson_id = :lessonId) +" +
      "(select count(*) from explore_english.exercises e where e.lesson_id = :lessonId)" +
      ") as pos;", nativeQuery = true)
  long countAllByLesson(@Param("lessonId") Long lessonId);
}
