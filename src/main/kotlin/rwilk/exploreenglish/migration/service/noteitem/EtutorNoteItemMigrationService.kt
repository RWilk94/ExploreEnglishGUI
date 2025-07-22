package rwilk.exploreenglish.migration.service.noteitem

import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.entity.FinalNote
import rwilk.exploreenglish.migration.mapper.FinalNoteItemMapper
import rwilk.exploreenglish.migration.repository.FinalNoteItemRepository
import rwilk.exploreenglish.repository.etutor.EtutorNoteItemRepository

@Service
class EtutorNoteItemMigrationService(
    private val etutorNoteItemRepository: EtutorNoteItemRepository,
    private val finalNoteItemRepository: FinalNoteItemRepository,
    private val finalNoteItemMapper: FinalNoteItemMapper,
) : NoteItemMigrationService {

    override fun migrate(finalNote: FinalNote) {
        val etutorNoteItems = etutorNoteItemRepository.findAllByNote_Id(finalNote.sourceId)
        val noteItems = etutorNoteItems.map { finalNoteItemMapper.map(it, finalNote) }
        finalNoteItemRepository.saveAll(noteItems)
    }
}
