package rwilk.exploreenglish;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import rwilk.exploreenglish.exception.ExceptionControllerAdvice;

@SpringBootApplication
@EnableJpaRepositories
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
    Thread.setDefaultUncaughtExceptionHandler(ExceptionControllerAdvice::showError);

    primaryStage.setTitle("ExploreEnglishGUI");
    Scene scene = new Scene(rootNode, 1366, 766);
    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    if (rootNode instanceof BorderPane root) {
      root.setTop(createMenuBar(scene));
    }

    primaryStage.setScene(scene);
    primaryStage.centerOnScreen();
    primaryStage.show();
  }

  @Override
  public void stop() {
    context.close();
  }

  private MenuBar createMenuBar(Scene scene) {
    MenuBar menuBar = new MenuBar();

    Menu menuFile = new Menu("File");
    Menu menuView = new Menu("View");

    MenuItem menuItemClose = new MenuItem("Close");
    menuItemClose.setOnAction(event -> scene.getWindow().hide());

    CheckMenuItem menuItemDarkMode = new CheckMenuItem("Dark Mode");
    menuItemDarkMode.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
      if (isSelected) {
        scene.getStylesheets().add(getClass().getResource("/dark_mode.css").toExternalForm());
      } else {
        scene.getStylesheets().clear();
      }
    });

    menuFile.getItems().add(menuItemClose);
    menuView.getItems().add(menuItemDarkMode);

    menuBar.getMenus().add(menuFile);
    menuBar.getMenus().add(menuView);

    return menuBar;
  }
}
