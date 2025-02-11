package rwilk.exploreenglish.scrapper.langeek.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise;
import rwilk.exploreenglish.model.entity.langeek.LangeekLesson;
import rwilk.exploreenglish.repository.langeek.LangeekCourseRepository;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseRepository;
import rwilk.exploreenglish.repository.langeek.LangeekLessonRepository;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekExerciseScrapper {
    private static final String BASE_URL = "https://langeek.co";

    private final LangeekCourseRepository langeekCourseRepository;
    private final LangeekLessonRepository langeekLessonRepository;
    private final LangeekExerciseRepository langeekExerciseRepository;

    @SuppressWarnings("DataFlowIssue")
    @Transactional
    public void webScrap(final LangeekLesson langeekLesson) {
        try {
            log.info("Scraping langeek exercises for lesson: {}", langeekLesson.getName());
            final URL url = new URL(langeekLesson.getHref());
            final Document document = Jsoup.parse(url, 10000);

            final Elements exercises = document.selectFirst("main").child(1).child(0).child(0).child(0).children();

            final List<LangeekExercise> langeekExercises = new ArrayList<>();
            for (Element exercise : exercises) {
                document.selectFirst("main").child(1).child(0).child(0).child(0).children().get(0).select("h3.tw-text-base").text();
                final String name = exercise.select("h3.tw-text-base").text();
                final String href = exercise.select("span.tw-flex-none").select("a.tw-transition-all").attr("href");

                final LangeekExercise langeekExercise = LangeekExercise.builder()
                        .name(name)
                        .type(ExerciseType.WORDS_LIST.name())
                        .href(BASE_URL + href)
                        .image(null)
                        .lesson(langeekLesson)
                        .isReady(false)
                        .build();
                langeekExercises.add(langeekExercise);
            }

            langeekExerciseRepository.saveAll(langeekExercises);
            langeekLesson.setIsReady(true);
            langeekLessonRepository.save(langeekLesson);
            log.info("Saved langeek exercises for lesson: {}", langeekLesson.getName());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
