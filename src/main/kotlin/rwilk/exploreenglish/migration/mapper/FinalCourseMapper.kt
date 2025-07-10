package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalCourse
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.LanguageEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorCourse
import rwilk.exploreenglish.model.entity.ewa.EwaCourse
import rwilk.exploreenglish.model.entity.langeek.LangeekCourse

@Component
class FinalCourseMapper {

    fun map(ewaCourse: EwaCourse): FinalCourse {
        return FinalCourse(
            name = ewaCourse.name,
            description = ewaCourse.description,
            image = ewaCourse.imageS,
            language = LanguageEnum.ENGLISH.name,
            source = SourceEnum.EWA.name,
            sourceId = ewaCourse.id,
        )
    }

    fun map(langeekCourse: LangeekCourse): FinalCourse {
        return FinalCourse(
            name = langeekCourse.name,
            description = langeekCourse.description,
            image = langeekCourse.image,
            language = LanguageEnum.ENGLISH.name,
            source = SourceEnum.LANGEEK.name,
            sourceId = langeekCourse.id,
        )
    }

    fun map(etutorCourse: EtutorCourse): FinalCourse {
        return FinalCourse(
            name = etutorCourse.name,
            description = etutorCourse.description,
            image = etutorCourse.image,
            language = etutorCourse.language,
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorCourse.id,
        )
    }
}
