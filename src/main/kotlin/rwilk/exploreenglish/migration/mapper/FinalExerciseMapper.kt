package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise
import rwilk.exploreenglish.model.entity.ewa.EwaExercise
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise

@Component
class FinalExerciseMapper(
    private val finalMediaMapper: FinalMediaMapper
) {
    fun map(ewaExercise: EwaExercise, ewaLesson: FinalLesson): FinalExercise {
        return FinalExercise(
            name = ewaExercise.origin,
            description = ewaExercise.title,
            image = finalMediaMapper.mapImage(ewaExercise.imageS),
            type = ewaExercise.kind,
            source = SourceEnum.EWA.name,
            sourceId = ewaExercise.id,
            lesson = ewaLesson
        )
    }

    fun map(ewaExercise: LangeekExercise, langeekLesson: FinalLesson): FinalExercise {
        return FinalExercise(
            name = ewaExercise.name,
            description = ewaExercise.description,
            image = finalMediaMapper.mapImage(ewaExercise.image),
            type = ewaExercise.type,
            source = SourceEnum.LANGEEK.name,
            sourceId = ewaExercise.id,
            lesson = langeekLesson
        )
    }

    fun map(ewaExercise: EtutorExercise, etutorLesson: FinalLesson): FinalExercise {
        return FinalExercise(
            name = ewaExercise.name,
            description = null,
            image = finalMediaMapper.mapImage(ewaExercise.image),
            type = ewaExercise.type,
            source = SourceEnum.ETUTOR.name,
            sourceId = ewaExercise.id,
            lesson = etutorLesson
        )
    }
}
