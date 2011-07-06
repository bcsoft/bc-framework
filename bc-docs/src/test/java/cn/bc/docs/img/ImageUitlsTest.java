package cn.bc.docs.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import cn.bc.docs.util.ImageUtils;

public class ImageUitlsTest {
	@Test
	public void testCropPng() throws Exception {
		String extension = "png";
		BufferedImage newImg = ImageUtils.crop(getTestImg(extension), 0, 0, 64,
				64, extension);
		ImageIO.write(newImg, extension, new File("target/test-crop."
				+ extension));
	}

	@Test
	public void testCropJpg() throws Exception {
		String extension = "jpg";
		BufferedImage newImg = ImageUtils.crop(getTestImg(extension), 0, 0, 64,
				64);
		ImageIO.write(newImg, extension, new File("target/test-crop."
				+ extension));
	}

	@Test
	public void testCropGif() throws Exception {
		String extension = "gif";
		BufferedImage newImg = ImageUtils.crop(getTestImg(extension), 0, 0, 64,
				64);
		ImageIO.write(newImg, extension, new File("target/test-crop."
				+ extension));
	}

	@Test
	public void testZoomInPng() throws Exception {
		String extension = "png";
		BufferedImage newImg = ImageUtils.zoom(getTestImg(extension), 0.5,
				extension);
		ImageIO.write(newImg, extension, new File("target/test-zoomin."
				+ extension));
	}

	@Test
	public void testZoomInJpg() throws Exception {
		String extension = "jpg";
		BufferedImage newImg = ImageUtils.zoom(getTestImg(extension), 0.5);
		ImageIO.write(newImg, extension, new File("target/test-zoomin."
				+ extension));
	}

	@Test
	public void testZoomInBmp() throws Exception {
		String extension = "bmp";
		BufferedImage newImg = ImageUtils.zoom(getTestImg(extension), 0.5);
		ImageIO.write(newImg, extension, new File("target/test-zoomin."
				+ extension));
	}

	@Test
	public void testZoomOutPng() throws Exception {
		String extension = "png";
		BufferedImage newImg = ImageUtils.zoom(getTestImg(extension), 2,
				extension);
		ImageIO.write(newImg, extension, new File("target/test-zoomout."
				+ extension));
	}

	@Test
	public void testZoomOutJpg() throws Exception {
		String extension = "jpg";
		BufferedImage newImg = ImageUtils.zoom(getTestImg(extension), 2);
		ImageIO.write(newImg, extension, new File("target/test-zoomout."
				+ extension));
	}

	@Test
	public void testZoomOutBmp() throws Exception {
		String extension = "bmp";
		BufferedImage newImg = ImageUtils.zoom(getTestImg(extension), 2);
		ImageIO.write(newImg, extension, new File("target/test-zoomout."
				+ extension));
	}

	@Test
	public void testZoomAndCropPng() throws Exception {
		String extension = "png";
		BufferedImage newImg = ImageUtils.zoomAndCrop(getTestImg(extension), 2,
				0, 0, 128, 128, extension);
		ImageIO.write(newImg, extension, new File("target/test-zoomAndCrop."
				+ extension));
	}

	@Test
	public void testZoomAndCropJpg() throws Exception {
		String extension = "jpg";
		BufferedImage newImg = ImageUtils.zoomAndCrop(getTestImg(extension), 2,
				0, 0, 128, 128, extension);
		ImageIO.write(newImg, extension, new File("target/test-zoomAndCrop."
				+ extension));
	}

	@Test
	public void testZoomAndCropBmp() throws Exception {
		String extension = "bmp";
		BufferedImage newImg = ImageUtils.zoomAndCrop(getTestImg(extension), 2,
				0, 0, 128, 128, extension);
		ImageIO.write(newImg, extension, new File("target/test-zoomAndCrop."
				+ extension));
	}

	@Test
	public void testCropAndZoomPng() throws Exception {
		String extension = "png";
		BufferedImage newImg = ImageUtils.cropAndZoom(getTestImg(extension), 0,
				0, 64, 64, 128, 128, extension);
		ImageUtils.write(newImg, "target/test-cropAndZoom." + extension,
				extension);
	}

	@Test
	public void testCropAndZoomJpg() throws Exception {
		String extension = "jpg";
		BufferedImage newImg = ImageUtils.cropAndZoom(getTestImg(extension), 0,
				0, 64, 64, 128, 128, extension);
		ImageUtils.write(newImg, "target/test-cropAndZoom." + extension,
				extension);
	}

	@Test
	public void testCropAndZoomBmp() throws Exception {
		String extension = "bmp";
		BufferedImage newImg = ImageUtils.cropAndZoom(getTestImg(extension), 0,
				0, 64, 64, 128, 128, extension);
		ImageUtils.write(newImg, "target/test-cropAndZoom." + extension,
				extension);
	}

	/**
	 * @param extension
	 *            扩展名，不含"."
	 * @return
	 * @throws IOException
	 */
	private InputStream getTestImg(String extension) throws IOException {
		return new ClassPathResource("cn/bc/docs/img/test" + "." + extension)
				.getInputStream();
	}
}
