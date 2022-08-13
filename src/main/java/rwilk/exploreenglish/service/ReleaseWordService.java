package rwilk.exploreenglish.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import rwilk.exploreenglish.model.entity.release.ReleaseWord;
import rwilk.exploreenglish.repository.release.ReleaseWordRepository;

@Service
@RequiredArgsConstructor
public class ReleaseWordService {

  private final ReleaseWordRepository releaseWordRepository;

  public List<ReleaseWord> getAll() {
    return releaseWordRepository.findAllBySourceIsNull();
  }

  public List<ReleaseWord> getById(Long id) {
    return releaseWordRepository.findAllByLessonId(id);
  }

  public void save(ReleaseWord releaseWord) {
    releaseWordRepository.save(releaseWord);
  }

}
