package rwilk.exploreenglish.scrapper.ewa.scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaCourse;
import rwilk.exploreenglish.repository.ewa.EwaCourseRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaCourseScrapper implements CommandLineRunner {

    private final EwaCourseRepository ewaCourseRepository;

    @Override
    public void run(String... args) throws Exception {
         // webScrap();
    }

    @Transactional
    public void webScrap() {
        if (!ewaCourseRepository.findAll().isEmpty()) {
            return;
        }

        final HttpClient client = HttpClient.newHttpClient();

        final String url = "https://api.appewa.com/api/v12/courses/roadmap";
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Origin", "https://appewa.com")
                .header("Referer", "https://appewa.com/")
                .header("X-Session-id", "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5YmRlZTg0Mi00ZTNiLTRkYWEtODBmNC1iZmUyNDFiYzRkOTIiLCJyb2xlIjoidXNlciIsImxhbmciOiJwbCIsImlhdCI6MTczMzIxNDc4Mn0.fJ3INcoqMSlPuOByT12TC97GY1GeJDRKBhu0sMYMpIKODt8zJu1lQa0Xb7o8-8Xp")
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final EwaCourse ewaCourse = EwaCourse.builder()
                    .id(null)
                    .name("Ewa Video Course")
                    .jsonData(response.body())
                    .build();

            ewaCourseRepository.save(ewaCourse);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
