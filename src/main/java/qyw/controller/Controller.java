package qyw.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import qyw.Transfer;
import qyw.capture.ScreenCapture;
import qyw.process.TempMain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    /**
     * 用户选择的图片列表，缓存下来方便记录操作失败
     */
    private Map<String, String> processImageList = new HashMap<String, String>();

    /**
     * 处理失败的图片集合
     */
    private List<String> processingFailedPictureList = new ArrayList<String>();



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

    @FXML
    private Text productName;

    @FXML
    private Text orderNo;

    @FXML
    private Text createDate;


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
            // 每次操作前，清空数据
            processImageList.clear();

            for(int i = 0 ; i < fileList.size() ; i++) {
                File file = fileList.get(i);
                if(null != file) {
                    System.out.println("选择的文件是：" + file.getAbsolutePath());
                    Image image =  new Image("file:" + file.getAbsolutePath());
                    imgShow.setImage(image);
                    TempMain.doProcess(file.getAbsolutePath());
//                    productName.setText(transfer.getProductName());
//                    orderNo.setText(transfer.getOrderNo());
//                    createDate.setText(transfer.getCreateDate());
                }
            }

            // 启动截图
//            Platform.runLater(() -> new ScreenCapture().start(new Stage()));
//
//            System.out.println("截图已经启动");
//            // 循环扫描文件目录
//            while(true) {
//                System.out.println("正在扫描...");
//                File folder = new File(System.getProperty("user.home"), "snapshots");
//                String[] content = folder.list();//取得当前目录下所有文件和文件夹
//                for(String name : content){
//                    System.out.println("当前文件是：" + name);
//                    break;
//                }
//            }
        }

    }

}
