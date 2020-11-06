package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.WordRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WordService {

  private final WordRepository wordRepository;

  public WordService(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public List<Word> getAll() {
    return wordRepository.findAll();
  }

  public Optional<Word> getById(Long id) {
    return wordRepository.findById(id);
  }

  public Word save(Word Word) {
    return wordRepository.save(Word);
  }

  public void delete(Word Word) {
    wordRepository.delete(Word);
  }

  public void deleteById(Long id) {
    wordRepository.deleteById(id);
  }

}
