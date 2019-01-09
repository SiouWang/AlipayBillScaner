package qyw.process;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import qyw.Transfer;
import qyw.opencv4.OpencvUtil;
import qyw.tesseract.Tess4jUtils;
import qyw.util.TextUtil;

import java.awt.image.BufferedImage;
import java.util.*;

public class TempMain {

    /**
     * 加载opencv的dll文件
     */
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public final static String CHI_SIM = "chi_sim";
    public final static String ENG = "eng";

    // 记录已剪裁的坐标
    static Set<String> hasCutPoint = new HashSet<String>();


    /**
     * 裁切图片输出BufferedImage直接用于OCR
     * @param src
     * @param rect
     * @return
     */
    public static BufferedImage cutImg(Mat src, Rect rect, int i) {

        Mat cutMat = new Mat(src, rect);
//        Imgcodecs.imwrite("F:/ocr_imgs/result/cutMat-"+ i +".jpg", cutMat);
        // 识别图像内容
        BufferedImage nameBuffer = OpencvUtil.Mat2BufImg(cutMat,".jpg");

        return nameBuffer;
    }



    public static Transfer doProcess(String imgPath) {

        Mat src = Imgcodecs.imread(imgPath);

        // 保存识别完毕的 图片
        Set<String> set = new HashSet<String>();
        List<String> textList = new ArrayList<String>();


        // 灰度处理
        Mat srcGray = new Mat();
        Imgproc.cvtColor(src, srcGray,Imgproc.COLOR_BGR2GRAY);

        // 二值化处理
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(srcGray, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);

        // 膨胀处理(这里设置size的宽的数值比较大，目的是为了让两列的图像膨胀为一列，但是不同内容区域的长度可能会导致膨胀过宽，超过了图片的宽度，导致后续的边缘检测无法探测到边缘，导致这一行的数据无法被识别)
        Mat dilate=new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(90, 10));
        Imgproc.dilate(binary, dilate, element, new Point(-1, -1), 1);



        // 边缘检测
        Mat cannyOutput = new Mat();
        Imgproc.Canny(dilate, cannyOutput, 1, 1 * 1, 3, false);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // 绘制边缘
        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));


        }
        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        List<MatOfPoint> contoursPolyList = new ArrayList<MatOfPoint>(contoursPoly.length);
        for (MatOfPoint2f poly : contoursPoly) {
            contoursPolyList.add(new MatOfPoint(poly.toArray()));
        }

        System.out.println("图片的宽度为：" + src.width() + " 图片的高度为：" + src.height());
        Random rng = new Random(12345);

        Transfer transfer = new Transfer();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            // 在原图绘制矩形边框
//            Imgproc.rectangle(src, boundRect[i].tl(), boundRect[i].br(), color, 2);
            // 文字识别
            String text = Tess4jUtils.readTextFromImage(cutImg(src, calculateWithdAndHeight(src, boundRect[i]), i), CHI_SIM);

            TextUtil.filterBill(text, "alipay", transfer);
            System.out.println(text);
        }

        // 处理重复字符串
        textList.addAll(set);
        return transfer;
    }

//    public static String parsingText(String imgPath) {
//
//        Mat src = Imgcodecs.imread(imgPath);
//
//        // 保存识别完毕的 图片
//        Set<String> set = new HashSet<String>();
//        List<String> textList = new ArrayList<String>();
//
//
//        // 灰度处理
//        Mat srcGray = new Mat();
//        Imgproc.cvtColor(src, srcGray,Imgproc.COLOR_BGR2GRAY);
//
//        // 二值化处理
//        Mat binary = new Mat();
//        Imgproc.adaptiveThreshold(srcGray, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
//
//        // 膨胀处理(这里设置size的宽的数值比较大，目的是为了让两列的图像膨胀为一列，但是不同内容区域的长度可能会导致膨胀过宽，超过了图片的宽度，导致后续的边缘检测无法探测到边缘，导致这一行的数据无法被识别)
//        Mat dilate=new Mat();
//        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(90, 10));
//        Imgproc.dilate(binary, dilate, element, new Point(-1, -1), 1);
//
//
//
//        // 边缘检测
//        Mat cannyOutput = new Mat();
//        Imgproc.Canny(dilate, cannyOutput, 1, 1 * 1, 3, false);
//
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // 绘制边缘
//        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
//        Rect[] boundRect = new Rect[contours.size()];
//        for (int i = 0; i < contours.size(); i++) {
//            contoursPoly[i] = new MatOfPoint2f();
//            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
//            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
//
//
//        }
//        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
//        List<MatOfPoint> contoursPolyList = new ArrayList<MatOfPoint>(contoursPoly.length);
//        for (MatOfPoint2f poly : contoursPoly) {
//            contoursPolyList.add(new MatOfPoint(poly.toArray()));
//        }
//
//        System.out.println("图片的宽度为：" + src.width() + " 图片的高度为：" + src.height());
//        Random rng = new Random(12345);
//
//        Transfer transfer = new Transfer();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < contours.size(); i++) {
//            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
//            // 文字识别
//            String text = Tess4jUtils.readTextFromImage(cutImg(src, calculateWithdAndHeight(src, boundRect[i]), i), CHI_SIM);
//
//            TextUtil.filterBill(text, "alipay", transfer);
//            System.out.println(text);
//        }
//
//        // 处理重复字符串
//        textList.addAll(set);
//
//        return text;
//    }


    /**
     * Rect对象中的tl() 表示top-left,保存的是矩形左上角的x,y坐标
     * Rect对象中的br() 表示bottorm-right保存的是矩形右下角的x, y坐标
     * 裁切图像，只需要知道y的坐标即可，根据rect来计算我们需要裁切的高度
     * @param src 原图像Mat对象
     * @param rect 矩形对象
     * @return
     */
    public static Rect calculateWithdAndHeight(Mat src, Rect rect) {
        // 拿左上角的Y坐标和右下角的Y坐标计算出矩形的高度，也就是裁切的高度
        Rect cutRect = new Rect();

        // 裁切的x坐标为 轮廓的左上角X + 10
        cutRect.x = 10;
        // 裁切的y坐标为 轮廓的左上角Y - 10
        cutRect.y = (int)rect.tl().y;
        cutRect.width = src.width() - 10;
        cutRect.height = (int)((rect.br().y)  - (rect.tl().y));
        String pointStr = cutRect.x + "";
        // 如果当前坐标没有处理过，则保存，剪裁图片的时候会从set里面判断是否已经处理过
        if(!hasCutPoint.contains(pointStr)) {
            hasCutPoint.add(pointStr);
        }
        return cutRect;
    }
}
