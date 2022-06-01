package rwilk.exploreenglish.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FormUtils {

  private FormUtils() {
  }

  public static boolean allFieldsFilled(List<Control> controls) {
    for (Control control : controls) {
      if (control instanceof TextField textField) {
        if (StringUtils.isEmpty(textField.getText())) {
          return false;
        }
      } else if (control instanceof ComboBox<?> comboBox && comboBox.getSelectionModel().getSelectedItem() == null) {
        return false;
      }
    }
    return true;
  }

  public static void clear(final List<Object> controls) {
    for (final Object control : controls) {
      if (control instanceof TextField textField) {
        textField.clear();
      } else if (control instanceof ComboBox<?> comboBox) {
        comboBox.getSelectionModel().clearSelection();
      } else if (control instanceof ToggleGroup toggleGroup) {
        toggleGroup.selectToggle(null);
      } else if (control instanceof ListView<?> listView) {
        listView.setItems(null);
      }
    }
  }

}
