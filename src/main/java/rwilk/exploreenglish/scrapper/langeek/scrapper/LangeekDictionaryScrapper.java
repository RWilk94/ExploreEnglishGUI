package rwilk.exploreenglish.scrapper.langeek.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.model.entity.langeek.LangeekDictionary;
import rwilk.exploreenglish.repository.langeek.LangeekDictionaryRepository;
import rwilk.exploreenglish.scrapper.langeek.schema.LangeekResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangeekDictionaryScrapper {
    private final LangeekDictionaryRepository langeekDictionaryRepository;

    public LangeekResponse webScrap(final Long wordId, final String language) {
        final LangeekDictionary cachedEntry = langeekDictionaryRepository.findByLangeekIdAndLanguage(wordId, language);
        if (cachedEntry != null) {
            final ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info("[fetchLangeekDictionaryEntry] Fetched from cache: {}", wordId);
                return objectMapper.readValue(cachedEntry.getJsonData(), LangeekResponse.class);
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
            return objectMapper.readValue(response.body(), LangeekResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
