package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Word;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonWordRepository extends JpaRepository<LessonWord, Long> {

  void deleteAllByLesson(Lesson lesson);

  void deleteAllByWord(Word word);

  List<LessonWord> findAllByLesson(Lesson lesson);

  List<LessonWord> findAllByWord(Word word);

  Optional<LessonWord> findByLesson_IdAndWord_Id(Long lessonId, Long wordId);

  @Query(value = "select GREATEST(" +
      "(select " +
      "  case " +
      "    when max(`position`) is not null then max(`position`) " +
      "    else -1 " +
      "  end " +
      "from explore_english.lesson_word lw where lw.lesson_id = :lessonId), " +
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
      "from lesson_word lw " +
      "where lw.lesson_id = :lessonId and lw.`position` = (select max(position) from lesson_word where lesson_id = :lessonId and `position` < :position);",
      nativeQuery = true)
  Optional<LessonWord> findPreviousPosition(@Param("lessonId") Long lessonId, @Param("position") Integer position);

  @Query(value = "select *" +
      "from lesson_word lw " +
      "where lw.lesson_id = :lessonId and lw.`position` = (select min(position) from lesson_word where lesson_id = :lessonId and `position` > :position);",
      nativeQuery = true)
  Optional<LessonWord> findNextPosition(@Param("lessonId") Long lessonId, @Param("position") Integer position);

}
