/**
 *
 */
package io.github.jiangdequan.application.markdown.editor;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.github.jiangdequan.application.markdown.editor.controller.EditorController;
import io.github.jiangdequan.application.markdown.editor.decorator.WindowDecorator;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author jiangdq
 */
public class Main extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Flow flow = new Flow(EditorController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        WindowDecorator decorator = new WindowDecorator(stage, container.getView());

        Scene scene = new Scene(decorator, getVisualScreenWidth() * 0.8, getVisualScreenHeight() * 0.8,
            Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        // 保存程序初始化桌面状态
        decorator
            .setPreviousWindowBounds(new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()));
    }

    /**
     * Gets the visual bounds of this Screen. These bounds account for objects in the native windowing system such as
     * task bars and menu bars. These bounds are contained by Screen.bounds.
     *
     * @return double
     */
    public static double getVisualScreenWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    /**
     * Gets the visual bounds of this Screen. These bounds account for objects in the native windowing system such as
     * task bars and menu bars. These bounds are contained by Screen.bounds.
     *
     * @return double
     */
    public static double getVisualScreenHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }
}
