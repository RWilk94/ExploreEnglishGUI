package rwilk.exploreenglish.scrapper.ewa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.ewa.EwaCourseRepository;
import rwilk.exploreenglish.repository.ewa.EwaExerciseRepository;
import rwilk.exploreenglish.repository.ewa.EwaLessonRepository;
import rwilk.exploreenglish.scrapper.ewa.scrapper.EwaCourseScrapper;
import rwilk.exploreenglish.scrapper.ewa.scrapper.EwaExerciseItemScrapper;
import rwilk.exploreenglish.scrapper.ewa.scrapper.EwaExerciseScrapper;
import rwilk.exploreenglish.scrapper.ewa.scrapper.EwaLessonScrapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaScrapper implements CommandLineRunner {
    private final EwaCourseScrapper ewaCourseScrapper;
    private final EwaLessonScrapper ewaLessonScrapper;
    private final EwaExerciseScrapper ewaExerciseScrapper;
    private final EwaExerciseItemScrapper ewaExerciseItemScrapper;
    private final EwaCourseRepository ewaCourseRepository;
    private final EwaLessonRepository ewaLessonRepository;
    private final EwaExerciseRepository ewaExerciseRepository;

    @Override
    public void run(String... args) throws Exception {
        webScrapEwaApp();
    }

    public void webScrapEwaApp() {
        if (ewaCourseRepository.count() == 0) {
            ewaCourseScrapper.webScrap();
        }

        ewaCourseRepository.findAllByIsReady(false)
                .forEach(ewaLessonScrapper::webScrap);

        ewaLessonRepository.findAllByIsReady(false)
                .forEach(ewaExerciseScrapper::webScrap);

        ewaExerciseRepository.findAllByIsReady(false)
                .forEach(exercise -> {
                    try {
                        Thread.sleep(1000);
                        ewaExerciseItemScrapper.webScrap(exercise);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                });
    }
}
