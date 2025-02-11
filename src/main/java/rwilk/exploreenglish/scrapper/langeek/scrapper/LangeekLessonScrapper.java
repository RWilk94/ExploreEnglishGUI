package rwilk.exploreenglish.scrapper.langeek.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.langeek.LangeekCourse;
import rwilk.exploreenglish.model.entity.langeek.LangeekLesson;
import rwilk.exploreenglish.repository.langeek.LangeekCourseRepository;
import rwilk.exploreenglish.repository.langeek.LangeekLessonRepository;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekLessonScrapper {
    private static final String BASE_URL = "https://langeek.co";

    private final LangeekCourseRepository langeekCourseRepository;
    private final LangeekLessonRepository langeekLessonRepository;

    @SuppressWarnings("DataFlowIssue")
    @Transactional
    public void webScrap(final LangeekCourse langeekCourse) {
        try {
            log.info("Scraping langeek lessons for course: {}", langeekCourse.getName());
            final URL url = new URL(langeekCourse.getHref());
            final Document document = Jsoup.parse(url, 10000);
            final Elements lessons = document.selectFirst("div.tw-grid").children();

            final List<LangeekLesson> langeekLessons = new ArrayList<>();

            for (Element lesson : lessons) {
                final String lessonTitle = lesson.select("h3").first().child(0).child(0).text();
                final String lessonDescription = lesson.child(0).child(1).child(1).text();
                final String lessonHref = lesson.select("a.tw-absolute").attr("href");
                final String img = lesson.selectFirst("img").attr("src");

                final LangeekLesson langeekLesson = LangeekLesson.builder()
                        .name(lessonTitle)
                        .description(lessonDescription)
                        .href(BASE_URL + lessonHref)
                        .image(img)
                        .course(langeekCourse)
                        .isReady(false)
                        .build();
                langeekLessons.add(langeekLesson);
            }

            langeekLessonRepository.saveAll(langeekLessons);
            langeekCourse.setIsReady(true);
            langeekCourseRepository.save(langeekCourse);
            log.info("Saved langeek lessons for course: {}", langeekCourse.getName());
        } catch (
                IOException e) {
            log.error(e.getMessage());
        }
    }

}
