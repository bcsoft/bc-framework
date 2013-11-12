package cn.bc.photo.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * QR码工具类
 * 
 * @author dragon
 */
public final class QRCodeUtils {
	// private static Log logger = LogFactory.getLog(QRCodeUtils.class);
	/** 默认的信息编码 */
	public static String DEFAULT_ENCODE = "UTF-8";
	/** 默认的图片大小 */
	public static int DEFAULT_SIZE = 300;
	/** 默认的图片格式 */
	public static String DEFAULT_FORMAT = "png";

	private static Hashtable<EncodeHintType, String> encodeHints;
	private static Hashtable<DecodeHintType, String> decodeHints;

	private QRCodeUtils() {
		// 默认的编码参数
		encodeHints = new Hashtable<EncodeHintType, String>();
		encodeHints.put(EncodeHintType.CHARACTER_SET, DEFAULT_ENCODE);

		// 默认的解码参数
		decodeHints = new Hashtable<DecodeHintType, String>();
		decodeHints.put(DecodeHintType.CHARACTER_SET, DEFAULT_ENCODE);
	}

	/**
	 * 生成QR码
	 * 
	 * @param content
	 *            QR码包含的信息
	 * @param file
	 *            生成的QR码文件
	 * @return
	 */
	public static void encode(String content, File file) throws Exception {
		MultiFormatWriter barcodeWriter = new MultiFormatWriter();
		BitMatrix matrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE,
				DEFAULT_SIZE, DEFAULT_SIZE, encodeHints);

		// 自动创建文件所在的目录
		if (!file.exists())
			file.mkdirs();

		// 确定文件格式
		String format = getFormat(file);

		// 生成文件
		MatrixToImageWriter.writeToFile(matrix, format, file);
	}

	private static String getFormat(File file) {
		// 确定文件格式
		String format = null;
		int i = file.getName().lastIndexOf(".");
		if (i != -1) {
			format = file.getName().substring(i + 1);
		}
		if (format == null || format.isEmpty()) {
			format = DEFAULT_FORMAT;
		}
		return format;
	}

	/**
	 * 生成QR码
	 * 
	 * @param content
	 *            QR码包含的信息
	 * @param stream
	 *            生成的QR码输出流
	 * @param format
	 *            QR码格式，如png、jpg
	 * @return
	 */
	public static void encode(String content, OutputStream stream, String format)
			throws Exception {
		MultiFormatWriter barcodeWriter = new MultiFormatWriter();
		BitMatrix matrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE,
				DEFAULT_SIZE, DEFAULT_SIZE, encodeHints);
		if (format == null || format.isEmpty()) {
			format = DEFAULT_FORMAT;// 默认的格式
		}

		// 生成文件
		MatrixToImageWriter.writeToStream(matrix, format, stream);
	}

	/**
	 * 获取QR码包含的信息
	 * 
	 * @param file
	 *            QR码文件
	 * @return
	 * @throws Exception
	 */
	public static String decode(File file) throws Exception {
		BufferedImage image = ImageIO.read(file);
		return decode(image);
	}

	/**
	 * 获取QR码包含的信息
	 * 
	 * @param inputStream
	 *            QR码信息流
	 * @return
	 * @throws Exception
	 */
	public static String decode(InputStream inputStream) throws Exception {
		BufferedImage image = ImageIO.read(inputStream);
		return decode(image);
	}

	/**
	 * 获取QR码包含的信息
	 * 
	 * @param image
	 *            QR码图片
	 * @return
	 * @throws Exception
	 */
	public static String decode(BufferedImage image) throws Exception {
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		MultiFormatReader barcodeReader = new MultiFormatReader();
		Result result = barcodeReader.decode(bitmap, decodeHints);
		return result.getText();
	}
}