package rwilk.exploreenglish.scrapper.ewa.scrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaExercise;
import rwilk.exploreenglish.model.entity.ewa.EwaExerciseItem;
import rwilk.exploreenglish.repository.ewa.EwaExerciseItemRepository;
import rwilk.exploreenglish.repository.ewa.EwaExerciseRepository;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Image;
import rwilk.exploreenglish.scrapper.ewa.schema.lesson.EwaLessonResponse;
import rwilk.exploreenglish.scrapper.ewa.schema.lesson.Exercise;
import rwilk.exploreenglish.scrapper.ewa.schema.lesson.Media;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaExerciseItemScrapper implements CommandLineRunner {

    private final EwaExerciseItemRepository ewaExerciseItemRepository;
    private final EwaExerciseRepository ewaExerciseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
    }

    @Transactional
    public void webScrap(final EwaExercise ewaExercise) {
        final HttpClient client = HttpClient.newHttpClient();

        final String url = "https://api.appewa.com/api/v12/lessons/" + ewaExercise.getEwaId();
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

            if (response.statusCode() != 200) {
                throw new RuntimeException(response.body());
            }

            final List<EwaExerciseItem> ewaExerciseItems = new ArrayList<>();
            for (final Exercise exercise : ewaLessonResponse.getResult().getLesson().getExercises()) {
                final Media media = exercise.getMedia();
                final Image thumbnail = media.getVideo() != null ? media.getVideo().getThumbnail() : null;
                final Map<String, String> medium = media.getEncodedVideos() != null ? media.getEncodedVideos().getMedium() : Map.of();

                final Map.Entry<String, String> entry = media.getVoice() != null
                        ? media.getVoice().entrySet().stream().findFirst().orElse(null)
                        : null;

                final EwaExerciseItem ewaExerciseItem = EwaExerciseItem.builder()
                        .id(null)
                        .ewaId(exercise.get_id())
                        .type(exercise.getType())
                        .thumbnailS(thumbnail != null ? thumbnail.getS() : null)
                        .thumbnailM(thumbnail != null ? thumbnail.getM() : null)
                        .thumbnailL(thumbnail != null ? thumbnail.getL() : null)
                        .thumbnailXl(thumbnail != null ? thumbnail.getXl() : null)
                        .videoHevc(medium.get("hevc"))
                        .videoVp9(medium.get("vp9"))
                        .videoAv1(medium.get("av1"))
                        .voiceKey(entry != null ? entry.getKey() : null)
                        .voiceUrl(entry != null ? entry.getValue() : null)
                        .contentDescription(exercise.getContent().getDescription())
                        .contentText(exercise.getContent().getText())
                        .contentTranslation(exercise.getContent().getTranslation())
                        .jsonData(getExerciseJson(exercise))
                        .isVideoDownloaded(false)
                        .ewaExercise(ewaExercise)
                        .build();
                ewaExerciseItems.add(ewaExerciseItem);
            }

            ewaExerciseItemRepository.saveAll(ewaExerciseItems);
            ewaExercise.setIsReady(true);
            ewaExercise.setJsonData(response.body());
            ewaExerciseRepository.save(ewaExercise);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String getExerciseJson(final Exercise exercise) {
        try {
            return objectMapper.writeValueAsString(exercise);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
