package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LessonWord;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.LessonWordRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LessonWordService {

  private final LessonWordRepository lessonWordRepository;

  public LessonWordService(LessonWordRepository lessonWordRepository) {
    this.lessonWordRepository = lessonWordRepository;
  }

  public Optional<LessonWord> getById(Long id) {
    return lessonWordRepository.findById(id);
  }

  public Optional<LessonWord> getByLessonIdAndWordId(Long lessonId, Long wordId) {
    return lessonWordRepository.findByLesson_IdAndWord_Id(lessonId, wordId);
  }

  public List<LessonWord> getAll() {
    return lessonWordRepository.findAll();
  }

  public List<LessonWord> getAllByLesson(Lesson lesson) {
    return lessonWordRepository.findAllByLesson(lesson);
  }

  public List<LessonWord> getAllByWord(Word word) {
    return lessonWordRepository.findAllByWord(word);
  }

  public LessonWord save(LessonWord lessonWord) {
    return lessonWordRepository.save(lessonWord);
  }

  @Transactional
  public void deleteById(Long id) {
    lessonWordRepository.deleteById(id);
  }

  public Integer getCountByLesson(Lesson lesson) {
    return (int) lessonWordRepository.countAllByLesson(lesson.getId()) + 1;
  }

  public Optional<LessonWord> getPreviousWord(Long lessonId, Integer position) {
    return lessonWordRepository.findPreviousPosition(lessonId, position);
  }

  public Optional<LessonWord> getNextWord(Long lessonId, Integer position) {
    return lessonWordRepository.findNextPosition(lessonId, position);
  }

  @Transactional
  public void deleteByLesson(Lesson lesson) {
    lessonWordRepository.deleteAllByLesson(lesson);
  }

  @Transactional
  public void deleteByWord(Word word) {
    lessonWordRepository.deleteAllByWord(word);
  }
}
