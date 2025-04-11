package rwilk.exploreenglish.scrapper.ewa.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaExercise;
import rwilk.exploreenglish.model.entity.ewa.EwaLesson;
import rwilk.exploreenglish.repository.ewa.EwaExerciseRepository;
import rwilk.exploreenglish.repository.ewa.EwaLessonRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaExerciseScrapper implements CommandLineRunner {

    private final EwaExerciseRepository ewaExerciseRepository;
    private final EwaLessonRepository ewaLessonRepository;

    @Override
    public void run(String... args) throws Exception {

    }

    @Transactional
    public void webScrap(final EwaLesson lesson) {
        log.info("[EwaExerciseScrapper] webScrap lesson: {}", lesson.getId());

        final EwaExercise ewaExercise = EwaExercise.builder()
                .id(null)
                .ewaId(lesson.getEwaId())
                .kind(lesson.getKind())
                .title(lesson.getTitle())
                .origin(lesson.getOrigin())
                .imageId(lesson.getImageId())
                .imageS(lesson.getImageS())
                .imageM(lesson.getImageM())
                .imageL(lesson.getImageL())
                .imageXl(lesson.getImageXl())
                .isAdult(lesson.getIsAdult())
                .isReady(false)
                .number(lesson.getNumber())
                .jsonData(null)
                .ewaLesson(lesson)
                .build();

        ewaExerciseRepository.save(ewaExercise);
        lesson.setIsReady(true);
        ewaLessonRepository.save(lesson);
    }
}
