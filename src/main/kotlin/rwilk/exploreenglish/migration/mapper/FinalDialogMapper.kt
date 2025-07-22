package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalDialogItem
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorDialogItem
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType

@Component
class FinalDialogMapper(
    private val finalMediaMapper: FinalMediaMapper
) {

    fun map(etutorDialogItem: EtutorDialogItem, finalExercise: FinalExercise): FinalDialogItem {
        return FinalDialogItem(
            id = null,
            type = etutorDialogItem.type,
            dialogForeign = etutorDialogItem.dialogForeign,
            dialogNative = etutorDialogItem.dialogNative,
            faceImage = finalMediaMapper.mapImage(etutorDialogItem.faceImage),
            comicImage = finalMediaMapper.mapImage(etutorDialogItem.comicImage),
            soundSeekSecond = etutorDialogItem.soundSeekSecond,
            audio = if (finalExercise.type != ExerciseType.VIDEO.name) finalMediaMapper.mapAudio(
                primarySound = etutorDialogItem.audio,
                secondarySound = null
            ) else null,
            video = if (finalExercise.type == ExerciseType.VIDEO.name) finalMediaMapper.mapVideo(
                url = etutorDialogItem.audio,
            ) else null,
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorDialogItem.id,
            exercise = finalExercise
        )
    }

}
