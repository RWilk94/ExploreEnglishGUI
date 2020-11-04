package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.SentenceRepository;

@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;

  public SentenceService(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

}
