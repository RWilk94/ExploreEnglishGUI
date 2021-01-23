package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSentence;
import rwilk.exploreenglish.repository.WordSentenceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WordSentenceService {

  private final WordSentenceRepository wordSentenceRepository;

  public WordSentenceService(WordSentenceRepository wordSentenceRepository) {
    this.wordSentenceRepository = wordSentenceRepository;
  }

  public List<WordSentence> getAll() {
    return wordSentenceRepository.findAll();
  }

  public List<WordSentence> getAllByWord(Word word) {
    return wordSentenceRepository.findAllByWord(word);
  }

  public List<WordSentence> getAllBySentence(Sentence sentence) {
    return wordSentenceRepository.findAllBySentence(sentence);
  }

  public Optional<WordSentence> getByWordIdAndSentenceId(Long wordId, Long sentenceId) {
    return wordSentenceRepository.findByWord_IdAndSentence_Id(wordId, sentenceId);
  }

  public Integer getCountByWord(Word word) {
    return (int) wordSentenceRepository.countAllByWord(word);
  }

  public WordSentence save(WordSentence wordSentence) {
    return wordSentenceRepository.save(wordSentence);
  }

  @Transactional
  public void deleteByWord(Word word) {
    wordSentenceRepository.deleteAllByWord(word);
  }

  @Transactional
  public void deleteById(Long id) {
    wordSentenceRepository.deleteById(id);
  }

  public Optional<WordSentence> getPreviousSentence(Long wordId, Integer position) {
    return wordSentenceRepository.findPreviousPosition(wordId, position);
  }

  public Optional<WordSentence> getNextSentence(Long wordId, Integer position) {
    return wordSentenceRepository.findNextPosition(wordId, position);
  }

}
