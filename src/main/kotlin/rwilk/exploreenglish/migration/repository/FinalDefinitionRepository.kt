package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalDefinition

interface FinalDefinitionRepository : JpaRepository<FinalDefinition, Long>
