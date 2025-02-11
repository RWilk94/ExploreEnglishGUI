package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekWord;

import java.util.List;

public interface LangeekWordRepository extends JpaRepository<LangeekWord, Long> {
    List<LangeekWord> findAllByHref(String href);
}
