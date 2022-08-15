package rwilk.exploreenglish.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import lombok.extern.slf4j.Slf4j;

import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
      final String fileName = fieldText.substring(fieldText.lastIndexOf("/") + 1);
      final URLConnection connection = new URL(trim(fieldText)).openConnection();
      final InputStream inputStream = connection.getInputStream();
      final OutputStream outputStream =
              new FileOutputStream("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\" + fileName);

      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
      outputStream.close();
      inputStream.close();

      Mp3File mp3file = new Mp3File("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\" + fileName);
      if (mp3file.hasId3v2Tag()) {
        final ID3v2 tag = mp3file.getId3v2Tag();
        tag.clearFrameSet("TIT2");
        tag.clearFrameSet("TPE1");
        tag.clearFrameSet("WOAR");
        mp3file.save("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\_" + fileName);
      }

      playFile("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\_" + fileName);

    } catch (Exception e) {
      log.error("An exception occurred during downloading a file {}", fieldText);
    }
  }

  public static String checkIfFileExists(final String fieldText) {
    try {
      final String fileName = fieldText.substring(fieldText.lastIndexOf("/") + 1);
      final URLConnection connection = new URL(trim(fieldText)).openConnection();
      final InputStream inputStream = connection.getInputStream();
      final OutputStream outputStream =
        new FileOutputStream("C:\\Corelogic\\TAX\\ExploreEnglishGUI\\files\\" + fileName);

      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
      outputStream.close();
      inputStream.close();

      return fieldText;

    } catch (Exception e) {
      log.error("An exception occurred during downloading a file {}", fieldText);
      return null;
    }
  }

  private static void playFile(final String pathToFile) {
    Media hit = new Media(new File(pathToFile).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(hit);
    mediaPlayer.setOnError(() -> log.error("An exception occurred during playing a file {}, root cause: {} ", pathToFile, mediaPlayer.getError().toString()));
    mediaPlayer.play();
  }

  private static String trim(final String fieldText) {
    if (fieldText.contains("?version=")) {
      return fieldText.substring(0, fieldText.indexOf("?version"));
    }
    return fieldText;
  }
}
