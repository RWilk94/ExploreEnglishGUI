package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.WordRepository;

@Service
public class WordService {

  private final WordRepository wordRepository;

  public WordService(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

}
