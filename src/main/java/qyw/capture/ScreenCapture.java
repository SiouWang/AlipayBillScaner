package qyw.capture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenCapture extends Application {

    private static ScreenCapture screenCapture;
    private Stage stage;
    public Rectangle screenBounds;
    private Rebounder rebounder;
    private GlassPane glassPane;

    public static ScreenCapture getScreenCapture() {

        return screenCapture;
    }

    @Override
    public void start(Stage primaryStage) {

        screenCapture = this;

        stage = primaryStage;
        Group group = new Group();
        Scene scene = new Scene(group);
        scene.setFill(Color.TRANSPARENT);
        scene.setCursor(Cursor.CROSSHAIR);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 长方形，宽和高等于显示器的宽和高
        screenBounds = new Rectangle(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());

        KeyPane keyPane = new KeyPane(primaryStage, rebounder = new Rebounder());
        glassPane = new GlassPane();
        // 以显示器的宽和高创建一个长方形
        glassPane.setShape(rebounder.shapeBuilder(null), true);

        group.getChildren().addAll(new Node[] { (Node) keyPane, (Node) glassPane, (Node) rebounder.getLasso() });

        scene.setOnMousePressed(me -> glassPane.setShape(rebounder.shapeBuilder(rebounder.start(me.getX(), me.getY())), false));
        scene.setOnMouseDragged(me -> glassPane.setShape(rebounder.shapeBuilder(rebounder.rebound(me.getX() + 1, me.getY() + 1)), false));

        // 鼠标释放后捕捉图片并保存
        scene.setOnMouseReleased(me -> {
            if (!rebounder.isStopped()) {
                capture(rebounder.stop(me.getX() + 1, me.getY() + 1));
            }
        });
    }

    /**
     * 捕捉鼠标绘制的矩形区域
     * @param finished
     */
    public void capture(final Rectangle finished) {

        stage.setWidth(0);
        stage.setHeight(0);
        EventQueue.invokeLater(() -> {
            if ((((finished.getWidth() - 1) * (finished.getHeight() - 1)) > 0.0d)) {
                try {
                    Robot robot = new Robot();
                    BufferedImage img = robot.createScreenCapture(new java.awt.Rectangle(
                                    (int) finished.getX(), (int) finished
                                    .getY(), (int) finished
                                    .getWidth() - 1, (int) finished
                                    .getHeight() - 1));



                    File folder = new File(System.getProperty("user.home"), "snapshots");
                    folder.mkdirs();
                    deleteDir(folder.getAbsolutePath());
                    File file = File.createTempFile("jfx2_screen_capture", ".jpg", folder);
                    ImageIO.write(img, "jpg", file);
                } catch (Exception ex) {
                    Logger.getLogger(ScreenCapture.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Platform.runLater(() -> {
                glassPane.setShape(rebounder.shapeBuilder(null), true);
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
            });
        });
        stage.hide();
    }

    public static boolean deleteDir(String path){
        File file = new File(path);
        if(!file.exists()){//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
            return false;
        }

        String[] content = file.list();//取得当前目录下所有文件和文件夹
        for(String name : content){
            File temp = new File(path, name);
            if(temp.isDirectory()){//判断是否是目录
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                temp.delete();//删除空目录
            }else{
                if(!temp.delete()){//直接删除文件
                    System.err.println("Failed to delete " + name);
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {

        launch(args);
    }
}