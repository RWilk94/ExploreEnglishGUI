package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  @Query(value = "select *" +
      "from courses c " +
      "where c.`position` = (select max(c1.position) from courses c1 where c1.`position` < :position);",
      nativeQuery = true)
  Optional<Course> findPreviousPosition(@Param("position") Integer position);

  @Query(value = "select *" +
      "from courses c " +
      "where c.`position` = (select min(c1.position) from courses c1 where c1.`position` > :position);",
      nativeQuery = true)
  Optional<Course> findNextPosition(@Param("position") Integer position);

}
