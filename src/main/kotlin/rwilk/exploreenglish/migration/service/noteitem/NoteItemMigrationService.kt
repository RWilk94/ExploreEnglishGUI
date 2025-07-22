package rwilk.exploreenglish.migration.service.noteitem

import rwilk.exploreenglish.migration.entity.FinalNote

interface NoteItemMigrationService {
    fun migrate(finalNote: FinalNote)
}
