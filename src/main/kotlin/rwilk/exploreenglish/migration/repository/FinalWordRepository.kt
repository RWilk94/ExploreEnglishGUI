package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalWord

interface FinalWordRepository : JpaRepository<FinalWord, Long> {
    fun findBySourceId(sourceId: Long): FinalWord?
}
