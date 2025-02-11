package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekDictionary;

public interface LangeekDictionaryRepository extends JpaRepository<LangeekDictionary, Long> {
    LangeekDictionary findByLangeekIdAndLanguage(Long langeekId, String language);
}
