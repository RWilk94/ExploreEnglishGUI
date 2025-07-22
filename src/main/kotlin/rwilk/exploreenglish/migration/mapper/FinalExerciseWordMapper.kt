package rwilk.exploreenglish.migration.mapper

import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalExerciseWord
import rwilk.exploreenglish.migration.entity.FinalWord
import rwilk.exploreenglish.migration.model.SourceEnum
import rwilk.exploreenglish.model.entity.etutor.EtutorLessonWord

@Component
class FinalExerciseWordMapper {
    fun map(etutorLessonWord: EtutorLessonWord, finalExercise: FinalExercise, finalWord: FinalWord): FinalExerciseWord {
        return FinalExerciseWord(
            id = null,
            position = etutorLessonWord.position,
            source = SourceEnum.ETUTOR.name,
            sourceId = etutorLessonWord.id,
            word = finalWord,
            exercise = finalExercise
        )
    }
}
