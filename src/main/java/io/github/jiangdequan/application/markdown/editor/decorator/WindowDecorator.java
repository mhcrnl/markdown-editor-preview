package io.github.jiangdequan.application.markdown.editor.decorator;

import io.github.jiangdequan.application.markdown.editor.Main;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowDecorator extends VBox {

    private Stage primaryStage;

    @FXML
    private HBox windowBar;

    private boolean allowMove = false;

    private boolean isDragging = false;

    /**
     * 上一次桌面状态
     */
    private Rectangle2D previousWindowBounds;

    private StackPane contentPlaceHolder = new StackPane();

    /**
     * 最大化.
     */
    private boolean maximized = false;

    private double mouseDragOffsetX;

    private double mouseDragOffsetY;
    private double xOffset = 0;
    private double yOffset = 0;
    private double newX, newY, initX, initY, initWidth = -1, initHeight = -1,
        initStageX = -1, initStageY = -1;

    public WindowDecorator(Stage stage, Node node) {
        this.primaryStage = stage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        setPickOnBounds(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WindowDecorator.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        windowBar.setPadding(new Insets(4));

        contentPlaceHolder.getStyleClass().add("jfx-decorator-content-container");
        contentPlaceHolder.setMinSize(0, 0);
        contentPlaceHolder.getChildren().add(node);
        ((Region) node).setMinSize(0, 0);
        VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);
        contentPlaceHolder.getStyleClass().add("resize-border");
        VBox.setVgrow(node, Priority.ALWAYS);
        contentPlaceHolder.setBorder(new Border(new BorderStroke(Color.rgb(220, 220, 220),
            BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(0, 4, 4, 4))));
        this.getChildren().add(contentPlaceHolder);
    }

    @FXML
    public void initialize() {
        windowBar.addEventHandler(MouseEvent.MOUSE_ENTERED, (enter) -> allowMove = true);
        windowBar.addEventHandler(MouseEvent.MOUSE_EXITED, (enter) -> {
            if (!isDragging) {
                allowMove = false;
            }
        });

        contentPlaceHolder.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) ->
            updateInitMouseValues(mouseEvent));
        windowBar.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) ->
            updateInitMouseValues(mouseEvent));

        // show the drag cursor on the borders
        this.setOnMouseMoved((mouseEvent) -> {
            if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized) {
                this.setCursor(Cursor.DEFAULT);
                return; // maximized mode does not support resize
            }
            if (!primaryStage.isResizable()) {
//                updateInitMouseValues(mouseEvent);
                return;
            }
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            Bounds boundsInParent = this.getBoundsInParent();
            if (contentPlaceHolder.getBorder() != null && contentPlaceHolder.getBorder().getStrokes().size() > 0) {
                double borderWidth = contentPlaceHolder.snappedLeftInset();
                if (isRightEdge(x, y)) {
                    if (y < borderWidth) {
                        this.setCursor(Cursor.NE_RESIZE);
                    } else if (y > this.getHeight() - borderWidth) {
                        this.setCursor(Cursor.SE_RESIZE);
                    } else {
                        this.setCursor(Cursor.E_RESIZE);
                    }
                } else if (isLeftEdge(x, y)) {
                    if (y < borderWidth) {
                        this.setCursor(Cursor.NW_RESIZE);
                    } else if (y > this.getHeight() - borderWidth) {
                        this.setCursor(Cursor.SW_RESIZE);
                    } else {
                        this.setCursor(Cursor.W_RESIZE);
                    }
                } else if (isTopEdge(x, y)) {
                    this.setCursor(Cursor.N_RESIZE);
                } else if (isBottomEdge(x, y)) {
                    this.setCursor(Cursor.S_RESIZE);
                } else {
                    this.setCursor(Cursor.DEFAULT);
                }
//                updateInitMouseValues(mouseEvent);
            }
        });

        // handle drag events on the decorator pane
        this.setOnMouseReleased((mouseEvent) -> isDragging = false);

        this.setOnMouseDragged((mouseEvent) -> {
            isDragging = true;
            if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
                return;
            }
            /*
             * Long press generates drag event!
             */
            if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || primaryStage.isMaximized()
                || maximized) {
                return;
            }

            newX = mouseEvent.getScreenX();
            newY = mouseEvent.getScreenY();

            double deltax = newX - initX;
            double deltay = newY - initY;
            Cursor cursor = this.getCursor();

            if (Cursor.E_RESIZE.equals(cursor)) {
                setStageWidth(initWidth + deltax);
                mouseEvent.consume();
            } else if (Cursor.NE_RESIZE.equals(cursor)) {
                if (setStageHeight(initHeight - deltay)) {
                    primaryStage.setY(initStageY + deltay);
                }
                setStageWidth(initWidth + deltax);
                mouseEvent.consume();
            } else if (Cursor.SE_RESIZE.equals(cursor)) {
                setStageWidth(initWidth + deltax);
                setStageHeight(initHeight + deltay);
                mouseEvent.consume();
            } else if (Cursor.S_RESIZE.equals(cursor)) {
                setStageHeight(initHeight + deltay);
                mouseEvent.consume();
            } else if (Cursor.W_RESIZE.equals(cursor)) {
                if (setStageWidth(initWidth - deltax)) {
                    primaryStage.setX(initStageX + deltax);
                }
                mouseEvent.consume();
            } else if (Cursor.SW_RESIZE.equals(cursor)) {
                if (setStageWidth(initWidth - deltax)) {
                    primaryStage.setX(initStageX + deltax);
                }
                setStageHeight(initHeight + deltay);
                mouseEvent.consume();
            } else if (Cursor.NW_RESIZE.equals(cursor)) {
                if (setStageWidth(initWidth - deltax)) {
                    primaryStage.setX(initStageX + deltax);
                }
                if (setStageHeight(initHeight - deltay)) {
                    primaryStage.setY(initStageY + deltay);
                }
                mouseEvent.consume();
            } else if (Cursor.N_RESIZE.equals(cursor)) {
                if (setStageHeight(initHeight - deltay)) {
                    primaryStage.setY(initStageY + deltay);
                }
                mouseEvent.consume();
            } else if (allowMove) {
                primaryStage.setX(mouseEvent.getScreenX() - xOffset);
                primaryStage.setY(mouseEvent.getScreenY() - yOffset);
                mouseEvent.consume();
            }
            setPreviousWindowBounds(new Rectangle2D(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth()
                , primaryStage.getHeight()));
        });
    }

    private void updateInitMouseValues(MouseEvent mouseEvent) {
        initStageX = primaryStage.getX();
        initStageY = primaryStage.getY();
        initWidth = primaryStage.getWidth();
        initHeight = primaryStage.getHeight();
        initX = mouseEvent.getScreenX();
        initY = mouseEvent.getScreenY();
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    private boolean isRightEdge(double x, double y) {
        return x < this.getWidth() && x > this.getWidth() - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isTopEdge(double x, double y) {
        return y >= 0 && y < contentPlaceHolder.snappedLeftInset();
    }

    private boolean isBottomEdge(double x, double y) {
        return y < this.getHeight() && y > this.getHeight() - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isLeftEdge(double x, double y) {
        return x >= 0 && x < contentPlaceHolder.snappedLeftInset();
    }

    boolean setStageWidth(double width) {
        if (width >= primaryStage.getMinWidth() && width >= windowBar.getMinWidth()) {
            primaryStage.setWidth(width);
//            initX = newX;
            return true;
        } else if (width >= primaryStage.getMinWidth() && width <= windowBar.getMinWidth()) {
            width = windowBar.getMinWidth();
            primaryStage.setWidth(width);
        }
        return false;
    }

    boolean setStageHeight(double height) {
        if (height >= primaryStage.getMinHeight() && height >= windowBar.getHeight()) {
            primaryStage.setHeight(height);
//            initY = newY;
            return true;
        } else if (height >= primaryStage.getMinHeight() && height <= windowBar.getHeight()) {
            height = windowBar.getHeight();
            primaryStage.setHeight(height);
        }
        return false;
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
        primaryStage.setIconified(true);
    }

    /**
     * resotre the window of the application to the previous size.
     */
    @FXML
    private void restore() {
        // 当前是最大化
        if (maximized) {
            maximized = false;
            primaryStage.setX(previousWindowBounds.getMinX());
            primaryStage.setY(previousWindowBounds.getMinY());
            primaryStage.setWidth(previousWindowBounds.getWidth());
            primaryStage.setHeight(previousWindowBounds.getHeight());
        } else {
            // 当前不是最大化
            // 屏幕长度和高度
            double width = Main.getVisualScreenWidth();
            double height = Main.getVisualScreenHeight();
            maximized = true;
            primaryStage.setX(0);
            primaryStage.setY(0);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
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
        if (Cursor.N_RESIZE.equals(this.getCursor())) {
            return;
        }
        if (maximized) {
            primaryStage.setWidth(previousWindowBounds.getWidth());
            primaryStage.setHeight(previousWindowBounds.getHeight());
            maximized = false;
        }
        primaryStage.setX(e.getScreenX() - mouseDragOffsetX);
        primaryStage.setY(e.getScreenY() - mouseDragOffsetY);
    }

    public Rectangle2D getPreviousWindowBounds() {
        return previousWindowBounds;
    }

    public void setPreviousWindowBounds(Rectangle2D previousWindowBounds) {
        this.previousWindowBounds = previousWindowBounds;
    }
}
