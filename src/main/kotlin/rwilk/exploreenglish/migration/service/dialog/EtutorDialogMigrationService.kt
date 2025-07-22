package rwilk.exploreenglish.migration.service.dialog

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.mapper.FinalDialogMapper
import rwilk.exploreenglish.migration.repository.FinalDialogItemRepository
import rwilk.exploreenglish.migration.repository.FinalExerciseRepository
import rwilk.exploreenglish.repository.etutor.EtutorDialogRepository

@Service
open class EtutorDialogMigrationService(
    private val finalExerciseRepository: FinalExerciseRepository,
    private val etutorDialogItemRepository: EtutorDialogRepository,
    private val finalDialogItemRepository: FinalDialogItemRepository,
    private val finalDialogMapper: FinalDialogMapper
) : DialogMigrationService, CommandLineRunner {

    @Transactional
    override fun migrate(finalExercise: FinalExercise) {
        val dialogItems = etutorDialogItemRepository.findAllByExercise_Id(finalExercise.sourceId)
            .map { finalDialogMapper.map(it, finalExercise) }

        finalDialogItemRepository.saveAll(dialogItems)
    }

    override fun run(vararg args: String?) {
//        finalExerciseRepository.findAllByTypeIn(
//            listOf(
//                ExerciseType.COMIC_BOOK.name,
//                ExerciseType.DIALOG.name,
//                ExerciseType.VIDEO.name,
//            )
//        ).forEach { exercise ->
//            migrate(exercise)
//        }
    }
}
