package rwilk.exploreenglish.scrapper.ewa.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.scrapper.ewa.schema.lesson.EwaLessonResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class EwaLessonScrapper implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        scrapLesson("db6bea10-3178-46ae-885a-d06e986f8521");
    }

    public void scrapLesson(final String id) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpClient client = HttpClient.newHttpClient();

        final String url = "https://api.appewa.com/api/v12/lessons/" + id;
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Origin", "https://appewa.com")
                .header("Referer", "https://appewa.com/")
                .header("X-Session-id", "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5YmRlZTg0Mi00ZTNiLTRkYWEtODBmNC1iZmUyNDFiYzRkOTIiLCJyb2xlIjoidXNlciIsImxhbmciOiJwbCIsImlhdCI6MTczMzIxNDc4Mn0.fJ3INcoqMSlPuOByT12TC97GY1GeJDRKBhu0sMYMpIKODt8zJu1lQa0Xb7o8-8Xp")
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final EwaLessonResponse ewaLessonResponse = objectMapper.readValue(response.body(), EwaLessonResponse.class);
            System.out.println(ewaLessonResponse);
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }



}
