package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.WordRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WordService {

  private final WordRepository wordRepository;
  private final SentenceService sentenceService;

  public WordService(WordRepository wordRepository, SentenceService sentenceService) {
    this.wordRepository = wordRepository;
    this.sentenceService = sentenceService;
  }

  public List<Word> getAll() {
    return wordRepository.findAll();
  }

  public List<Word> getAllByLesson(Lesson lesson) {
    return wordRepository.findAllByLesson(lesson);
  }

  public Optional<Word> getById(Long id) {
    return wordRepository.findById(id);
  }

  public Word save(Word word) {
    return wordRepository.save(word);
  }

  @Transactional
  public void delete(Word word) {
    sentenceService.deleteByWord(word);
    wordRepository.delete(word);
  }

  @Transactional
  public void deleteByLesson(Lesson lesson) {
    List<Word> words = getAllByLesson(lesson);
    for (Word word : words) {
      sentenceService.deleteByWord(word);
      wordRepository.delete(word);
    }
  }

  @Transactional
  public void deleteById(Long id) {
    wordRepository.deleteById(id);
  }

}
