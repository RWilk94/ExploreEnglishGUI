package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorLesson
import rwilk.exploreenglish.model.entity.ewa.EwaLesson
import rwilk.exploreenglish.model.entity.langeek.LangeekLesson

@Component
class FinalLessonMapper(
    private val finalMediaMapper: FinalMediaMapper
) {
    fun map(lesson: EwaLesson, course: FinalCourse): FinalLesson {
        return FinalLesson(
            name = lesson.origin,
            description = lesson.title,
            image = finalMediaMapper.mapImage(lesson.imageS),
            source = SourceEnum.EWA.name,
            sourceId = lesson.id,
            course = course
        )
    }

    fun map(lessson: LangeekLesson, course: FinalCourse): FinalLesson {
        return FinalLesson(
            name = lessson.name,
            description = lessson.description,
            image = finalMediaMapper.mapImage(lessson.image),
            source = SourceEnum.LANGEEK.name,
            sourceId = lessson.id,
            course = course
        )
    }

    fun map(lesson: EtutorLesson, course: FinalCourse): FinalLesson {
        return FinalLesson(
            name = lesson.name,
            description = lesson.description,
            image = finalMediaMapper.mapImage(lesson.image),
            source = SourceEnum.ETUTOR.name,
            sourceId = lesson.id,
            course = course
        )
    }

}
