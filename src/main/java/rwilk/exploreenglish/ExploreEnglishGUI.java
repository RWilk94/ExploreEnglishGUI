package rwilk.exploreenglish;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ExploreEnglishGUI extends Application {

  private ConfigurableApplicationContext context;
  private Parent rootNode;

  @Override
  public void init() throws Exception {
    SpringApplicationBuilder builder = new SpringApplicationBuilder(ExploreEnglishGUI.class);
    context = builder.run(getParameters().getRaw().toArray(new String[0]));

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/main.fxml"));
    loader.setControllerFactory(context::getBean);
    rootNode = loader.load();
    super.init();
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("ExploreEnglishGUI");
    Scene scene = new Scene(rootNode, 1366, 766);
    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.centerOnScreen();
    primaryStage.show();
  }

  @Override
  public void stop() {
    context.close();
  }
}
