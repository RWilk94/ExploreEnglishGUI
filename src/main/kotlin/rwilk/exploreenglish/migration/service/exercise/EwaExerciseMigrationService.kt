package rwilk.exploreenglish.migration.service.exercise

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.FinalExerciseMapper
import rwilk.exploreenglish.migration.repository.FinalExerciseRepository
import rwilk.exploreenglish.migration.service.exerciseitem.EwaExerciseItemMigrationService
import rwilk.exploreenglish.migration.service.word.EwaLessonPhrasesMigrationService
import rwilk.exploreenglish.migration.service.word.EwaLessonWordsMigrationService
import rwilk.exploreenglish.repository.ewa.EwaExerciseRepository

@Service
open class EwaExerciseMigrationService(
    private val ewaExerciseRepository: EwaExerciseRepository,
    private val ewaLessonWordsMigrationService: EwaLessonWordsMigrationService,
    private val ewaLessonPhrasesMigrationService: EwaLessonPhrasesMigrationService,
    private val ewaExerciseItemMigrationService: EwaExerciseItemMigrationService,
    private val finalExerciseRepository: FinalExerciseRepository,
    private val finalExerciseMapper: FinalExerciseMapper,
) : ExerciseMigrationService, CommandLineRunner {

    private val objectMapper = ObjectMapper()

    @Transactional
    override fun migrate(lesson: FinalLesson): List<FinalExercise> {
        val savedFinalExercises = mutableListOf<FinalExercise>()

        ewaExerciseRepository.findAllByEwaLesson_Id(lesson.sourceId)
            .map { ewaExercise ->
                val jsonNode = objectMapper.readTree(ewaExercise.jsonData)
                val lessonWordsNode = jsonNode.path("result").path("lesson").path("lessonWords")
                if (lessonWordsNode.isArray && lessonWordsNode.size() > 0) {
                    // need to create ONE finalExercise
                    // need to create MULTIPLE finalWords and finalExerciseWords
                    val finalExerciseLessonWords = finalExerciseRepository.save(
                        finalExerciseMapper.map(ewaExercise, "lessonWords", lesson)
                    )
                    lessonWordsNode.forEach { wordNode ->
                        ewaLessonWordsMigrationService.migrate(
                            exercise = finalExerciseLessonWords,
                            node = wordNode
                        )
                    }
                    savedFinalExercises.add(finalExerciseLessonWords)
                }

                val lessonPhrasesNode = jsonNode.path("result").path("lesson").path("lessonPhrases")
                if (lessonPhrasesNode.isArray && lessonPhrasesNode.size() > 0) {
                    // need to create ONE finalExercise
                    // need to create MULTIPLE finalWords and finalExerciseWords
                    val finalExerciseLessonPhrases = finalExerciseRepository.save(
                        finalExerciseMapper.map(ewaExercise, "lessonPhrases", lesson)
                    )
                    lessonPhrasesNode.forEach { wordNode ->
                        ewaLessonPhrasesMigrationService.migrate(
                            exercise = finalExerciseLessonPhrases,
                            node = wordNode
                        )
                    }
                    savedFinalExercises.add(finalExerciseLessonPhrases)
                }

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

    override fun run(vararg args: String?) {
        // migrate(FinalLesson(id = 1L, sourceId = 1))
    }
}
