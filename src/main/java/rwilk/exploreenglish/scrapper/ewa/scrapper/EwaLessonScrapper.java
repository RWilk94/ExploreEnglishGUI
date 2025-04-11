package rwilk.exploreenglish.scrapper.ewa.scrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaCourse;
import rwilk.exploreenglish.model.entity.ewa.EwaLesson;
import rwilk.exploreenglish.repository.ewa.EwaCourseRepository;
import rwilk.exploreenglish.repository.ewa.EwaLessonRepository;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Lesson;
import rwilk.exploreenglish.scrapper.ewa.schema.lesson2.EwaLesson2Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaLessonScrapper implements CommandLineRunner {

    private final EwaCourseRepository ewaCourseRepository;
    private final EwaLessonRepository ewaLessonRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        // scrapLesson("db6bea10-3178-46ae-885a-d06e986f8521");
    }

    @Transactional
    public void webScrap(final EwaCourse ewaCourse) {
        log.info("[EwaLessonScrapper] webScrap course: {}", ewaCourse.getId());
        final HttpClient client = HttpClient.newHttpClient();

        final String url = "https://api.appewa.com/api/v12/courses/" + ewaCourse.getEwaId();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Origin", "https://appewa.com")
                .header("Referer", "https://appewa.com/")
                .header("X-Session-id", "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2OTc0MWZhOC1kY2UwLTRmODktOWU3NC1jNmYzMGI2ODA1YzMiLCJuYW1lIjoiUmFmYcWCIFdpbGsiLCJyb2xlIjoidXNlciIsImxhbmciOiJwbCIsImlhdCI6MTc0NDI4NTM3NH0._ZSr3AI801wcJBFCAZqS78XTcWJ6K5zoxAQt4rG5N92rPZp6CDVrZ9l4UCGFcwUY")
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final EwaLesson2Response ewaLesson2Response = objectMapper.readValue(response.body(), EwaLesson2Response.class);

            final List<EwaLesson> ewaLessons = new ArrayList<>();
            for (final Lesson lesson : ewaLesson2Response.getResult().getCourse().getLessons()) {
                final EwaLesson ewaLesson = EwaLesson.builder()
                        .id(null)
                        .ewaId(lesson.getId())
                        .isAdult(lesson.isAdult())
                        .kind(lesson.getKind())
                        .title(lesson.getTitle())
                        .origin(lesson.getOrigin())
                        .number(lesson.getNumber())
                        .imageId(lesson.getImageId())
                        .imageS(lesson.getImage() != null ? lesson.getImage().getS() : null)
                        .imageM(lesson.getImage() != null ? lesson.getImage().getM() : null)
                        .imageL(lesson.getImage() != null ? lesson.getImage().getL() : null)
                        .imageXl(lesson.getImage() != null ? lesson.getImage().getXl() : null)
                        .jsonData(getEwaLessonJson(lesson))
                        .isReady(false)
                        .course(ewaCourse)
                        .build();

                ewaLessons.add(ewaLesson);
            }

            ewaLessonRepository.saveAll(ewaLessons);
            ewaCourse.setIsReady(true);
            ewaCourse.setJsonData(response.body());
            ewaCourseRepository.save(ewaCourse);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private String getEwaLessonJson(final Lesson lesson) {
        try {
            return objectMapper.writeValueAsString(lesson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
