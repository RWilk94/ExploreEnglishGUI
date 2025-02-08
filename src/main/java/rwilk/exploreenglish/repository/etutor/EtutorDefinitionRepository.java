package rwilk.exploreenglish.repository.etutor;

import org.springframework.data.jpa.repository.JpaRepository;

import rwilk.exploreenglish.model.entity.etutor.EtutorDefinition;

import java.util.List;

public interface EtutorDefinitionRepository extends JpaRepository<EtutorDefinition, Long> {

    List<EtutorDefinition> findAllByForeignTranslation(String foreignTranslation);

}
