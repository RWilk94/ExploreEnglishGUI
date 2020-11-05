package rwilk.exploreenglish.controller.course;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.service.CourseService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
@Controller
public class CourseFormController implements Initializable {

  private CourseController courseController;
  private CourseService courseService;
  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(CourseController courseController, CourseService courseService) {
    this.courseController = courseController;
    this.courseService = courseService;
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    textFieldEnName.clear();
    textFieldPlName.clear();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (!textFieldId.getText().isEmpty()) {
      courseService.getById(Long.valueOf(textFieldId.getText())).ifPresent(course -> courseService.delete(course));
      buttonClearOnAction(actionEvent);
      courseController.refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (!textFieldId.getText().isEmpty() && !textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty()) {
      Optional<Course> courseOptional = courseService.getById(Long.valueOf(textFieldId.getText()));
      courseOptional.ifPresent(course -> {
        course.setEnglishName(textFieldEnName.getText().trim());
        course.setPolishName(textFieldPlName.getText().trim());
        setCourseForm(courseService.save(course));
        courseController.refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (!textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty()) {
      Course course = Course.builder()
          .englishName(textFieldEnName.getText().trim())
          .polishName(textFieldPlName.getText().trim())
          .build();
      course = courseService.save(course);
      setCourseForm(course);
      courseController.refreshTableView();
    }
  }

  public void setCourseForm(Course course) {
    textFieldEnName.setText(course.getEnglishName());
    textFieldPlName.setText(course.getPolishName());
    textFieldId.setText(course.getId().toString());
  }

}
