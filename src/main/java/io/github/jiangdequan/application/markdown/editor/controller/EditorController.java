/**
 *
 */
package io.github.jiangdequan.application.markdown.editor.controller;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;
import io.github.jiangdequan.application.markdown.editor.Main;
import java.io.IOException;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * @author jiangdq
 */
public class EditorController extends StackPane {

  private Stage stage;

  /**
   * .
   */
  private Rectangle2D previousWindowBounds;

  /**
   * 最大化.
   */
  private boolean maximized = true;

  private double mouseDragOffsetX;

  private double mouseDragOffsetY;

  private String htmlStr = "<html><header><header><body>Hello, JavaFX Markdown Editor!</body></html>";

  @FXML
  private CodeArea editor;

  @FXML
  private WebView webview;

  private MutableDataSet options;
  private Parser parser;
  private HtmlRenderer renderer;

  /**
   * @param stage Stage
   */
  public EditorController(Stage stage) {
    this.stage = stage;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Editor.fxml"));
    loader.setController(this);
    loader.setRoot(this);

    try {
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 初始化方法,默认会执行.
   */
  @FXML
  private void initialize() {
    options = new MutableDataSet();
    options.setFrom(ParserEmulationProfile.MARKDOWN);
    options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[]{TablesExtension.create()}));
    parser = Parser.builder(options).build();
    renderer = HtmlRenderer.builder(options).build();

    // 行号
    editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
    editor.replaceText(0, 0, "Hello, JavaFX Markdown Editor!");
    webview.getEngine().loadContent(htmlStr);
  }

  /**
   * exit the application.
   */
  @FXML
  private void exit() {
    Platform.exit();
  }

  /**
   * minimize the application.
   */
  @FXML
  private void minimize() {
    stage.setIconified(true);
  }

  /**
   * resotre the window of the application to the previous size.
   */
  @FXML
  private void restore() {
    // 屏幕对象
    double width = Main.getVisualScreenWidth();
    double height = Main.getVisualScreenHeight();
    // 最大化
    if (maximized) {
      maximized = false;
      if (null == previousWindowBounds) {
        previousWindowBounds = new Rectangle2D((stage.getWidth() - 800) / 2,
            (stage.getHeight() - 600) / 2, 800,
            600);
      }

      if (previousWindowBounds != null) {
        stage.setX(previousWindowBounds.getMinX());
        stage.setY(previousWindowBounds.getMinY());
        stage.setWidth(previousWindowBounds.getWidth());
        stage.setHeight(previousWindowBounds.getHeight());
      }
      // 非最大化
    } else {
      maximized = true;
      previousWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(),
          stage.getHeight());
      stage.setX(0);
      stage.setY(0);
      stage.setWidth(width);
      stage.setHeight(height);
    }
  }

  @FXML
  private void toogleMaximized(MouseEvent e) {
    if (2 == e.getClickCount()) {
      restore();
    }
  }

  @FXML
  private void recordPosition(MouseEvent e) {
    mouseDragOffsetX = e.getSceneX();
    mouseDragOffsetY = e.getSceneY();
  }

  @FXML
  private void dragWindow(MouseEvent e) {
    if (maximized) {
      previousWindowBounds = new Rectangle2D((stage.getWidth() - 800) / 2,
          (stage.getHeight() - 600) / 2, 800,
          600);
      stage.setWidth(previousWindowBounds.getWidth());
      stage.setHeight(previousWindowBounds.getHeight());
      maximized = false;
    }
    stage.setX(e.getScreenX() - mouseDragOffsetX);
    stage.setY(e.getScreenY() - mouseDragOffsetY);
  }

  @FXML
  private void preview() {
    String html = renderer.render(parser.parse(editor.getText()));

    webview.getEngine().loadContent(html);
  }
}
