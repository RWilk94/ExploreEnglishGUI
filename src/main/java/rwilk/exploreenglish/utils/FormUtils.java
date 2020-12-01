package rwilk.exploreenglish.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.NonNull;
import rwilk.exploreenglish.custom.ToggleGroup2;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;

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

  public static Word getWord(Word word, List<Object> controls) {
    for (Object control : controls) {
      if (control instanceof TextField) {
        TextField textField = (TextField) control;
        setWordField(word, getFieldId(textField), textField);
      } else if (control instanceof ComboBox) {
        ComboBox comboBox = (ComboBox) control;
        setWordField(word, getFieldId(comboBox), comboBox);
      } else if (control instanceof ToggleGroup2) {
        ToggleGroup2 toggleGroup2 = (ToggleGroup2) control;
        setWordField(word, getFieldId(toggleGroup2), toggleGroup2);
      }
    }
    return word;
  }

  public static Word setWordForm(Word word, List<Object> controls) {
    for (Object control : controls) {
      if (control instanceof TextField) {
        TextField textField = (TextField) control;
        setFieldFromWord(word, getFieldId(textField), textField);
      } else if (control instanceof ComboBox) {
        ComboBox comboBox = (ComboBox) control;
        setFieldFromWord(word, getFieldId(comboBox), comboBox);
      } else if (control instanceof ToggleGroup2) {
        ToggleGroup2 toggleGroup2 = (ToggleGroup2) control;
        setFieldFromWord(word, getFieldId(toggleGroup2), toggleGroup2);
      }
    }
    return word;
  }

  public static Term setWordForm(Term term, List<Object> controls) {
    for (Object control : controls) {
      if (control instanceof TextField) {
        TextField textField = (TextField) control;
        setFieldFromWord(term, getFieldId(textField), textField);
      } else if (control instanceof ComboBox) {
        ComboBox comboBox = (ComboBox) control;
        setFieldFromWord(term, getFieldId(comboBox), comboBox);
      } else if (control instanceof ToggleGroup2) {
        ToggleGroup2 toggleGroup2 = (ToggleGroup2) control;
        setFieldFromWord(term, getFieldId(toggleGroup2), toggleGroup2);
      }
    }
    return term;
  }

  private static void setWordField(@NonNull Word word, @NonNull String id, Object field) {
    switch (id) {
      case "textFieldId":
        word.setId(getLong(field));
        break;
      case "textFieldEnglishName":
        word.setEnglishName(getString(field));
        break;
      case "textFieldAmericanName":
        word.setAmericanName(getString(field));
        break;
      case "textFieldOtherNames":
        word.setOtherName(getString(field));
        break;
      case "textFieldPolishName":
        word.setPolishName(getString(field));
        break;
      case "toggleGroupPartOfSpeech":
        word.setPartOfSpeech(getString(field));
        break;
      case "toggleGroupArticle":
        word.setArticle(getString(field));
        break;
      case "textFieldComparative":
        word.setComparative(getString(field));
        break;
      case "textFieldSuperlative":
        word.setSuperlative(getString(field));
        break;
      case "textFieldPastTense":
        word.setPastTense(getString(field));
        break;
      case "textFieldPastParticiple":
        word.setPastParticiple(getString(field));
        break;
      case "textFieldPlural":
        word.setPlural(getString(field));
        break;
      case "textFieldSynonym":
        word.setSynonym(getString(field));
        break;
      case "comboBoxLesson":
        word.setLesson(getLesson(field));
        break;
    }
  }

  private static void setFieldFromWord(@NonNull Word word, @NonNull String id, Object field) {
    switch (id) {
      case "textFieldId":
        setTextField((TextField) field, word.getId().toString());
        break;
      case "textFieldEnglishName":
        setTextField((TextField) field, word.getEnglishName());
        break;
      case "textFieldAmericanName":
        setTextField((TextField) field, word.getAmericanName());
        break;
      case "textFieldOtherNames":
        setTextField((TextField) field, word.getOtherName());
        break;
      case "textFieldPolishName":
        setTextField((TextField) field, word.getPolishName());
        break;
      case "toggleGroupPartOfSpeech":
        setToggleGroup2((ToggleGroup2) field, word.getPartOfSpeech());
        break;
      case "toggleGroupArticle":
        setToggleGroup2((ToggleGroup2) field, word.getArticle());
        break;
      case "textFieldComparative":
        setTextField((TextField) field, word.getComparative());
        break;
      case "textFieldSuperlative":
        setTextField((TextField) field, word.getSuperlative());
        break;
      case "textFieldPastTense":
        setTextField((TextField) field, word.getPastTense());
        break;
      case "textFieldPastParticiple":
        setTextField((TextField) field, word.getPastParticiple());
        break;
      case "textFieldPlural":
        setTextField((TextField) field, word.getPlural());
        break;
      case "textFieldSynonym":
        setTextField((TextField) field, word.getSynonym());
        break;
      case "comboBoxLesson":
        setComboBox((ComboBox<Lesson>) field, word.getLesson());
        break;
    }
  }

  private static void setFieldFromWord(@NonNull Term term, @NonNull String id, Object field) {
    switch (id) {
      case "textFieldEnglishName":
        String englishName = StringUtils.trimToEmpty(term.getEnglishName());
        if (englishName.contains("British English")) {
          englishName = englishName.replace("British English", "(British English)");
        } else if (englishName.contains("American English")){
          englishName = englishName.replace("American English", "(American English)");
        }
        setTextField((TextField) field, englishName);
        break;
      case "textFieldAmericanName":
        String americanName = StringUtils.trimToEmpty(term.getAmericanName());
        if (americanName.contains("British English")) {
          americanName = americanName.replace("British English", "(British English)");
        } else if (americanName.contains("American English")){
          americanName = americanName.replace("American English", "(American English)");
        }
        setTextField((TextField) field, americanName);
        break;
      case "textFieldOtherNames":
        String otherName = StringUtils.trimToEmpty(term.getOtherName());
        if (otherName.contains("British English")) {
          otherName = otherName.replaceAll("British English", "(British English)");
        }
        if (otherName.contains("American English")){
          otherName = otherName.replaceAll("American English", "(American English)");
        }
        setTextField((TextField) field, otherName);
        break;
      case "textFieldPolishName":
        setTextField((TextField) field, term.getPolishName());
        break;
      case "toggleGroupPartOfSpeech":
        setToggleGroup2((ToggleGroup2) field, "");
        break;
      case "toggleGroupArticle":
        setToggleGroup2((ToggleGroup2) field, "");
        break;
      case "textFieldComparative":
        setTextField((TextField) field, term.getComparative());
        break;
      case "textFieldSuperlative":
        setTextField((TextField) field, term.getSuperlative());
        break;
      case "textFieldPastTense":
        setTextField((TextField) field, term.getPastTense());
        break;
      case "textFieldPastParticiple":
        setTextField((TextField) field, term.getPastParticiple());
        break;
      case "textFieldPlural":
        setTextField((TextField) field, term.getPlural());
        break;
      case "textFieldSynonym":
        setTextField((TextField) field, term.getSynonym());
        break;
    }
  }

  private static String getFieldId(TextField textField) {
    return textField.getId();
  }

  private static String getFieldId(ComboBox comboBox) {
    return comboBox.getId();
  }

  private static String getFieldId(ToggleGroup2 toggleGroup2) {
    return toggleGroup2.getId();
  }

  private static Long getLong(Object field) {
    if (field instanceof TextField) {
      String number = ((TextField) field).getText();
      if (StringUtils.isNoneEmpty(number) && NumberUtils.isParsable(number)) {
        return Long.valueOf(number);
      }
    }
    return null;
  }

  private static String getString(Object field) {
    if (field instanceof TextField) {
      return ((TextField) field).getText();
    } else if (field instanceof ToggleGroup2) {
      ToggleGroup2 toggleGroup2 = (ToggleGroup2) field;
      if (toggleGroup2.getSelectedToggle() != null && toggleGroup2.getSelectedToggle().isSelected()) {
        return ((ToggleButton) toggleGroup2.getSelectedToggle()).getText();
      }
    }
    return StringUtils.EMPTY;
  }

  private static void setTextField(TextField textField, String value) {
    textField.setText(value);
  }

  private static void setComboBox(ComboBox comboBox, Object object) {
    comboBox.getSelectionModel().select(object);
  }

  private static void setToggleGroup2(ToggleGroup2 toggleGroup2, String text) {
    Toggle toggleButton = toggleGroup2.getToggles().stream()
        .filter(toggle -> toggle.getUserData().toString().equals(text))
        .findFirst()
        .orElse(null);
    toggleGroup2.selectToggle(toggleButton);
  }

  private static Lesson getLesson(Object object) {
    if (object instanceof ComboBox) {
      ComboBox<Lesson> comboBox = (ComboBox<Lesson>) object;
      if (comboBox.getSelectionModel().getSelectedItem() != null) {
        return comboBox.getSelectionModel().getSelectedItem();
      }
    }
    return null;
  }

}
