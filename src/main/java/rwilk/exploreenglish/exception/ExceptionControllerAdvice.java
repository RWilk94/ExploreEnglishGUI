package rwilk.exploreenglish.exception;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionControllerAdvice {

  public static final String NOT_FOUND_WORD_INSTANCE = "Not found WORD instance.";
  public static final String NOT_FOUND_WORD_SOUND_INSTANCE = "Not found WORD_SOUND instance.";

  private ExceptionControllerAdvice() {
  }

  public static void showError(final Thread t, final Throwable e) {
    log.error(e.getMessage(), e);

    if (Platform.isFxApplicationThread()) {
      showErrorDialog(e);
    } else {
      log.error("An unexpected error occurred in " + t);
    }
  }

  private static void showErrorDialog(final Throwable e) {
    final Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Validation Error");
    alert.setHeaderText(null);
    alert.setContentText(extractErrorMessage(e.getCause().getCause()));
    alert.showAndWait();
  }

  private static String extractErrorMessage(final Throwable e) {
    if (e == null) {
      return "";
    }
    return e.getMessage();
  }

}
