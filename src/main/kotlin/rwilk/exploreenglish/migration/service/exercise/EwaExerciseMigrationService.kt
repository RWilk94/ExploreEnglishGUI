package rwilk.exploreenglish.migration.service.exercise

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalLesson
import rwilk.exploreenglish.migration.mapper.FinalExerciseMapper
import rwilk.exploreenglish.migration.mapper.FinalWordMapper
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
    private val finalWordMapper: FinalWordMapper,
) : ExerciseMigrationService, CommandLineRunner {

    private val objectMapper = ObjectMapper()

    @Transactional
    override fun migrate(lesson: FinalLesson): List<FinalExercise> {
        val savedFinalExercises = mutableListOf<FinalExercise>()

        ewaExerciseRepository.findAllByEwaLesson_Id(lesson.sourceId)
            .map { ewaExercise ->
                val jsonNode = objectMapper.readTree(ewaExercise.jsonData)

                var words: MutableList<JsonNode> = mutableListOf()

                val lessonWordsNodes = jsonNode.path("result").path("lesson").path("lessonWords")
                if (lessonWordsNodes.isArray && lessonWordsNodes.size() > 0) {
                    words += lessonWordsNodes.toList()
                }
                val lessonPhrasesNodes = jsonNode.path("result").path("lesson").path("lessonPhrases")
                if (lessonPhrasesNodes.isArray && lessonPhrasesNodes.size() > 0) {
                    words += lessonPhrasesNodes.toList()
                }

                val lessonExplainWordNodes = jsonNode.path("result").path("lesson").path("exercises")
                    .filter { it.path("type").asText() == "explainWord" }
                if (lessonExplainWordNodes.isNotEmpty()) {
                    words += lessonExplainWordNodes.toList()
                }

                // need to create ONE finalExercise
                // need to create MULTIPLE finalWords and finalExerciseWords
                val finalExerciseLessonWords = finalExerciseRepository.save(
                    finalExerciseMapper.map(ewaExercise, "words", lesson)
                )

                // do distinct check
                val finalWordsGroupedByForeignTranslation =
                    words.map { finalWordMapper.mapLessonWordsType(it, finalExerciseLessonWords) }.toList()
                        .groupBy { word ->
                            (word.definitions.firstOrNull { definition -> definition.type == "WORD" }?.foreignTranslation
                                ?: word.definitions.firstOrNull()?.foreignTranslation ?: "").lowercase()
                        }

                finalWordsGroupedByForeignTranslation.map { (foreignTranslation, finalWords) ->
                    val firstWord = finalWords.first()
                    val otherWords = finalWords.subList(1, finalWords.size)

                    otherWords
                        .flatMap { it.definitions }
                        .filter { it.type != "WORD" }
                        .forEach { definitionToAdd ->
                            if (!firstWord.definitions.map { it.foreignTranslation.orEmpty().lowercase() }.contains(definitionToAdd.foreignTranslation.orEmpty().lowercase())) {
                                firstWord.definitions += definitionToAdd
                            }
                         }
//                    firstWord

                    ewaLessonWordsMigrationService.migrateWordV2(finalExerciseLessonWords, firstWord)
                    }

//                finalWordsGroupedByForeignTranslation

//                words.forEach { wordNode ->
//                    ewaLessonWordsMigrationService.migrate(
//                        exercise = finalExerciseLessonWords,
//                        node = wordNode
//                    )
//                }


                savedFinalExercises.add(finalExerciseLessonWords)

                val exercisesNode = jsonNode.path("result").path("lesson").path("exercises")
                if (exercisesNode.isArray && exercisesNode.size() > 0) {
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
         migrate(FinalLesson(id = 41, sourceId = 41))
    }
}
