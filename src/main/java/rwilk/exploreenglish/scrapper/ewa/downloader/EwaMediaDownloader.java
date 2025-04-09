package rwilk.exploreenglish.scrapper.ewa.downloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaExerciseItem;
import rwilk.exploreenglish.repository.ewa.EwaExerciseItemRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaMediaDownloader implements CommandLineRunner {

    private final static String BASE_URL = "https://storage.appewa.com/api/v1/files/";
    private final EwaExerciseItemRepository ewaExerciseItemRepository;

    @Override
    public void run(String... args) throws Exception {
        ewaExerciseItemRepository.findAllByIsVideoDownloadedOrIsVoiceDownloaded(false, false)
                .forEach(this::downloadMedia);
    }

    @Transactional
    public void downloadMedia(final EwaExerciseItem ewaExerciseItem) {
        downloadVideo(ewaExerciseItem);
        downloadVoice(ewaExerciseItem);

        ewaExerciseItemRepository.save(ewaExerciseItem);
    }

    private void downloadVideo(final EwaExerciseItem ewaExerciseItem) {
        if (ewaExerciseItem.getVideoHevc() == null) {
            log.warn("No video path found for exercise item: {}", ewaExerciseItem.getId());
            ewaExerciseItem.setIsVideoDownloaded(true);
            return;
        }

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + ewaExerciseItem.getVideoHevc()))
                .GET()
                .build();

        try {
            log.info("Downloading {}", getVideoDestinationPath(ewaExerciseItem));
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                final File file = new File(getVideoDestinationPath(ewaExerciseItem));
                final File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }

                try (final InputStream inputStream = response.body();
                     final FileOutputStream outputStream = new FileOutputStream(file)) {

                    final byte[] buffer = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    ewaExerciseItem.setIsVideoDownloaded(true);
                }
            } else {
                log.error("Can't download the file. Video path: {}",  getVideoDestinationPath(ewaExerciseItem));
                throw new IOException("Can't download the file. Error code: " + response.statusCode());
            }

        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void downloadVoice(final EwaExerciseItem ewaExerciseItem) {
        if (ewaExerciseItem.getVoiceUrl() == null) {
            log.warn("No voice path found for exercise item: {}", ewaExerciseItem.getId());
            ewaExerciseItem.setIsVoiceDownloaded(true);
            return;
        }

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ewaExerciseItem.getVoiceUrl()))
                .GET()
                .build();

        try {
            log.info("Downloading {}", getVoiceDestinationPath(ewaExerciseItem));
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                final File file = new File(getVoiceDestinationPath(ewaExerciseItem));
                final File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }

                try (final InputStream inputStream = response.body();
                     final FileOutputStream outputStream = new FileOutputStream(file)) {

                    final byte[] buffer = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    ewaExerciseItem.setIsVoiceDownloaded(true);
                }
            } else {
                log.error("Can't download the file. Voice path: {}", getVoiceDestinationPath(ewaExerciseItem));
                throw new IOException("Can't download the file. Error code: " + response.statusCode());
            }

        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getVoiceKey(final EwaExerciseItem ewaExerciseItem) {
        if (ewaExerciseItem.getVoiceKey() != null) {
            return ewaExerciseItem.getVoiceKey().replaceAll("[^a-zA-Z0-9 ]", "");
        } else if (ewaExerciseItem.getContentTranslation() != null) {
            return ewaExerciseItem.getContentTranslation().replaceAll("[^a-zA-Z0-9 ]", "");
        } else {
            throw new RuntimeException("No voice key found for exercise item: " + ewaExerciseItem.getId());
        }
    }

    private String getVideoDestinationPath(final EwaExerciseItem ewaExerciseItem) {
        return "ewa-video/"
                + ewaExerciseItem.getEwaExercise().getId().toString()
                + "/"
                + ewaExerciseItem.getId().toString()
                + "_"
                + getVoiceKey(ewaExerciseItem)
                + ".mp4";
    }

    private String getVoiceDestinationPath(final EwaExerciseItem ewaExerciseItem) {
        return "ewa-video/"
                + ewaExerciseItem.getEwaExercise().getId().toString()
                + "/"
                + ewaExerciseItem.getId().toString()
                + "_"
                + getVoiceKey(ewaExerciseItem)
                + ".mp3";
    }
}
