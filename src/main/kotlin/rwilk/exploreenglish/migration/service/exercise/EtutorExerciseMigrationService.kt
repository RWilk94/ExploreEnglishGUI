package rwilk.exploreenglish.migration.service.exercise

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.FinalExerciseMapper
import rwilk.exploreenglish.migration.repository.FinalExerciseRepository
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository

@Service
open class EtutorExerciseMigrationService(
    private val etutorExerciseRepository: EtutorExerciseRepository,
    private val finalExerciseRepository: FinalExerciseRepository,
    private val finalExerciseMapper: FinalExerciseMapper
) : ExerciseMigrationService {

    @Transactional
    override fun migrate(lesson: FinalLesson): List<FinalExercise> {
        return etutorExerciseRepository.findAllByLesson_Id(lesson.sourceId).map {
            finalExerciseMapper.map(it, lesson)
        }.also {
            finalExerciseRepository.saveAll(it)
        }
    }
}
