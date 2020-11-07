package rwilk.exploreenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Word;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  List<Word> findAllByLesson(Lesson lesson);

}
