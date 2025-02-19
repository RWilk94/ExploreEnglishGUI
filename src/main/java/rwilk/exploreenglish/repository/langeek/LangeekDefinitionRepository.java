package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.langeek.LangeekDefinition;

public interface LangeekDefinitionRepository extends JpaRepository<LangeekDefinition, Long> {
}
