package cn.bc.photo.webcam;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

/**
 * Java Webcam https://github.com/sarxos/webcam-capture
 * 
 * @author dragon
 */
public class TestWebcam {
	@Test
	public void getImage1024() throws Exception {
		Webcam webcam = Webcam.getDefault();

		// 获取设备支持的分辨率,默认为 176x144
		Dimension[] viewSizes = webcam.getViewSizes();
		System.out.println("viewSizes.count=" + viewSizes.length);
		for (Dimension d : viewSizes) {
			System.out.println(d);
		}

		webcam.setViewSize(new Dimension(1024, 768));
		webcam.open();
		ImageIO.write(webcam.getImage(), "JPG", new File("1024x768.jpg"));
	}

	@Test
	public void getImageCustom() throws Exception {
		/**
		 * When you set custom resolutions you have to be sure that your webcam
		 * device will handle them!
		 */

		// @formatter:off
		Dimension[] nonStandardResolutions = new Dimension[] {
				new Dimension(2000, 1600), new Dimension(2048, 1536),
				new Dimension(1600, 1200), new Dimension(1280, 1024),
				new Dimension(1024, 800), new Dimension(800, 600),
				new Dimension(1920, 1080), WebcamResolution.HD720.getSize() };
		// @formatter:on

		// your camera have to support HD720p to run this code
		Webcam webcam = Webcam.getDefault();

		// 获取设备支持的分辨率,默认为 176x144
		Dimension[] viewSizes = webcam.getViewSizes();
		System.out.println("viewSizes.count=" + viewSizes.length);
		for (Dimension d : viewSizes) {
			System.out.println(d);
		}

		webcam.setCustomViewSizes(nonStandardResolutions);
		webcam.setViewSize(nonStandardResolutions[2]);
		webcam.open();

		BufferedImage image = webcam.getImage();
		System.out
				.println("name=" + image.getWidth() + "x" + image.getHeight());
		ImageIO.write(image, "JPG",
				new File(image.getWidth() + "x" + image.getHeight() + ".jpg"));
	}

	@Test
	public void detectMotionExample() throws Exception {
		new DetectMotionExample();
		// System.in.read(); // keep program open
		Thread.sleep(60000);
		System.out.println("detectMotionExample:Bye!");
	}

	@Test
	public void webcamDiscoveryListenerExample() throws Exception {
		new WebcamDiscoveryListenerExample();
		// getImage();
		Thread.sleep(30000);
		System.out.println("webcamDiscoveryListenerExample:Bye!");
	}
}