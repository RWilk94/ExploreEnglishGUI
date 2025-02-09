package rwilk.exploreenglish.scrapper.langeek;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.scrapper.langeek.schema.Example;
import rwilk.exploreenglish.scrapper.langeek.schema.LangeekResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class LangeekScrapper implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        webScrap();
    }

    public void webScrap() {
        String url = "https://dictionary.langeek.co/_next/data/jhx9FlB2G5-B3s04rKsis/en-PL/word/18288.json?locale=en-PL&id=18288&entry=big";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Konwersja JSON -> obiekt
            ObjectMapper objectMapper = new ObjectMapper();
            LangeekResponse langeekResponse = objectMapper.readValue(response.body(), LangeekResponse.class);

            System.out.println(langeekResponse);

            int id = langeekResponse.getPageProps()
                    .getInitialState()
                    .getStaticData()
                    .getWordEntry()
                    .getWords()
                    .get(0)
                    .getTranslations()
                    .get(0)
                    .getId();

            List<Example> examples = langeekResponse.getPageProps()
                    .getInitialState()
                    .getStaticData()
                    .getSimpleExamples()
                    .get(String.valueOf(id));

            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
