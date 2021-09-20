package rwilk.exploreenglish.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class SoundUtils {

  private SoundUtils(){}

  public static void downloadFile(final String fieldText) {
    try {
      final URLConnection connection =
              new URL(fieldText).openConnection();
      final InputStream inputStream = connection.getInputStream();
      final OutputStream outputStream =
              new FileOutputStream("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\temp-file.mp3");

      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
      outputStream.close();

      playFile("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\temp-file.mp3");

    } catch (Exception e) {
      log.error("An exception occurred during downloading a file {}", fieldText);
    }
  }

  private static void playFile(final String pathToFile) {
    Media hit = new Media(new File(pathToFile).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(hit);
    mediaPlayer.play();
  }
}
