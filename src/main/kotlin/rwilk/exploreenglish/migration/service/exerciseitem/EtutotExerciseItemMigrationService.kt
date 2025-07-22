package rwilk.exploreenglish.migration.service.exerciseitem

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.mapper.FinalExerciseAnswerMapper
import rwilk.exploreenglish.migration.mapper.FinalExerciseQuestionMapper
import rwilk.exploreenglish.migration.repository.FinalExerciseAnswerRepository
import rwilk.exploreenglish.migration.repository.FinalExerciseQuestionRepository
import rwilk.exploreenglish.repository.etutor.EtutorExerciseItemRepository

@Service
open class EtutotExerciseItemMigrationService(
    private val finalExerciseQuestionRepository: FinalExerciseQuestionRepository,
    private val finalExerciseAnswerRepository: FinalExerciseAnswerRepository,
    private val etutorExerciseItemRepository: EtutorExerciseItemRepository,
    private val finalExerciseQuestionMapper: FinalExerciseQuestionMapper,
    private val finalExerciseAnswerMapper: FinalExerciseAnswerMapper,
) : ExerciseItemMigrationService, CommandLineRunner {

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
//        finalExerciseRepository.findAll()
//            .filter {
//                it.type == ExerciseType.PICTURES_LISTENING.name
//                        || it.type == ExerciseType.PICTURES_CHOICE.name
//                        || it.type == ExerciseType.EXERCISE.name
//                        || it.type == ExerciseType.MATCHING_PAIRS.name
//                        || it.type == ExerciseType.PICTURES_MASKED_WRITING.name
//                        || it.type == ExerciseType.SPEAKING.name
//                        || it.type == ExerciseType.WRITING.name
//                        || it.type == ExerciseType.MATCHING_PAIRS_GRAMMAR.name
//                        || it.type == ExerciseType.DIALOG.name
//                        || it.type == ExerciseType.READING.name
//            }
//            .forEachIndexed { index, exercise ->
//                logger.info("Migrating exercise item ${index + 1} for exercise: ${exercise.id} - ${exercise.name}")
//                migrate(exercise)
//            }
    }

}
