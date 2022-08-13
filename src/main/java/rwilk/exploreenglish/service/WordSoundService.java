package rwilk.exploreenglish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.model.entity.WordSound;
import rwilk.exploreenglish.repository.WordSoundRepository;
import rwilk.exploreenglish.utils.WordUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordSoundService {

  private final WordSoundRepository wordSoundRepository;

  public Optional<WordSound> getById(final Long id) {
    return wordSoundRepository.findById(id);
  }

  public List<WordSound> getAll() {
    return wordSoundRepository.findAll();
  }

  public List<WordSound> getAllByWordId(final Long wordId) {
    return wordSoundRepository.findAllByWord_Id(wordId);
  }

  public List<WordSound> getAllByEnglishNameLike(final String pattern) {
    return wordSoundRepository.findAllByEnglishNameLike(WordUtils.removeNonLiteralCharacters(pattern));
  }

  public List<WordSound> getAllByEnglishName(final String pattern) {
    return wordSoundRepository.findAllByEnglishName(WordUtils.removeNonLiteralCharacters(pattern));
  }

  public WordSound save(final WordSound wordSound) {
    return wordSoundRepository.save(wordSound);
  }

  @Transactional
  public void delete(final WordSound wordSound) {
    wordSoundRepository.delete(wordSound);
  }

  @Transactional
  public void deleteByWord(final Word word) {
    wordSoundRepository.deleteAllByWord(word);
  }

}
