package rwilk.exploreenglish.scrapper.langeek.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.LangeekDictionary;
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise;
import rwilk.exploreenglish.repository.langeek.LangeekDictionaryRepository;
import rwilk.exploreenglish.repository.langeek.LangeekExerciseRepository;
import rwilk.exploreenglish.scrapper.langeek.schema.exercise.LangeekDictionaryExerciseResponse;
import rwilk.exploreenglish.scrapper.langeek.schema.word.LangeekDictionaryWordResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekDictionaryScrapper {
    private final LangeekDictionaryRepository langeekDictionaryRepository;
    private final LangeekExerciseRepository langeekExerciseRepository;

    public LangeekDictionaryExerciseResponse webScrapExercise(final LangeekExercise langeekExercise) {
        final ObjectMapper objectMapper = new ObjectMapper();
        if (langeekExercise.getJsonData() != null) {
            try {
                log.info("[fetchLangeekExercise] Fetched from cache: {}", langeekExercise.getName());
                return objectMapper.readValue(langeekExercise.getJsonData(), LangeekDictionaryExerciseResponse.class);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } else {
            log.info("[fetchLangeekExercise] Fetched from web: {}", langeekExercise.getName());
            final String href = langeekExercise.getHref();
            final HttpClient client = HttpClient.newHttpClient();

            final int id = Integer.parseInt(href.substring(href.lastIndexOf("subcategory/") + 12, href.lastIndexOf("/word-list")));
            final String url = "https://langeek.co/_next/data/tUmY4WxKKuxaSo8_0o3D1/en-PL/vocab/subcategory/" + id + "/word-list.json?locale=en-PL&id=" + id;

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            try {
                final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                langeekExercise.setJsonData(response.body());
                langeekExerciseRepository.save(langeekExercise);

                return objectMapper.readValue(response.body(), LangeekDictionaryExerciseResponse.class);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public LangeekDictionaryWordResponse webScrap(final Long wordId, final String language) {
        final LangeekDictionary cachedEntry = langeekDictionaryRepository.findByLangeekIdAndLanguage(wordId, language);
        if (cachedEntry != null) {
            final ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info("[fetchLangeekDictionaryEntry] Fetched from cache: {}", wordId);
                return objectMapper.readValue(cachedEntry.getJsonData(), LangeekDictionaryWordResponse.class);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        log.info("[fetchLangeekDictionaryEntry] Fetched from web: {}", wordId);
        final String url = "https://dictionary.langeek.co/_next/data/jhx9FlB2G5-B3s04rKsis/" + language + "/word/" + wordId + ".json?locale=" + language + "&wordId=" + wordId;

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            langeekDictionaryRepository.save(LangeekDictionary.builder()
                    .langeekId(wordId)
                    .language(language)
                    .jsonData(response.body())
                    .build());

            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), LangeekDictionaryWordResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
