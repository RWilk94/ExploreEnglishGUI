package rwilk.exploreenglish.controller.main;

import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.service.InjectService;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class MainController implements Initializable {

  private final InjectService injectService;
  public TabPane tabPane;

  public MainController(InjectService injectService) {
    this.injectService = injectService;
    this.injectService.setMainController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
}
