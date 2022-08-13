package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Lesson;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

  List<Lesson> findAllByCourseOrderByPosition(Course course);

  @Query(value = "select max(l.`position`) " +
          "from lessons l " +
          "where l.course_id = :courseId",
          nativeQuery = true)
  Integer maxPosition(@Param("courseId") Long courseId);

  @Query(value = "select *" +
      "from lessons l " +
      "where l.course_id = :courseId and l.`position` = (select max(position) from lessons l where l.course_id = :courseId and l.`position` < :position);",
      nativeQuery = true)
  Optional<Lesson> findPreviousPosition(@Param("courseId") Long courseId, @Param("position") Integer position);

  @Query(value = "select *" +
      "from lessons l " +
      "where l.course_id = :courseId and l.`position` = (select min(position) from lessons l where l.course_id = :courseId and l.`position` > :position);",
      nativeQuery = true)
  Optional<Lesson> findNextPosition(@Param("courseId") Long courseId, @Param("position") Integer position);

}
