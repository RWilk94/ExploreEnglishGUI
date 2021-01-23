package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.LessonWordRepository;
import rwilk.exploreenglish.repository.WordRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WordService {

  private final LessonWordRepository lessonWordRepository;
  private final WordRepository wordRepository;
  private final WordSentenceService wordSentenceService;

  public WordService(LessonWordRepository lessonWordRepository,
                     WordRepository wordRepository,
                     WordSentenceService wordSentenceService) {
    this.lessonWordRepository = lessonWordRepository;
    this.wordRepository = wordRepository;
    this.wordSentenceService = wordSentenceService;
  }

  public List<Word> getAll() {
    return wordRepository.findAll();
  }

  public Optional<Word> getById(Long id) {
    return wordRepository.findById(id);
  }

  public Word save(Word word) {
    return wordRepository.save(word);
  }

  @Transactional
  public void delete(Word word) {
    wordSentenceService.deleteByWord(word);
    lessonWordRepository.deleteAllByWord(word);
    wordRepository.delete(word);
  }

}
