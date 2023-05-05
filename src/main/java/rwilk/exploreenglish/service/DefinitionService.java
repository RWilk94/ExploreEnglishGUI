package rwilk.exploreenglish.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import rwilk.exploreenglish.model.entity.Definition;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.DefinitionRepository;
import rwilk.exploreenglish.utils.WordUtils;

@Service
@RequiredArgsConstructor
public class DefinitionService {

  private final DefinitionRepository definitionRepository;

  public Optional<Definition> getById(final Long id) {
    return definitionRepository.findById(id);
  }

  public List<Definition> getAll() {
    return definitionRepository.findAll();
  }

  public List<Definition> getAllByWordId(final Long wordId) {
    return definitionRepository.findAllByWord_Id(wordId);
  }

  public List<Definition> getAllByEnglishNameLike(final String pattern) {
    return definitionRepository.findAllByEnglishNameLike(WordUtils.removeNonLiteralCharacters(pattern));
  }

  public List<Definition> getAllByEnglishName(final String pattern) {
    return definitionRepository.findAllByEnglishName(WordUtils.removeNonLiteralCharacters(pattern));
  }

  public List<Definition> getAllByTypeAndBritishSoundContains(final String type, final String britishSoundContains) {
    return definitionRepository.findAllByTypeAndBritishSoundContains(type, britishSoundContains);
  }

  public Definition save(final Definition definition) {
    return definitionRepository.save(definition);
  }

  @Transactional
  public void delete(final Definition definition) {
    definitionRepository.delete(definition);
  }

  @Transactional
  public void deleteByWord(final Word word) {
    definitionRepository.deleteAllByWord(word);
  }

}
