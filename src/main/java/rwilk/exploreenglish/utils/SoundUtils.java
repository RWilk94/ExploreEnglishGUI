package rwilk.exploreenglish.utils;

import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class SoundUtils {

  private SoundUtils(){}

  public static void playSound(final TextField textField) {
    final String fieldText = textField.getText();
    if (StringUtils.isNotBlank(fieldText) && fieldText.contains("https://www")) {
      final String trimmedText = trim(fieldText);

      textField.setText(trimmedText);
      SoundUtils.downloadFile(trimmedText);

      final ClipboardContent content = new ClipboardContent();
      content.putString(trimmedText);
      Clipboard.getSystemClipboard().setContent(content);
    }
  }

  public static void downloadFile(final String fieldText) {
    try {
      final URLConnection connection = new URL(trim(fieldText)).openConnection();
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

  private static String trim(final String fieldText) {
    if (fieldText.contains("?version=")) {
      return fieldText.substring(0, fieldText.indexOf("?version"));
    }
    return fieldText;
  }
}
