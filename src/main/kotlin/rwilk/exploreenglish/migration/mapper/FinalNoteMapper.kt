package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalNote
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorNote

@Component
class FinalNoteMapper(
    private val finalMediaMapper: FinalMediaMapper
) {

    fun map(etutorNote: EtutorNote, exercise: FinalExercise): FinalNote {
        return FinalNote(
            id = null,
            nativeTitle = etutorNote.nativeTitle,
            foreignTitle = etutorNote.foreignTitle,
            nativeContent = etutorNote.nativeContent,
            foreignContent = etutorNote.foreignContent,
            audio = finalMediaMapper.mapAudio(etutorNote.audio, null),
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorNote.id,
            exercise = exercise
        )
    }

}
