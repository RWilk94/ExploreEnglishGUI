package rwilk.exploreenglish.migration.repository

import org.springframework.data.jpa.repository.JpaRepository
import rwilk.exploreenglish.migration.entity.FinalNote

interface FinalNoteRepository : JpaRepository<FinalNote, Long>
