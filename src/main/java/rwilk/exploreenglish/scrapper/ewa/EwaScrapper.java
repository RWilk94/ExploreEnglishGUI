package rwilk.exploreenglish.scrapper.ewa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Child;
import rwilk.exploreenglish.scrapper.ewa.schema.course.EwaCourseResponse;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Lesson;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Result;
import rwilk.exploreenglish.scrapper.ewa.scrapper.EwaCourseScrapper;
import rwilk.exploreenglish.scrapper.ewa.scrapper.EwaLessonScrapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaScrapper implements CommandLineRunner {
    private final EwaCourseScrapper ewaCourseScrapper;
    private final EwaLessonScrapper ewaLessonScrapper;

    @Override
    public void run(String... args) throws Exception {
        // webScrapEwaApp();
    }

    public void webScrapEwaApp() {
        final EwaCourseResponse ewaCourseResponse = ewaCourseScrapper.webScrapCourses();

        for (final Result result : ewaCourseResponse.getResult()) {
            System.out.println(result.getTitle());
            // System.out.println(result.getId());

            for (final Child child : result.getChilds()) {

                for (final Lesson lesson : child.getLessons()) {
                    System.out.println(lesson.getTitle());
                    // System.out.println(lesson.getId());
                }
            }
            System.out.println("-------------------------------------------------");
        }
    }
}
