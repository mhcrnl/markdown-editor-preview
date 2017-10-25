/**
 *
 */
package io.github.jiangdequan.application.markdown.editor;

import io.github.jiangdequan.application.markdown.editor.controller.EditorController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author jiangdq
 */
public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Scene scene = new Scene(new EditorController(stage), getVisualScreenWidth() * 0.8,
        getVisualScreenHeight() * 0.8,
        Color.TRANSPARENT);
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Gets the visual bounds of this Screen. These bounds account for objects in the native windowing
   * system such as task bars and menu bars. These bounds are contained by Screen.bounds.
   *
   * @return double
   */
  public static double getVisualScreenWidth() {
    return Screen.getPrimary().getVisualBounds().getWidth();
  }

  /**
   * Gets the visual bounds of this Screen. These bounds account for objects in the native windowing
   * system such as task bars and menu bars. These bounds are contained by Screen.bounds.
   *
   * @return double
   */
  public static double getVisualScreenHeight() {
    return Screen.getPrimary().getVisualBounds().getHeight();
  }
}
