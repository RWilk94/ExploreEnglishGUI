package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalNote
import rwilk.exploreenglish.migration.entity.FinalNoteItem
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorNoteItem

@Component
class FinalNoteItemMapper(
    private val finalMediaMapper: FinalMediaMapper
) {

    fun map(etutorNoteItem: EtutorNoteItem, finalNote: FinalNote): FinalNoteItem {
        return FinalNoteItem(
            id = null,
            example = etutorNoteItem.example,
            plainText = etutorNoteItem.plainText,
            primaryStyle = etutorNoteItem.primaryStyle,
            secondaryStyle = etutorNoteItem.secondaryStyle,
            additional = etutorNoteItem.additional,
            languageType = etutorNoteItem.languageType,
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorNoteItem.id,
            audio = finalMediaMapper.mapAudio(
                primarySound = etutorNoteItem.primarySound,
                secondarySound = etutorNoteItem.secondarySound
            ),
            image = finalMediaMapper.mapImage(etutorNoteItem.image),
            note = finalNote
        )
    }

}
