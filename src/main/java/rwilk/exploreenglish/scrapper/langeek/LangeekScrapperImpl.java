package rwilk.exploreenglish.scrapper.langeek;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.LangeekCourse;
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise;
import rwilk.exploreenglish.model.entity.langeek.LangeekLesson;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseRepository;
import rwilk.exploreenglish.scrapper.langeek.scrapper.*;

import java.util.List;

@Order(999)
@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekScrapperImpl implements LangeekScrapper, CommandLineRunner {

    private final LangeekCourseScrapper langeekCourseScrapper;
    private final LangeekLessonScrapper langeekLessonScrapper;
    private final LangeekExerciseScrapper langeekExerciseScrapper;
    private final LangeekWordScrapper langeekWordScrapper;
    private final LangeekWordV2Scrapper langeekWordV2Scrapper;
    private final LangeekExerciseRepository langeekExerciseRepository;

    @SuppressWarnings("unused")
    private static final List<String> courses = List.of(
            "https://langeek.co/en-PL/vocab/level-based",
            "https://langeek.co/en-PL/vocab/book-collection/5/new-english-file",
            "https://langeek.co/en-PL/vocab/book-collection/6/headway",
            "https://langeek.co/en-PL/vocab/book-collection/7/top-notch",
            "https://langeek.co/en-PL/vocab/book-collection/8/solutions",
            "https://langeek.co/en-PL/vocab/book-collection/9/english-result",
            "https://langeek.co/en-PL/vocab/book-collection/10/four-corners",
            "https://langeek.co/en-PL/vocab/book-collection/11/face2face",
            "https://langeek.co/en-PL/vocab/book-collection/12/insight",
            "https://langeek.co/en-PL/vocab/book-collection/13/total-english",
            "https://langeek.co/en-PL/vocab/book-collection/14/interchange",
            "https://langeek.co/en-PL/vocab/book-collection/2/sat",
            "https://langeek.co/en-PL/vocab/book-collection/3/toefl",
            "https://langeek.co/en-PL/vocab/book-collection/4/gre",
            "https://langeek.co/en-PL/vocab/book-collection/20/ielts-general",
            "https://langeek.co/en-PL/vocab/book-collection/21/ielts-academic",
            "https://langeek.co/en-PL/vocab/book-collection/23/act-test",
            "https://langeek.co/en-PL/vocab/book-collection/24/SAT",
            "https://langeek.co/en-PL/vocab/most-common",
            "https://langeek.co/en-PL/vocab/book-collection/16/adverbs",
            "https://langeek.co/en-PL/vocab/book-collection/17/verbs",
            "https://langeek.co/en-PL/vocab/book-collection/18/adjectives",
            "https://langeek.co/en-PL/vocab/book-collection/19/other-pos",
            "https://langeek.co/en-PL/vocab/idioms",
            "https://langeek.co/en-PL/vocab/collocations",
            "https://langeek.co/en-PL/vocab/proverbs",
            "https://langeek.co/en-PL/vocab/phrasal-verbs",
            "https://langeek.co/en-PL/vocab/topic-related"
    );

    @Override
    public void run(String... args) throws Exception {
        langeekExerciseRepository.findAllByIsReady(false)
                // .stream()
                // .filter(exercise -> exercise.getLesson().getId() >= 7 && exercise.getLesson().getId() <= 12)
                .forEach(langeekExercise -> {
                    try {
//                        Thread.sleep(1000);
                    webScrapWords(langeekExercise);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        log.error("Error: {}", e.getMessage());
                    }
                });
    }

    @Override
    public void webScrapCourse(final String courseUrl) {
        langeekCourseScrapper.webScrap(courseUrl);
    }

    @Override
    public void webScrapLessons(final LangeekCourse langeekCourse) {
        langeekLessonScrapper.webScrap(langeekCourse);
    }

    @Override
    public void webScrapExercises(final LangeekLesson langeekLesson) {
        langeekExerciseScrapper.webScrap(langeekLesson);
    }

    @Override
    public void webScrapWords(final LangeekExercise langeekExercise) {
        langeekWordV2Scrapper.webScrap(langeekExercise);
    }
}
