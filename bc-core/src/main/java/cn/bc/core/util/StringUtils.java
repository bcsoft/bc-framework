package cn.bc.core.util;

import cn.bc.core.exception.CoreException;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 字符串辅助工具类
 *
 * @author dragon
 */
public class StringUtils {
	protected static Logger logger = LoggerFactory.getLogger(StringUtils.class);

	private StringUtils() {
	}

	// ============================== 字符串编码转换 ==============================
	public static String fromEncode = "ISO-8859-1";
	public static String toEncode = "UTF-8";
	public static boolean encodeToCN = false;

	/**
	 * 获取对象的字符串表示
	 *
	 * @param source
	 * @return
	 */
	public static String toString(Object source) {
		return source != null ? source.toString() : null;
	}

	/**
	 * 获取对象的字符串表示,null值转换为长度为0的字符串
	 *
	 * @param source
	 * @return
	 */
	public static String null2Empty(Object source) {
		return source != null ? source.toString() : "";
	}

	/**
	 * 将指定的字符串转换成toEncode的编码
	 *
	 * @param isoString
	 * @return
	 */
	public static String encodeToCN(String isoString) {
		if (isoString == null || isoString.length() == 0)
			return "";
		if (encodeToCN) {
			return encode(isoString, fromEncode, toEncode);
		} else {
			return isoString;
		}
	}

	/**
	 * 将指定的字符串转换成fromEncode的编码
	 *
	 * @param cnString
	 * @return
	 */
	public static String encodeToEN(String cnString) {
		if (cnString == null || cnString.length() == 0)
			return "";
		return encode(cnString, toEncode, fromEncode);
	}

	/**
	 * 对字符串进行转码
	 *
	 * @param srcString  所要转换的字符串
	 * @param fromEncode 字符串源编码
	 * @param toEncode   字符串目标编码
	 * @return 转换后的字符串
	 * @since 4.4.0
	 */
	public static String encode(String srcString, String fromEncode,
	                            String toEncode) {
		if (srcString == null || srcString.length() == 0)
			return "";

		try {
			String returnString = new String(srcString.getBytes(fromEncode),
					toEncode);
			return returnString;
		} catch (Exception e) {
			return "";
		}
	}

	// ============================== 字符串格式化 ==============================

	/**
	 * 对字符串进行格式化，格式化的方式为：对源字符串(sourceString)中的{n}(其中n表示数字，从0开始)进行替换
	 *
	 * @param sourceString 源字符串
	 * @param args         所要格式化的参数信息
	 * @return 返回格式化后的字符串
	 */
	public static String formater(String sourceString, Object[] args) {
		if (sourceString == null || sourceString.length() == 0)
			return "";
		if (null == args || args.length == 0)
			return sourceString;
		return MessageFormat.format(sourceString, args);
	}

