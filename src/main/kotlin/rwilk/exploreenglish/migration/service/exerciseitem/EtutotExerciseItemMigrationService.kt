package rwilk.exploreenglish.migration.service.exerciseitem

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.mapper.FinalExerciseAnswerMapper
import rwilk.exploreenglish.migration.mapper.FinalExerciseQuestionMapper
import rwilk.exploreenglish.migration.mapper.FinalMediaContentMapper
import rwilk.exploreenglish.migration.mapper.FinalMediaMapper
import rwilk.exploreenglish.migration.repository.*
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType

@Service
open class EtutotExerciseItemMigrationService(
    private val finalExerciseRepository: FinalExerciseRepository,
    private val finalExerciseQuestionRepository: FinalExerciseQuestionRepository,
    private val finalExerciseAnswerRepository: FinalExerciseAnswerRepository,
    private val finalMediaRepository: FinalMediaRepository,
    private val finalMediaContentRepository: FinalMediaContentRepository,
    private val etutorExerciseItemRepository: EtutorExerciseItemRepository,
    private val finalExerciseQuestionMapper: FinalExerciseQuestionMapper,
    private val finalExerciseAnswerMapper: FinalExerciseAnswerMapper,
    private val finalMediaMapper: FinalMediaMapper,
    private val finalMediaContentMapper: FinalMediaContentMapper,
) : ExerciseItemMigrationService, CommandLineRunner {

    private val logger: Logger = LoggerFactory.getLogger(EtutotExerciseItemMigrationService::class.java)

    @Transactional
    override fun migrate(exercise: FinalExercise) {
        val exerciseItems = etutorExerciseItemRepository.findAllByExercise_Id(exercise.id)

        for (exerciseItem in exerciseItems) {
            val exerciseQuestion = finalExerciseQuestionMapper.map(exerciseItem, exercise)
            val exerciseAnswers = finalExerciseAnswerMapper.map(exerciseItem, exerciseQuestion)

            val savedQuestion = finalExerciseQuestionRepository.save(exerciseQuestion)
            exerciseAnswers.forEach {
                it.question = savedQuestion
                finalExerciseAnswerRepository.save(it)
            }
        }
    }

    override fun run(vararg args: String?) {
        finalExerciseRepository.findAll()
            .filter {
                it.type == ExerciseType.PICTURES_LISTENING.name
                        || it.type == ExerciseType.PICTURES_CHOICE.name
                        || it.type == ExerciseType.EXERCISE.name
                        || it.type == ExerciseType.MATCHING_PAIRS.name
                        || it.type == ExerciseType.PICTURES_MASKED_WRITING.name
                        || it.type == ExerciseType.SPEAKING.name
                        || it.type == ExerciseType.WRITING.name
                        || it.type == ExerciseType.MATCHING_PAIRS_GRAMMAR.name
                        || it.type == ExerciseType.DIALOG.name
                        || it.type == ExerciseType.READING.name
            }
            .forEachIndexed { index, exercise ->
                logger.info("Migrating exercise item ${index + 1} for exercise: ${exercise.id} - ${exercise.name}")
                migrate(exercise)
            }
    }

}
