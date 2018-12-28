package qyw.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Pane rootLayout;

    /**
     * 选择图片按钮
     */
    @FXML
    private MenuItem selectImgBtn;

    /**
     * 图片显示区域
     */
    @FXML
    private ImageView imgShow;


    public void chooseImagesFromExplorer() throws FileNotFoundException, InterruptedException {
        System.out.println("1212312");

        Stage stage = (Stage) rootLayout.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择支付宝账单截图，请务必保证截图清晰规整");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );

        // 增加图片格式过滤
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("所有图片", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        // 显示文件选择窗体
        List<File> fileList = fileChooser.showOpenMultipleDialog(stage);
        if(null != fileList && fileList.size() > 0) {
            for(int i = 0 ; i < fileList.size() ; i++) {
                File file = fileList.get(i);
                if(null != file) {
                    System.out.println("选择的文件是：" + file.getAbsolutePath());
                    Image image =  new Image("file:" + file.getAbsolutePath());
                    imgShow.setImage(image);
                    Thread.sleep(2000);
                }
            }
        }

    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *  @param location
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
