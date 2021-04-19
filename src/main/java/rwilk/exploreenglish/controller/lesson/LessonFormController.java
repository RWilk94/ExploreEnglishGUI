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
      lessonController.refreshChildTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText()) && StringUtils.isNotEmpty(textFieldEnName.getText())
        && StringUtils.isNotEmpty(textFieldPlName.getText())
        && comboBoxCourse.getSelectionModel().getSelectedItem() != null) {

      lessonController.getLessonService().getById(Long.valueOf(textFieldId.getText()))
          .ifPresent(lesson -> {
            Course course = comboBoxCourse.getSelectionModel().getSelectedItem();
            lesson.setEnglishName(textFieldEnName.getText().trim());
            lesson.setPolishName(textFieldPlName.getText().trim());

            if (lesson.getCourse().getId().compareTo(course.getId()) != 0) {
              lesson.setCourse(course);
              lesson.setPosition(lessonController.getLessonService().getCountByCourse(course));
            }
            setLessonForm(lessonController.getLessonService().save(lesson));
            lessonController.refreshTableView();
            lessonController.refreshChildComboBoxes();
          });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldEnName.getText()) && StringUtils.isNotEmpty(textFieldPlName.getText())
        && comboBoxCourse.getSelectionModel().getSelectedItem() != null) {
      Course course = comboBoxCourse.getSelectionModel().getSelectedItem();
      Lesson lesson = Lesson.builder()
          .englishName(textFieldEnName.getText().trim())
          .polishName(textFieldPlName.getText().trim())
          .position(lessonController.getLessonService().getCountByCourse(course))
          .course(course)
          .build();
      lesson = lessonController.getLessonService().save(lesson);
      setLessonForm(lesson);
      lessonController.refreshTableView();
      lessonController.refreshChildComboBoxes();
    }
  }

  public void setLessonForm(Lesson lesson) {
    textFieldEnName.setText(lesson.getEnglishName());
    textFieldPlName.setText(lesson.getPolishName());
    textFieldId.setText(lesson.getId().toString());
    comboBoxCourse.getSelectionModel().select(lesson.getCourse());
  }

  public void initializeCourseComboBox() {
    List<Course> courses = lessonController.getCourseService().getAll();
    comboBoxCourse.setItems(FXCollections.observableArrayList(courses));
  }

}
