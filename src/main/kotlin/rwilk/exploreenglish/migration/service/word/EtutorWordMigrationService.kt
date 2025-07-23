package rwilk.exploreenglish.migration.service.word

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.mapper.FinalExerciseWordMapper
import rwilk.exploreenglish.migration.mapper.FinalWordMapper
import rwilk.exploreenglish.migration.repository.FinalExerciseRepository
import rwilk.exploreenglish.migration.repository.FinalExerciseWordRepository
import rwilk.exploreenglish.migration.repository.FinalWordRepository
import rwilk.exploreenglish.repository.etutor.EtutorLessonWordRepository
import javax.transaction.Transactional

@Service
open class EtutorWordMigrationService(
    private val finalWordMapper: FinalWordMapper,
    private val finalExerciseWordMapper: FinalExerciseWordMapper,
    private val finalWordRepository: FinalWordRepository,
    private val finalExerciseRepository: FinalExerciseRepository,
    private val finalExerciseWordRepository: FinalExerciseWordRepository,
    private val etutorLessonWordRepository: EtutorLessonWordRepository,
) : WordMigrationService, CommandLineRunner {

    @Transactional
    override fun migrate(finalExercise: FinalExercise) {
        etutorLessonWordRepository.findAllByExercise_Id(finalExercise.sourceId).forEach { etutorLessonWord ->
            val finalWord = finalWordRepository.save(finalWordMapper.map(etutorLessonWord.word))

            finalExerciseWordRepository.save(
                finalExerciseWordMapper.map(
                    etutorLessonWord = etutorLessonWord,
                    finalExercise = finalExercise,
                    finalWord = finalWord
                )
            )
        }
    }

    override fun run(vararg args: String?) {
//        finalExerciseRepository.getById(1292L).also { finalWord ->
//            migrate(finalWord)
//        }

//        finalExerciseRepository.findAllByTypeIn(
//            listOf(
//                ExerciseType.WORDS_LIST.name,
//                ExerciseType.GRAMMAR_LIST.name,
//                ExerciseType.PICTURES_WORDS_LIST.name,
//            )
//        ).forEach { migrate(it) }
    }
}
