package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Word;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  List<Word> findAllByLesson(Lesson lesson);

  @Query(value = "select GREATEST(" +
      "(select " +
      "  case " +
      "    when max(`position`) is not null then max(`position`) " +
      "    else -1 " +
      "  end " +
      "from explore_english.words w where w.lesson_id = :lessonId), " +
      "(select " +
      "  case " +
      "    when max(`position`) is not null then max(`position`) " +
      "    else -1 " +
      "  end " +
      "from explore_english.notes n where n.lesson_id = :lessonId), " +
      "(select " +
      "  case " +
      "    when max(`position`) is not null then max(`position`) " +
      "    else -1 " +
      "  end " +
      "from explore_english.exercises e where e.lesson_id = :lessonId)) as max;", nativeQuery = true)
  long countAllByLesson(@Param("lessonId") Long lessonId);

  @Query(value = "select *" +
      "from words w " +
      "where w.lesson_id = :lessonId and w.`position` = (select max(position) from words where lesson_id = :lessonId and `position` < :position);",
      nativeQuery = true)
  Optional<Word> findPreviousPosition(@Param("lessonId") Long lessonId, @Param("position") Integer position);

  @Query(value = "select *" +
      "from words w " +
      "where w.lesson_id = :lessonId and w.`position` = (select min(position) from words where lesson_id = :lessonId and `position` > :position);",
      nativeQuery = true)
  Optional<Word> findNextPosition(@Param("lessonId") Long lessonId, @Param("position") Integer position);

}
