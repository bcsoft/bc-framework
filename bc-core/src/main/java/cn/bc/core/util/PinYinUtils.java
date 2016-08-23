package cn.bc.core.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 中文拼音工具类
 *
 * @author dragon
 */
public class PinYinUtils {
	protected static Log logger = LogFactory.getLog(PinYinUtils.class);

	private PinYinUtils() {
	}

	/**
	 * 全局小写拼音格式化对象
	 */
	private static HanyuPinyinOutputFormat lowercaseFormatter;

	/**
	 * 全局大写拼音格式化对象
	 */
	private static HanyuPinyinOutputFormat uppercaseFormatter;

	static {
		// 静态构造函数，初始化拼音格式化对象
		lowercaseFormatter = new HanyuPinyinOutputFormat();
		lowercaseFormatter.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		lowercaseFormatter.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		lowercaseFormatter.setVCharType(HanyuPinyinVCharType.WITH_V);

		uppercaseFormatter = new HanyuPinyinOutputFormat();
		uppercaseFormatter.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		uppercaseFormatter.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		uppercaseFormatter.setVCharType(HanyuPinyinVCharType.WITH_V);
	}

	/**
	 * 将指定的字符串转换为小写拼音
	 *
	 * @param source 要转换的字符串
	 * @return 转换为小写拼音的字符串，如果要转换的字符包含有非中文的字符，这些非中文字符将不作转换。
	 */
	public static String getPinYin(String source) {
		return getLowercasePingYin(source);
	}

	/**
	 * 将指定的字符串转换为小写拼音
	 *
	 * @param source 要转换的字符串
	 * @return 转换为小写拼音的字符串，如果要转换的字符包含有非中文的字符，这些非中文字符将不作转换。
	 */
	public static String getLowercasePingYin(String source) {
		return getPingYinWithFormater(source, lowercaseFormatter);
	}

	/**
	 * 将指定的字符串转换为大写拼音
	 *
	 * @param source 要转换的字符串
	 * @return 转换为大写拼音的字符串，如果要转换的字符包含有非中文的字符，这些非中文字符将不作转换。
	 */
	public static String getUppercasePingYin(String source) {
		return getPingYinWithFormater(source, uppercaseFormatter);
	}

	/**
	 * 将指定的字符串转换为拼音
	 *
	 * @param source   要转换的字符串
	 * @param formater 格式化对象
	 * @return 转换为拼音的字符串，如果要转换的字符包含有非中文的字符，这些非中文字符将不作转换。
	 */
	public static String getPingYinWithFormater(String source, HanyuPinyinOutputFormat formater) {
		if (source == null || source.length() == 0)
			return source;

		char[] sourceChars = source.toCharArray();
		String[] t2;
		String target = "";
		int len = sourceChars.length;
		try {
			for (int i = 0; i < len; i++) {
				if (java.lang.Character.toString(sourceChars[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(sourceChars[i], formater);
					target += t2[0];
				} else
					target += java.lang.Character.toString(sourceChars[i]);
			}
			return target;
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return target;
	}
}