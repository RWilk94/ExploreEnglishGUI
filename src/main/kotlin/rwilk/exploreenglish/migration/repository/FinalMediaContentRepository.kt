package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalMediaContent

interface FinalMediaContentRepository : JpaRepository<FinalMediaContent, Long> {
    fun findByUrl(url: String): FinalMediaContent?
    fun findByUrlLike(urlLike: String): List<FinalMediaContent>
}
