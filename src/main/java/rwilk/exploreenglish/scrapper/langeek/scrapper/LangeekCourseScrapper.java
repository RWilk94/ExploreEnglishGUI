package rwilk.exploreenglish.scrapper.langeek.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.LanguageEnum;
import rwilk.exploreenglish.model.entity.langeek.LangeekCourse;
import rwilk.exploreenglish.repository.langeek.LangeekCourseRepository;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekCourseScrapper {

    private final LangeekCourseRepository langeekCourseRepository;

    @SuppressWarnings("DataFlowIssue")
    @Transactional
    public void webScrap(final String courseUrl) {
        try {
            log.info("Scraping langeek course: {}", courseUrl);
            final URL url = new URL(courseUrl);

            final Document document = Jsoup.parse(url, 10000);

            final String name = document.select("h1").text();
            final String description = document.selectFirst("h1").parent().child(1).text();

            final LangeekCourse langeekCourse = LangeekCourse.builder()
                    .name(name)
                    .description(description)
                    .href(courseUrl)
                    .image(null)
                    .isReady(false)
                    .language(LanguageEnum.ENGLISH.name())
                    .build();

            langeekCourseRepository.save(langeekCourse);
            log.info("Saved langeek course: {}", langeekCourse.getName());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
