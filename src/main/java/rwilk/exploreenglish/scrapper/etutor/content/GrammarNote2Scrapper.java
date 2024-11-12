package rwilk.exploreenglish.scrapper.etutor.content;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.entity.etutor.EtutorExercise;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorNoteRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

@Component
public class GrammarNote2Scrapper extends Note2BaseScrapper implements CommandLineRunner {

    public GrammarNote2Scrapper(EtutorExerciseRepository etutorExerciseRepository,
                                EtutorNoteRepository etutorNoteRepository) {
        super(etutorExerciseRepository, etutorNoteRepository);
    }

    @Override
    public void run(String... args) throws Exception {
//        etutorExerciseRepository.findAllByTypeAndIsReady(ExerciseType.GRAMMAR_NOTE.toString(), false)
//                .subList(0, 20)
//                .forEach(this::webScrap);
    }

    public void webScrap(final EtutorExercise etutorExercise) {
        if (ExerciseType.SCREEN != ExerciseType.valueOf(etutorExercise.getType())) {
            return;
        }

        super.webScrap(etutorExercise);
    }
}
