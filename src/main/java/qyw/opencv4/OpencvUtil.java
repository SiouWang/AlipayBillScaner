package qyw.opencv4;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpencvUtil {

    private static final int BLACK = 0;
    private static final int WHITE = 255;

    /**
     * 灰化处理
     * @return
     */
    public static Mat gray (Mat image){
        Imgproc.cvtColor(image, image,Imgproc.COLOR_BGR2GRAY);
        return image;
    }

    /**
     * 二值化处理
     * @return
     */
    public static Mat binary (Mat mat){
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(mat, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
        return binary;
    }

    /**
     * 模糊处理
     * @param mat
     * @return
     */
    public static Mat blur (Mat mat) {
        Mat blur = new Mat();
        Imgproc.blur(mat,blur,new Size(5,5));
        return blur;
    }

    /**
     *膨胀
     * @param mat
     * @return
     */
    public static Mat dilate (Mat mat,int size){
        Mat dilate=new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size,size));
        //膨胀
        Imgproc.dilate(mat, dilate, element, new Point(-1, -1), 1);
        return dilate;
    }

    /**
     * 腐蚀
     * @param mat
     * @return
     */
    public static Mat erode (Mat mat,int size){
        Mat erode=new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size,size));
        //腐蚀
        Imgproc.erode(mat, erode, element, new Point(-1, -1), 1);
        return erode;
    }

    /**
     * 边缘检测
     * @param mat
     * @return
     */
    public static Mat carry(Mat mat){
        Mat dst=new Mat();
        //高斯平滑滤波器卷积降噪
        Imgproc.GaussianBlur(mat, dst, new Size(3,3), 0);
        //边缘检测
        Imgproc.Canny(mat, dst, 50, 150);
        return dst;
    }

    /**
     * 轮廓检测
     * @param mat
     * @return
     */
    public static List<MatOfPoint> findContours(Mat mat){
        List<MatOfPoint> contours=new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    /**
     * 清除小面积轮廓
     * @param mat
     * @param size
     * @return
     */
    public static Mat drawContours(Mat mat,int size){
        List<MatOfPoint> cardContours=OpencvUtil.findContours(mat);
        for (int i = 0; i < cardContours.size(); i++)
        {
            double area=OpencvUtil.area(cardContours.get(i));
            if(area<size){
                Imgproc.drawContours(mat, cardContours, i, new Scalar( 0, 0, 0),-1 );
            }
        }
        return mat;
    }



    /**
     * 计算角度
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @return
     */
    public static double  getAngle(double px1, double py1, double px2, double py2) {
        //两点的x、y值
        double x = px2-px1;
        double y = py2-py1;
        double hypotenuse = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
        //斜边长度
        double cos = x/hypotenuse;
        double radian = Math.acos(cos);
        //求出弧度
        double angle = 180/(Math.PI/radian);
        //用弧度算出角度
        if (y<0) {
            angle = -angle;
        } else if ((y == 0) && (x<0)) {
            angle = 180;
        }
        while (angle<0){
            angle = angle +90;
        }
        return angle;
    }


    /**
     * 根据四点坐标截取模板图片
     * @param mat
     * @param pointList
     * @return
     */
    public static Mat shear (Mat mat,List<Point> pointList){
        int x=minX(pointList);
        int y=minY(pointList);
        int xl=xLength(pointList)>mat.cols()-x?mat.cols()-x:xLength(pointList);
        int yl=yLength(pointList)>mat.rows()-y?mat.rows()-y:yLength(pointList);
        Rect re=new Rect(x,y,xl,yl);
        return new Mat(mat,re);
    }


    /**
     * 图片旋转
     * @param splitImage
     * @param angle
     * @return
     */
    public static Mat rotate3(Mat splitImage, double angle){
        double thera = angle * Math.PI / 180;
        double a = Math.sin(thera);
        double b = Math.cos(thera);

        int wsrc = splitImage.width();
        int hsrc = splitImage.height();

        int wdst = (int) (hsrc * Math.abs(a) + wsrc * Math.abs(b));
        int hdst = (int) (wsrc * Math.abs(a) + hsrc * Math.abs(b));
        Mat imgDst = new Mat(hdst, wdst, splitImage.type());

        Point pt = new Point(splitImage.cols() / 2, splitImage.rows() / 2);
        // 获取仿射变换矩阵
        Mat affineTrans = Imgproc.getRotationMatrix2D(pt, angle, 1.0);

        //System.out.println(affineTrans.dump());
        // 改变变换矩阵第三列的值
        affineTrans.put(0, 2, affineTrans.get(0, 2)[0] + (wdst - wsrc) / 2);
        affineTrans.put(1, 2, affineTrans.get(1, 2)[0] + (hdst - hsrc) / 2);

        Imgproc.warpAffine(splitImage, imgDst, affineTrans, imgDst.size(),
                Imgproc.INTER_CUBIC | Imgproc.WARP_FILL_OUTLIERS);
        return imgDst;
    }



    /**
     * Mat转换成BufferedImag
     * @param matrix
     *            要转换的Mat
     * @param fileExtension
     *            格式为 ".jpg", ".png", etc
     * @return
     */
    public static BufferedImage Mat2BufImg (Mat matrix, String fileExtension) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param original
     *            要转换的BufferedImage
     * @param imgType
     *            bufferedImage的类型 如 BufferedImage.TYPE_3BYTE_BGR
     * @param matType
     *            转换成mat的type 如 CvType.CV_8UC3
     */
    public static Mat BufImg2Mat (BufferedImage original, int imgType, int matType) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }
        if (original.getType() != imgType) {
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }
        DataBufferByte dbi =(DataBufferByte)original.getRaster().getDataBuffer();
        byte[] pixels = dbi.getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }


    /**
     * 获取轮廓的面积
     * @param contour
     * @return
     */
    public static double area (MatOfPoint contour){
        MatOfPoint2f mat2f=new MatOfPoint2f();
        contour.convertTo(mat2f,CvType.CV_32FC1);
        RotatedRect rect=Imgproc.minAreaRect(mat2f);
        return rect.boundingRect().area();
    }


    /**
     * 获取最小的X坐标
     * @param points
     * @return
     */
    public  static int minX(List<Point> points){
        Collections.sort(points, new XComparator(false));
        return (int)(points.get(0).x>0?points.get(0).x:-points.get(0).x);
    }

    /**
     * 获取最小的Y坐标
     * @param points
     * @return
     */
    public  static int minY(List<Point> points){
        Collections.sort(points, new YComparator(false));
        return (int)(points.get(0).y>0?points.get(0).y:-points.get(0).y);
    }

    /**
     * 获取最长的X坐标距离
     * @param points
     * @return
     */
    public static int xLength(List<Point> points){
        Collections.sort(points, new XComparator(false));
        return (int)(points.get(3).x-points.get(0).x);
    }

    /**
     * 获取最长的Y坐标距离
     * @param points
     * @return
     */
    public  static int yLength(List<Point> points){
        Collections.sort(points, new YComparator(false));
        return (int)(points.get(3).y-points.get(0).y);
    }

    //集合排序规则（根据X坐标排序）
    public static class XComparator implements Comparator<Point> {
        private boolean reverseOrder; // 是否倒序
        public XComparator(boolean reverseOrder) {
            this.reverseOrder = reverseOrder;
        }

        public int compare(Point arg0, Point arg1) {
            if(reverseOrder)
                return (int)arg1.x - (int)arg0.x;
            else
                return (int)arg0.x - (int)arg1.x;
        }
    }

    //集合排序规则（根据Y坐标排序）
    public static class YComparator implements Comparator<Point> {
        private boolean reverseOrder; // 是否倒序
        public YComparator(boolean reverseOrder) {
            this.reverseOrder = reverseOrder;
        }

        public int compare(Point arg0, Point arg1) {
            if(reverseOrder)
                return (int)arg1.y - (int)arg0.y;
            else
                return (int)arg0.y - (int)arg1.y;
        }
    }


}
