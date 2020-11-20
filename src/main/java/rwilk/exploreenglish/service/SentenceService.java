package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.SentenceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;

  public SentenceService(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

  public List<Sentence> getAll() {
    return sentenceRepository.findAll();
  }

  public List<Sentence> getAllByWord(Word word) {
    return sentenceRepository.findAllByWord(word);
  }

  public Optional<Sentence> getById(Long id) {
    return sentenceRepository.findById(id);
  }

  public Sentence save(Sentence sentence) {
    return sentenceRepository.save(sentence);
  }

  @Transactional
  public void delete(Sentence sentence) {
    sentenceRepository.delete(sentence);
  }

  @Transactional
  public void deleteByWord(Word word) {
    sentenceRepository.deleteAllByWord(word);
  }

  @Transactional
  public void deleteById(Long id) {
    sentenceRepository.deleteById(id);
  }

  public Integer getCountByWord(Word word) {
    return (int) sentenceRepository.countAllByWord(word);
  }

  public Optional<Sentence> getPreviousSentence(Long wordId, Integer position) {
    return sentenceRepository.findPreviousPosition(wordId, position);
  }

  public Optional<Sentence> getNextSentence(Long wordId, Integer position) {
    return sentenceRepository.findNextPosition(wordId, position);
  }

}
