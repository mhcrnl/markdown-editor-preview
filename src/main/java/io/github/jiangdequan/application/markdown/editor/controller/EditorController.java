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
import io.datafx.controller.ViewController;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * @author jiangdq
 */
@ViewController(value = "/fxml/Editor.fxml", title = "Material Design Example")
public class EditorController {

    private Stage stage;

    @FXML
    private StackPane root;

    @FXML
    private SplitPane splitPane;

    private String htmlStr = "<html><header><header><body>Hello, JavaFX Markdown Editor!</body></html>";

    @FXML
    private CodeArea editor;

    @FXML
    private WebView webview;

    private MutableDataSet options;
    private Parser parser;
    private HtmlRenderer renderer;

    @FXML
    private HBox bottomBar;

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
        editor.setAutoScrollOnDragDesired(true);
        editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
        editor.replaceText(0, 0, "Hello, JavaFX Markdown Editor!");
        webview.getEngine().loadContent(htmlStr);
    }

    @FXML
    private void preview() {
        String html = renderer.render(parser.parse(editor.getText()));
        webview.getEngine().loadContent(html);
    }

    public StackPane getRoot() {
        return root;
    }

    public void setRoot(StackPane root) {
        this.root = root;
    }
}
