package rwilk.exploreenglish.controller.course;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.migration.entity.FinalCourse;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class CourseFormController implements Initializable {

  private CourseController courseController;

  public TextField textFieldId;
  public TextField textFieldName;
  public TextArea textAreaDescription;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(CourseController courseController) {
    this.courseController = courseController;
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    textFieldName.clear();
    textAreaDescription.clear();
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
//    if (!textFieldId.getText().isEmpty()) {
//      courseController.getCourseService().getById(Long.valueOf(textFieldId.getText())).ifPresent(course -> courseController.getCourseService().delete(course));
//      buttonClearOnAction(actionEvent);
//      courseController.refreshTableView();
//      courseController.refreshChildTableView();
//    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
//    if (!textFieldId.getText().isEmpty() && !textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty()) {
//      Optional<Course> courseOptional = courseController.getCourseService().getById(Long.valueOf(textFieldId.getText()));
//      courseOptional.ifPresent(course -> {
//        course.setEnglishName(textFieldEnName.getText().trim());
//        course.setPolishName(textFieldPlName.getText().trim());
//        setCourseForm(courseController.getCourseService().save(course));
//        courseController.refreshTableView();
//        courseController.refreshChildComboBoxes();
//      });
//    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
//    if (!textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty()) {
//      Course course = Course.builder()
//                            .englishName(textFieldEnName.getText().trim())
//                            .polishName(textFieldPlName.getText().trim())
//                            .position(courseController.getCourseService().getCount())
//                            .build();
//      course = courseController.getCourseService().save(course);
//      setCourseForm(course);
//      courseController.refreshTableView();
//      courseController.refreshChildComboBoxes();
//    }
  }

  public void setCourseForm(FinalCourse course) {
    textFieldName.setText(course.getName());
    textAreaDescription.setText(course.getDescription());
    textFieldId.setText(course.getId().toString());
  }

  public void buttonExportDatabaseOnAction(ActionEvent actionEvent) {
//    courseController.getExportService().export();
    // TODO copy generated files to android assets
  }

  public void buttonGenerateDocumentOnAction(final ActionEvent actionEvent) {
    // courseController.getDocService().generateDocument();
//    courseController.getExportDocumentService().generate();
  }
}
