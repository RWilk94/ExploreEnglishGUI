package rwilk.exploreenglish.controller.lesson;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Lesson;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class LessonFormController implements Initializable {

  private LessonController lessonController;
  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;
  public ComboBox<Course> comboBoxCourse;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(LessonController lessonController) {
    this.lessonController = lessonController;
    initializeCourseComboBox();
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldEnName.clear();
    textFieldPlName.clear();
    textFieldId.clear();
    comboBoxCourse.getSelectionModel().select(null);
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      lessonController.getLessonService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(lesson -> lessonController.getLessonService().delete(lesson));
      buttonClearOnAction(actionEvent);
      lessonController.refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText()) && StringUtils.isNotEmpty(textFieldEnName.getText())
        && StringUtils.isNotEmpty(textFieldPlName.getText())
        && !comboBoxCourse.getSelectionModel().isEmpty()) {

      lessonController.getLessonService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(lesson -> {
            lesson.setEnglishName(textFieldEnName.getText().trim());
            lesson.setPolishName(textFieldPlName.getText().trim());
            lesson.setCourse(comboBoxCourse.getSelectionModel().getSelectedItem());
            setLessonForm(lessonController.getLessonService().save(lesson));
            lessonController.refreshTableView();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldEnName.getText()) && StringUtils.isNotEmpty(textFieldPlName.getText())
        && !comboBoxCourse.getSelectionModel().isEmpty()) {
      Lesson lesson = Lesson.builder()
          .englishName(textFieldEnName.getText().trim())
          .polishName(textFieldPlName.getText().trim())
          .course(lessonController.getCourseService().getById((comboBoxCourse.getSelectionModel().getSelectedItem()).getId()).get())
          .build();
      lesson = lessonController.getLessonService().save(lesson);
      setLessonForm(lesson);
      lessonController.refreshTableView();
    }
  }

  public void setLessonForm(Lesson lesson) {
    textFieldEnName.setText(lesson.getEnglishName());
    textFieldPlName.setText(lesson.getPolishName());
    textFieldId.setText(lesson.getId().toString());
    comboBoxCourse.getSelectionModel().select(lesson.getCourse());
  }

  private void initializeCourseComboBox() {
    List<Course> courses = lessonController.getCourseService().getAll();
    comboBoxCourse.setItems(FXCollections.observableArrayList(courses));
  }

}
