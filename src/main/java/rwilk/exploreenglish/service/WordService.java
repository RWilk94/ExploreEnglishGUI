package rwilk.exploreenglish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSound;
import rwilk.exploreenglish.repository.LessonWordRepository;
import rwilk.exploreenglish.repository.WordRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {

  private final LessonWordRepository lessonWordRepository;
  private final WordRepository wordRepository;
  private final WordSentenceService wordSentenceService;
  private final WordSoundService wordItemService;

  public List<Word> getAll() {
    return wordRepository.findAll();
  }

  public Optional<Word> getById(Long id) {
    return wordRepository.findById(id);
  }

  public List<Word> getAllByEnglishNamesLike(String pattern) {
    return wordItemService.getAllByEnglishNameLike(pattern)
        .stream()
        .map(WordSound::getWord)
        .toList();
  }

  public List<Word> getAllByEnglishNames(String pattern) {
    return wordItemService.getAllByEnglishName(pattern)
        .stream()
        .map(WordSound::getWord)
        .toList();
  }

  public Word save(final Word word) {
    return wordRepository.save(word);
  }

  @Transactional
  public void delete(Word word) {
    wordSentenceService.deleteByWord(word);
    lessonWordRepository.deleteAllByWord(word);
    wordRepository.delete(word);
  }

}
