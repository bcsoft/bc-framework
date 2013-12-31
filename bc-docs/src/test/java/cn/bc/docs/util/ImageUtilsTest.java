package cn.bc.docs.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageUtilsTest {
    private String path = "E:\\t\\image\\"; // 测试用图片所在路径

    @Test
    // 水平合并测试
    public void combineHorizontal() throws Exception {
        String fileType = "jpg";
        File img1 = new File(path + "01." + fileType);
        File img2 = new File(path + "02." + fileType);
        Imager[] imagers = new Imager[2];
        imagers[0] = new Imager(new FileInputStream(img1),0,10,0,0);
        imagers[1] = new Imager(new FileInputStream(img2));
        BufferedImage newImg = ImageUtils.combineHorizontal(imagers);
        File outFile = new File(path + "Horizontal." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    // 垂直合并测试
    public void combineVertica() throws Exception {
        String fileType = "jpg";
        File img1 = new File(path + "01." + fileType);
        File img2 = new File(path + "02." + fileType);
        Imager[] imagers = new Imager[2];
        imagers[0] = new Imager(new FileInputStream(img1),0,0,10,0);
        imagers[1] = new Imager(new FileInputStream(img2));
        BufferedImage newImg = ImageUtils.combineVertical(imagers);
        File outFile = new File(path + "Vertical." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    // 混合合并测试：无间隙
    public void combineMix_noGap() throws Exception {
        String fileType = "jpg";
        InputStream[] images = new InputStream[3];
        images[0] = new FileInputStream(new File(path + "01." + fileType));
        images[1] = new FileInputStream(new File(path + "02." + fileType));
        images[2] = new FileInputStream(new File(path + "03." + fileType));
        BufferedImage newImg = ImageUtils.combineMix(images, "1,1;1");
        File outFile = new File(path + "Mix_noGap." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    // 水平合并测试：带10个像素间隙
    public void combineMix_gap() throws Exception {
        String fileType = "jpg";
        InputStream[] images = new InputStream[4];
        images[0] = new FileInputStream(new File(path + "01." + fileType));
        images[1] = new FileInputStream(new File(path + "02." + fileType));
        images[2] = new FileInputStream(new File(path + "03." + fileType));
        images[3] = new FileInputStream(new File(path + "04." + fileType));
        BufferedImage newImg = ImageUtils.combineMix(images, "1:0 10 10 0,1:0 0 10 0;1:0 10 0 0,1");
        File outFile = new File(path + "Mix_gap." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }
}