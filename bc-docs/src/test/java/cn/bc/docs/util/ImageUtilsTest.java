package cn.bc.docs.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageUtilsTest {
	@Test
	public void combineJpg() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		File img1 = new File("E:\\t\\image\\01.png");
		File img2 = new File("E:\\t\\image\\02.png");
		combineImage(img1, img2, "png");
	}

	@Test
	public void combineHorizontal4Png() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		File img1 = new File("E:\\t\\image\\01.png");
		File img2 = new File("E:\\t\\image\\02.png");
		String fileType = "png";
		InputStream[] images = new InputStream[2];
		images[0] = new FileInputStream(img1);
		images[1] = new FileInputStream(img2);
		BufferedImage newImg = ImageUtils.combineHorizontal(images,10);
		File outFile = new File("E:\\t\\image\\Horizontal." + fileType);
		ImageIO.write(newImg, fileType, outFile);// 写图片
	}

    @Test
    public void combineHorizontal4Jpg() throws Exception {
        // 需要将dll方法到jdk的bin目录下
        File img1 = new File("E:\\t\\image\\01.jpg");
        File img2 = new File("E:\\t\\image\\02.jpg");
        String fileType = "jpg";
        InputStream[] images = new InputStream[2];
        images[0] = new FileInputStream(img1);
        images[1] = new FileInputStream(img2);
        BufferedImage newImg = ImageUtils.combineHorizontal(images,10);
        File outFile = new File("E:\\t\\image\\Horizontal." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    public void combineHorizontal4JpgAndPng1() throws Exception {
        // 需要将dll方法到jdk的bin目录下
        File img1 = new File("E:\\t\\image\\01.png");
        File img2 = new File("E:\\t\\image\\02.jpg");
        String fileType = "jpg";
        InputStream[] images = new InputStream[2];
        images[0] = new FileInputStream(img1);
        images[1] = new FileInputStream(img2);
        BufferedImage newImg = ImageUtils.combineHorizontal(images);
        File outFile = new File("E:\\t\\image\\Horizontal.s." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    public void combineHorizontal4JpgAndPng2() throws Exception {
        // 需要将dll方法到jdk的bin目录下
        File img1 = new File("E:\\t\\image\\01.png");
        File img2 = new File("E:\\t\\image\\02.jpg");
        String fileType = "png";
        InputStream[] images = new InputStream[2];
        images[0] = new FileInputStream(img1);
        images[1] = new FileInputStream(img2);
        BufferedImage newImg = ImageUtils.combineHorizontal(images);
        File outFile = new File("E:\\t\\image\\Horizontal.s." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

	@Test
	public void combineVertica4Png() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		File img1 = new File("E:\\t\\image\\01.png");
		File img2 = new File("E:\\t\\image\\02.png");
		InputStream[] images = new InputStream[2];
		images[0] = new FileInputStream(img1);
		images[1] = new FileInputStream(img2);
		BufferedImage newImg = ImageUtils.combineVertical(images, 10);
		String fileType = "png";
		File outFile = new File("E:\\t\\image\\Vertical." + fileType);
		ImageIO.write(newImg, fileType, outFile);// 写图片
	}

    @Test
    public void combineVertica4Jpg() throws Exception {
        // 需要将dll方法到jdk的bin目录下
        File img1 = new File("E:\\t\\image\\01.jpg");
        File img2 = new File("E:\\t\\image\\02.jpg");
        InputStream[] images = new InputStream[2];
        images[0] = new FileInputStream(img1);
        images[1] = new FileInputStream(img2);
        BufferedImage newImg = ImageUtils.combineVertical(images,10);
        String fileType = "jpg";
        File outFile = new File("E:\\t\\image\\Vertical." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    public void combineVertica4JpgAndPng1() throws Exception {
        // 需要将dll方法到jdk的bin目录下
        File img1 = new File("E:\\t\\image\\01.png");
        File img2 = new File("E:\\t\\image\\02.jpg");
        InputStream[] images = new InputStream[2];
        images[0] = new FileInputStream(img1);
        images[1] = new FileInputStream(img2);
        BufferedImage newImg = ImageUtils.combineVertical(images);
        String fileType = "jpg";
        File outFile = new File("E:\\t\\image\\Vertical." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

    @Test
    public void combineVertica4JpgAndPng2() throws Exception {
        // 需要将dll方法到jdk的bin目录下
        File img1 = new File("E:\\t\\image\\01.png");
        File img2 = new File("E:\\t\\image\\02.jpg");
        InputStream[] images = new InputStream[2];
        images[0] = new FileInputStream(img1);
        images[1] = new FileInputStream(img2);
        BufferedImage newImg = ImageUtils.combineVertical(images);
        String fileType = "png";
        File outFile = new File("E:\\t\\image\\Vertical." + fileType);
        ImageIO.write(newImg, fileType, outFile);// 写图片
    }

	@Test
	public void combineMix01() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		File img1 = new File("E:\\t\\image\\01.png");
		File img2 = new File("E:\\t\\image\\02.png");
		InputStream[] images = new InputStream[2];
		images[0] = new FileInputStream(img1);
		images[1] = new FileInputStream(img2);
		BufferedImage newImg = ImageUtils.combineMix(images, "1,1");
		String fileType = "png";
		File outFile = new File("E:\\t\\image\\Mix01." + fileType);
		ImageIO.write(newImg, fileType, outFile);// 写图片
	}

	@Test
	public void combineMix02() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		File img1 = new File("E:\\t\\image\\01.png");
		File img2 = new File("E:\\t\\image\\02.png");
		InputStream[] images = new InputStream[2];
		images[0] = new FileInputStream(img1);
		images[1] = new FileInputStream(img2);
		BufferedImage newImg = ImageUtils.combineMix(images, "1;1");
		String fileType = "png";
		File outFile = new File("E:\\t\\image\\Mix02." + fileType);
		ImageIO.write(newImg, fileType, outFile);// 写图片
	}

	@Test
	public void combineMix03() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		File img1 = new File("E:\\t\\image\\01.png");
		File img2 = new File("E:\\t\\image\\02.png");
		File img3 = new File("E:\\t\\image\\03.png");
		InputStream[] images = new InputStream[3];
		images[0] = new FileInputStream(img1);
		images[1] = new FileInputStream(img2);
		images[2] = new FileInputStream(img3);
		BufferedImage newImg = ImageUtils.combineMix(images, "1,1;1");
		String fileType = "png";
		File outFile = new File("E:\\t\\image\\Mix03." + fileType);
		ImageIO.write(newImg, fileType, outFile);// 写图片
	}

	private void combineImage(File fileOne, File fileTwo, String fileType)
			throws IOException {
		// 读取第一张图片
		BufferedImage ImageOne = ImageIO.read(fileOne);
		int width1 = ImageOne.getWidth();// 图片宽度
		int height1 = ImageOne.getHeight();// 图片高度

		// 从图片中读取RGB
		int[] ImageArrayOne = new int[width1 * height1];
		ImageArrayOne = ImageOne.getRGB(0, 0, width1, height1, ImageArrayOne,
				0, width1);

		// 对第二张图片做相同的处理
		BufferedImage ImageTwo = ImageIO.read(fileTwo);
		int width2 = ImageTwo.getWidth();// 图片宽度
		int height2 = ImageTwo.getHeight();// 图片高度
		int[] ImageArrayTwo = new int[width2 * height2];
		ImageArrayTwo = ImageTwo.getRGB(0, 0, width2, height2, ImageArrayTwo,
				0, width2);

		// 生成新图片
		int width = width1 + width2;
		int height = Math.max(height1, height2);
		BufferedImage ImageNew = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		ImageNew.setRGB(0, 0, width1, height1, ImageArrayOne, 0, width1);// 设置左半部分的RGB
		ImageNew.setRGB(width1, 0, width2, height2, ImageArrayTwo, 0, width2);// 设置右半部分的RGB

		File outFile = new File("E:\\t\\image\\out." + fileType);
		ImageIO.write(ImageNew, fileType, outFile);// 写图片
	}
}