	/**
	 * 验证字符串是否是可转换为数字格式
	 *
	 * @param value 要验证的字符串
	 * @return 如果输入的字符串可转换为数字格式则返回true，否则返回false
	 */
	public static boolean isNumeric(String value) {
		try {
			if (value == null || value.length() == 0)
				return false;
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/**
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 *
	 * @param str 所要转换的字符串
	 * @return 返回以16进制表示的字符串
	 */
	public static String String2Hex(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/**
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 *
	 * @param bytes 所要解码的字符串
	 * @return 返回解码后的字符串
	 */
	public static String Hex2String(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);

		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/**
	 * 将字符串数组转换成Long数组
	 *
	 * @param sArray
	 * @return
	 */
	public static Long[] stringArray2LongArray(String[] sArray) {
		if (null == sArray || sArray.length == 0)
			return null;
		Long[] longArray = new Long[sArray.length];
		for (int i = 0; i < sArray.length; i++) {
			try {
				longArray[i] = new Long(sArray[i]);
			} catch (NumberFormatException e) {
				longArray[i] = new Long(0);
			}
		}
		return longArray;
	}

	/**
	 * 将字符串数组转换成Integer数组
	 *
	 * @param sArray
	 * @return
	 */
	public static Integer[] stringArray2IntegerArray(String[] sArray) {
		if (null == sArray || sArray.length == 0)
			return null;
		Integer[] longArray = new Integer[sArray.length];
		for (int i = 0; i < sArray.length; i++) {
			try {
				longArray[i] = new Integer(sArray[i]);
			} catch (NumberFormatException e) {
				longArray[i] = new Integer(0);
			}
		}
		return longArray;
	}

	/**
	 * 在字符串两边添加双引号
	 *
	 * @param str 源字符串
	 * @return
	 */
	public static String wrapQuota(String str) {
		return "\"" + str + "\"";
	}

	public static HtmlCompressor compressor = new HtmlCompressor();

	/**
	 * 压缩Html代码
	 *
	 * @param html 要压缩Html代码
	 * @return
	 */
	public static String compressHtml(String html) {
		return compressor.compress(html);
	}

	/**
	 * 计算子串出现的次数
	 *
	 * @param str
	 * @param sub
	 * @return
	 */
	public static int countMatches(String str, String sub) {
		if (str == null || str.length() == 0 || sub == null
				|| sub.length() == 0) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while ((idx = str.indexOf(sub, idx)) != -1) {
			count++;
			idx += sub.length();
		}
		return count;
	}

	private static DecimalFormat sizeInfoFormater = new DecimalFormat("#.#");

	/**
	 * 格式化文件大小为易读的格式，如10Bytes、10.2KB、20.3MB
	 *
	 * @param size 已字节为单位的文件大小
	 * @return
	 */
	public static String formatSize(long size) {
		if (size < 1024)
			return sizeInfoFormater.format(size) + "Bytes";
		else if (size < 1024 * 1024)
			return sizeInfoFormater.format(size / 1024) + "KB";
		else
			return sizeInfoFormater.format(size / 1024 / 1024) + "MB";
	}

	/**
	 * 转换字符串值到指定的数据类型
	 *
	 * @param type  值类型："int"|"long"|"Long"|"float"|"date"|"startDate"|"endDate"|
	 *              "calendar"| "startCalendar"|"endCalendar"|
	 *              "int[]"|"long[]"|"money"
	 * @param value 字符串值
	 * @return
	 */
	public static Object convertValueByType(String type, String value) {
		if (type == null || type.length() == 0)
			return value;

		if (type.equals("int")) {
			return new Integer(value);
		} else if (type.equals("string")) {
			return value;
		} else if (type.equalsIgnoreCase("long")) {
			return value == null || value.isEmpty() ? null : new Long(value);
		} else if (type.equals("float")) {
			return value == null || value.isEmpty() ? null : new Float(value);
		} else if (type.equals("date")) {
			return DateUtils.getDate(value);
		} else if (type.equals("startDate")) {
			Date date = DateUtils.getDate(value);
			DateUtils.setToZeroTime(date);
			return date;
		} else if (type.equals("endDate")) {
			Date date = DateUtils.getDate(value);
			DateUtils.setToMaxTime(date);
			return date;
		} else if (type.equals("calendar")) {
			return DateUtils.getCalendar(value);
		} else if (type.equals("startCalendar")) {
			Calendar calendar = DateUtils.getCalendar(value);
			DateUtils.setToZeroTime(calendar);
			return calendar;
		} else if (type.equals("endCalendar")) {
			Calendar calendar = DateUtils.getCalendar(value);
			DateUtils.setToMaxTime(calendar);
			return calendar;
		} else if (type.equals("boolean")) {
			return value == null || value.isEmpty() ? null : new Boolean(value);
		} else if (type.equals("list")) {
			return JsonUtils.toCollection(value);
		} else if (type.equals("map")) {
			return JsonUtils.toMap(value);
		} else if (type.equals("array")) {
			return JsonUtils.toArray(value);
		} else if (type.equals("int[]")) {
			String subStr = value.substring(1, value.length() - 1);
			String[] strArray = subStr.split(",");
			int[] intArray = new int[strArray.length];
			for (int i = 0; i < strArray.length; i++) {
				intArray[i] = Integer.parseInt(strArray[i]);
			}
			return intArray;
		} else if (type.equals("long[]")) {
			String subStr = value.substring(1, value.length() - 1);
			String[] strArray = subStr.split(",");
			long[] longArray = new long[strArray.length];
			for (int i = 0; i < strArray.length; i++) {
				longArray[i] = Long.parseLong(strArray[i]);
			}
			return longArray;
		} else if (type.equals("float[]")) {
			String subStr = value.substring(1, value.length() - 1);
			String[] strArray = subStr.split(",");
			float[] floatArray = new float[strArray.length];
			for (int i = 0; i < strArray.length; i++) {
				floatArray[i] = Float.parseFloat(strArray[i]);
			}
			return floatArray;
		} else if (type.equals("string[]")) {
			String subStr = value.substring(1, value.length() - 1);
			String[] strArray = subStr.split(",");
			for (int i = 0; i < strArray.length; i++) {
				strArray[i] = strArray[i].replaceAll("\"", "");
			}
			return strArray;
		} else if (type.equals("boolean[]")) {
			String subStr = value.substring(1, value.length() - 1);
			String[] strArray = subStr.split(",");
			boolean[] booleanArray = new boolean[strArray.length];
			for (int i = 0; i < strArray.length; i++) {
				booleanArray[i] = Boolean.parseBoolean(strArray[i]);
			}
			return booleanArray;
		} else if (type.equals("money")) {
			if (value == null || value.isEmpty()) return null;
			return new BigDecimal(value.replace(",", ""));
		} else {
			throw new CoreException("unsupport value type: type=" + type
					+ ",value=" + value);
		}
	}

	/**
	 * 字符串反转
	 *
	 * @param s
	 * @return
	 */
	public static String reverse(String s) {
		return new StringBuffer(s).reverse().toString();
	}


	/**
	 * 多位数字转换为中文繁体并且补零 如8000 转后为 捌仟零佰零拾零
	 *
	 * @param n
	 * @return
	 */
	public static String number2Chinese(String n) {
		String num1[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖",};
		String num2[] = {"", "拾", "佰", "仟", "万", "亿", "兆", "吉", "太", "拍", "艾"};
		n = n.indexOf(".") > 0 ? n.substring(0, n.indexOf(".")) : n;

		int len = n.length();

		if (len <= 5) {
			String ret = "";
			for (int i = 0; i < len; ++i) {
				ret = ret + num1[n.substring(i, i + 1).charAt(0) - '0']
						+ num2[len - i - 1];
			}
			return ret;
		} else if (len <= 8) {
			String ret = multiDigit2Chinese(n.substring(0, len - 4));
			if (ret.length() != 0)
				ret += num2[4];
			return ret + multiDigit2Chinese(n.substring(len - 4));
		} else {
			String ret = multiDigit2Chinese(n.substring(0, len - 8));
			if (ret.length() != 0)
				ret += num2[5];
			return ret + multiDigit2Chinese(n.substring(len - 8));
		}
	}

	/**
	 * 把数字格式化为中文的金钱格式
	 *
	 * @param n
	 * @return
	 */
	public static String multiDigit2ChineseMoney(String n) {
		String s1 = "", s2 = "";
		if (n.indexOf(".") > 0) {
			s1 = n.substring(0, n.indexOf("."));
			s2 = n.substring(n.indexOf(".") + 1);
		} else {
			return number2Chinese(n) + "元零角零分";
		}
		String value = number2Chinese(s1) + "元";
		String num1[] = {"角", "分"};
		for (int i = 0; i < 2 && i < s2.length(); i++) {
			value += siginDigit2Chinese(Integer.valueOf(s2.charAt(i) + "")) + num1[i];
		}
		return value;
	}


	/**
	 * 多位数字转换为中文繁体
	 *
	 * @param n
	 * @return
	 */
	public static String multiDigit2Chinese(String n) {
		String num1[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖",};
		String num2[] = {"", "拾", "佰", "仟", "万", "亿", "兆", "吉", "太", "拍", "艾"};
		n = n.indexOf(".") > 0 ? n.substring(0, n.indexOf(".")) : n;

		int len = n.length();

		if (len <= 5) {
			String ret = "";
			for (int i = 0; i < len; ++i) {
				if (n.charAt(i) == '0') {
					int j = i + 1;
					while (j < len && n.charAt(j) == '0')
						++j;
					if (j < len)
						ret += "零";
					i = j - 1;
				} else
					ret = ret + num1[n.substring(i, i + 1).charAt(0) - '0']
							+ num2[len - i - 1];
			}
			return ret;
		} else if (len <= 8) {
			String ret = multiDigit2Chinese(n.substring(0, len - 4));
			if (ret.length() != 0)
				ret += num2[4];
			return ret + multiDigit2Chinese(n.substring(len - 4));
		} else {
			String ret = multiDigit2Chinese(n.substring(0, len - 8));
			if (ret.length() != 0)
				ret += num2[5];
			return ret + multiDigit2Chinese(n.substring(len - 8));
		}
	}

	/**
	 * 个位数字转换为中文繁体
	 *
	 * @param n
	 * @return
	 */
	public static String siginDigit2Chinese(Object n) {
		String num[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
		if (n == null)
			return null;
		if (n instanceof Integer && Integer.parseInt(n.toString()) < 10) {
			return num[(Integer) n];
		} else if (n instanceof Long && Long.parseLong(n.toString()) < 10) {
			return num[Integer.parseInt(n.toString())];
		} else {
			return n.toString();
		}
	}

	/**
	 * 从类资源文件中获取文本内容并格式化
	 *
	 * @param path 资源路径
	 * @param args 格式化参数
	 * @return 用 FreeMarker 格式化后的内容
	 */
	public static String getContentFromClassResource(String path, Map<String, Object> args) {
		InputStream in = StringUtils.class.getClassLoader().getResourceAsStream(path);
		try {
			String sql = FreeMarkerUtils.format(FileCopyUtils.copyToString(new InputStreamReader(in)), args);
			logger.debug("sql={}", sql);
			return sql;
		} catch (IOException e) {
			throw new CoreException(e);
		}
	}
}