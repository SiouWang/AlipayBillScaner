package qyw.tesseract;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;

public class Tess4jUtils {

    public static final String CHINESE = "zho";

    /**
     * 识别图片文件中的文字
     * @param instance
     * @param imageFile
     * @return
     */
    public static String readTextFromImage(File imageFile){

        ITesseract instance = new Tesseract();
        // 语言库代码位置
        instance.setDatapath("src/main/resources/tessdata");


        // 识别中文
        instance.setLanguage("chi_sim");
        String result = null;
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 识别图片中的文字
     * @param instance
     * @param img
     * @return
     */
    public static String readTextFromImage(BufferedImage img, String language){

        ITesseract instance = new Tesseract();
        // 语言库代码位置
        instance.setDatapath("src/main/resources/tessdata");
        // 识别中文
        instance.setLanguage(language);

        String result = null;
        try {
            result = instance.doOCR(img);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {

        String imagePath = "src/main/resources/image/141.png";
        String text = readTextFromImage(new File(imagePath));
        String [] textArray = text.split("\n");
        for(int i = 0 ; i < textArray.length ; i++) {
            System.out.println("第" + (i + 1) + "行：" + textArray[i]);
        }



    }

}

