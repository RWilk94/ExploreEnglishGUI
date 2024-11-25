package rwilk.exploreenglish.scrapper.etutor.content_v2.note;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.content_v2.BaseNoteScrapperV2;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.List;

@Component
public class NoteScrapperV2 extends BaseNoteScrapperV2 implements CommandLineRunner {

    public NoteScrapperV2(EtutorExerciseRepository etutorExerciseRepository,
                          EtutorNoteRepository etutorNoteRepository) {
        super(etutorExerciseRepository, etutorNoteRepository);
    }

    @Override
    public void run(final String... args) throws Exception {
//        webScrap(etutorExerciseRepository.findById(173L).get());
//        etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.SCREEN.toString(), false)
//                .subList(0, 10)
//                .forEach(this::webScrap);
    }

    public void webScrap(final EtutorExercise etutorExercise) {
        if (!List.of(
                ExerciseType.TIP,
                ExerciseType.SCREEN,
                ExerciseType.GRAMMAR_NOTE,
                ExerciseType.SCREEN_CULTURAL,
                ExerciseType.SCREEN_CULINARY,
                ExerciseType.SCREEN_MUSIC
        ).contains(ExerciseType.valueOf(etutorExercise.getType()))) {
            return;
        }

        super.webScrap(etutorExercise);
    }
}
