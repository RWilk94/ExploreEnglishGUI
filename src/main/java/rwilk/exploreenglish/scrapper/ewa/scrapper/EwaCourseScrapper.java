package rwilk.exploreenglish.scrapper.ewa.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaCourse;
import rwilk.exploreenglish.repository.ewa.EwaCourseRepository;
import rwilk.exploreenglish.scrapper.ewa.schema.course2.Course;
import rwilk.exploreenglish.scrapper.ewa.schema.course2.EwaCourseResultResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaCourseScrapper implements CommandLineRunner {

    private final EwaCourseRepository ewaCourseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        // webScrap();
    }

    @Transactional
    public void webScrap() {
        log.info("[EwaCourseScrapper] webScrap");
        final HttpClient client = HttpClient.newHttpClient();

        final String url = "https://api.appewa.com/api/v12/courses";
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Origin", "https://appewa.com")
                .header("Referer", "https://appewa.com/")
                .header("X-Session-id", "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2OTc0MWZhOC1kY2UwLTRmODktOWU3NC1jNmYzMGI2ODA1YzMiLCJuYW1lIjoiUmFmYcWCIFdpbGsiLCJyb2xlIjoidXNlciIsImxhbmciOiJwbCIsImlhdCI6MTc0NDI4NTM3NH0._ZSr3AI801wcJBFCAZqS78XTcWJ6K5zoxAQt4rG5N92rPZp6CDVrZ9l4UCGFcwUY")
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final EwaCourseResultResponse ewaCourseResultResponse = objectMapper.readValue(response.body(), EwaCourseResultResponse.class);

            final List<EwaCourse> ewaCourses = new ArrayList<>();
            for (final Course course : ewaCourseResultResponse.getResult().getRows()) {
                final EwaCourse ewaCourse = EwaCourse.builder()
                        .id(null)
                        .ewaId(course.getId())
                        .name(course.getTitle())
                        .description(course.getDescription())
                        .imageId(course.getImageId())
                        .imageS(course.getImage() != null ? course.getImage().getS() : null)
                        .imageM(course.getImage() != null ? course.getImage().getM() : null)
                        .imageL(course.getImage() != null ? course.getImage().getL() : null)
                        .imageXl(course.getImage() != null ? course.getImage().getXl() : null)
                        .isReady(false)
                        .jsonData(response.body())
                        .build();
                ewaCourses.add(ewaCourse);
            }
            ewaCourseRepository.saveAll(ewaCourses);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
