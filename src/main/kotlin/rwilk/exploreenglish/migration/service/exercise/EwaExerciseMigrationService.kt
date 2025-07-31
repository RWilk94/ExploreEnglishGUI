package rwilk.exploreenglish.migration.service.exercise

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.service.exerciseitem.EwaExerciseItemMigrationService
import rwilk.exploreenglish.repository.ewa.EwaExerciseRepository

@Service
open class EwaExerciseMigrationService(
    private val ewaExerciseRepository: EwaExerciseRepository,
    private val ewaExerciseItemMigrationService: EwaExerciseItemMigrationService,
) : ExerciseMigrationService {

    private val objectMapper = ObjectMapper()

    @Transactional
    override fun migrate(lesson: FinalLesson): List<FinalExercise> {
        val savedFinalExercises = mutableListOf<FinalExercise>()

        ewaExerciseRepository.findAllByEwaLesson_Id(lesson.sourceId)
            .map { ewaExercise ->
                val jsonNode = objectMapper.readTree(ewaExercise.jsonData)
                val exercisesNode = jsonNode.path("result").path("lesson").path("exercises")
                if (exercisesNode.isArray) {
                    exercisesNode.groupBy { it.path("type").asText() }
                        .forEach { entry ->
                            val finalExercise =
                                ewaExerciseItemMigrationService.migrate(
                                    lesson = lesson,
                                    ewaExercise = ewaExercise,
                                    type = entry.key,
                                    nodes = entry.value
                                )
                            savedFinalExercises.add(finalExercise)
                        }
                }
            }

        return savedFinalExercises
    }
}
