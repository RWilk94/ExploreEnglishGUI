package rwilk.exploreenglish.migration.service.word

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.mapper.FinalExerciseWordMapper
import rwilk.exploreenglish.migration.mapper.FinalWordMapper
import rwilk.exploreenglish.migration.model.EwaExerciseTypeEnum
import rwilk.exploreenglish.migration.repository.FinalExerciseWordRepository
import rwilk.exploreenglish.migration.repository.FinalWordRepository
import rwilk.exploreenglish.repository.ewa.EwaExerciseItemRepository

@Service
open class EwaWordMigrationService(
    private val finalWordRepository: FinalWordRepository,
    private val ewaExerciseItemRepository: EwaExerciseItemRepository,
    private val finalExerciseWordRepository: FinalExerciseWordRepository,
    private val finalWordMapper: FinalWordMapper,
    private val finalExerciseWordMapper: FinalExerciseWordMapper,
) : WordMigrationService {

    @Transactional
    override fun migrate(finalExercise: FinalExercise) {
        ewaExerciseItemRepository.findAllByEwaExercise_Id(finalExercise.sourceId)
            .filter { it.type == EwaExerciseTypeEnum.EXPLAIN.type }
            .forEach { ewaExerciseItem ->
                val finalWord = finalWordRepository.save(finalWordMapper.map(ewaExerciseItem))

                finalExerciseWordRepository.save(
                    finalExerciseWordMapper.map(
                        ewaExerciseItem = ewaExerciseItem,
                        finalExercise = finalExercise,
                        finalWord = finalWord
                    )
                )
            }
    }
}
