package rwilk.exploreenglish.migration.service.note

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rwilk.exploreenglish.migration.entity.FinalExercise
import rwilk.exploreenglish.migration.entity.FinalNote
import rwilk.exploreenglish.migration.mapper.FinalNoteMapper
import rwilk.exploreenglish.migration.repository.FinalExerciseRepository
import rwilk.exploreenglish.migration.repository.FinalNoteRepository
import rwilk.exploreenglish.migration.service.noteitem.EtutorNoteItemMigrationService
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType

@Service
open class EtutorNoteMigrationService(
    private val finalExerciseRepository: FinalExerciseRepository,
    private val etutorNoteRepository: EtutorNoteRepository,
    private val finalNoteRepository: FinalNoteRepository,
    private val finalNoteMapper: FinalNoteMapper,
    private val etutorNoteItemMigrationService: EtutorNoteItemMigrationService
) : NoteMigrationService, CommandLineRunner {

    @Transactional
    override fun migrate(exercise: FinalExercise) {
        val etutorNotes = etutorNoteRepository.findAllByExercise_Id(exercise.sourceId)

        val finalNotes = etutorNotes.map {
            finalNoteMapper.map(it, exercise)
        }

        val savedFinalNotes: List<FinalNote> = finalNoteRepository.saveAll(finalNotes)

        savedFinalNotes.forEach { finalNote ->
            etutorNoteItemMigrationService.migrate(finalNote)
        }
    }

    override fun run(vararg args: String?) {
        finalExerciseRepository.findAllByTypeIn(
            listOf(
                ExerciseType.SCREEN.name,
                ExerciseType.SCREEN_MUSIC.name,
                ExerciseType.SCREEN_CULINARY.name,
                ExerciseType.SCREEN_CULTURAL.name,
            )
        ).forEach { exercise ->
            migrate(exercise)
        }
    }
}
