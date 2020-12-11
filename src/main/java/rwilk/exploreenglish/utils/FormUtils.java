package rwilk.exploreenglish.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FormUtils {

  private FormUtils() {
  }

  public static boolean allFieldsFilled(List<Control> controls) {
    for (Control control : controls) {
      if (control instanceof TextField) {
        TextField textField = (TextField) control;
        if (StringUtils.isEmpty(textField.getText())) {
          return false;
        }
      } else if (control instanceof ComboBox) {
        ComboBox comboBox = (ComboBox) control;
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
          return false;
        }
      }
    }
    return true;
  }

  public static void clear(List<Object> controls) {
    for (Object control : controls) {
      if (control instanceof TextField) {
        TextField textField = (TextField) control;
        textField.clear();
      } else if (control instanceof ComboBox) {
        ComboBox comboBox = (ComboBox) control;
        comboBox.getSelectionModel().clearSelection();
      } else if (control instanceof ToggleGroup) {
        ToggleGroup toggleGroup = (ToggleGroup) control;
        toggleGroup.selectToggle(null);
      }
    }
  }

}
