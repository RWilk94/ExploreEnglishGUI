package rwilk.exploreenglish.controller.course;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class CourseFormController implements Initializable {

  private CourseController courseController;
  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(CourseController courseController) {
    this.courseController = courseController;
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    textFieldEnName.clear();
    textFieldPlName.clear();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (!textFieldId.getText().isEmpty()) {
      courseController.getCourseService().getById(Long.valueOf(textFieldId.getText())).ifPresent(course -> courseController.getCourseService().delete(course));
      buttonClearOnAction(actionEvent);
      courseController.refreshTableView();
      courseController.refreshChildTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (!textFieldId.getText().isEmpty() && !textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty()) {
      Optional<Course> courseOptional = courseController.getCourseService().getById(Long.valueOf(textFieldId.getText()));
      courseOptional.ifPresent(course -> {
        course.setEnglishName(textFieldEnName.getText().trim());
        course.setPolishName(textFieldPlName.getText().trim());
        setCourseForm(courseController.getCourseService().save(course));
        courseController.refreshTableView();
        courseController.refreshChildComboBoxes();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (!textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty()) {
      Course course = Course.builder()
          .englishName(textFieldEnName.getText().trim())
          .polishName(textFieldPlName.getText().trim())
          .build();
      course = courseController.getCourseService().save(course);
      setCourseForm(course);
      courseController.refreshTableView();
      courseController.refreshChildComboBoxes();
    }
  }

  public void setCourseForm(Course course) {
    textFieldEnName.setText(course.getEnglishName());
    textFieldPlName.setText(course.getPolishName());
    textFieldId.setText(course.getId().toString());
  }

}
