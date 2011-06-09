package cn.bc.core.util;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 字符串辅助工具类
 * 
 * @author dragon
 */
public class StringUtils {
	protected static Log logger = LogFactory.getLog(StringUtils.class);
	private StringUtils() {}
	
	// ============================== 字符串编码转换 ==============================
    public static String fromEncode = "ISO-8859-1";
	public static String toEncode = "UTF-8";
	public static boolean encodeToCN = false;
    
    /**
     * 将指定的字符串转换成toEncode的编码
     * @param isoString
     * @return
     */
    public static String encodeToCN(String isoString) {
        if(isoString == null || isoString.length() == 0)
            return "";
        if(encodeToCN){
        	return encode(isoString, fromEncode, toEncode);
        }else{
        	return isoString;
        }
    }
    
    /**
     * 将指定的字符串转换成fromEncode的编码
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
     * @param srcString 所要转换的字符串
     * @param fromEncode 字符串源编码
     * @param toEncode 字符串目标编码
     * @return 转换后的字符串
     * @since 4.4.0
     */
    public static String encode(String srcString, String fromEncode, String toEncode) {
        if(srcString == null || srcString.length() == 0)
            return "";
        
        try {
        	String returnString = new String(srcString.getBytes(fromEncode), toEncode);
            return returnString;
        } catch (Exception e) {
            return "";
        }
    }
    
	// ============================== 字符串格式化 ==============================
    /**
     * 对字符串进行格式化，格式化的方式为：对源字符串(sourceString)中的{n}(其中n表示数字，从0开始)进行替换
     * @param sourceString 源字符串
     * @param args 所要格式化的参数信息
     * @return 返回格式化后的字符串
     */
    public static String formater(String sourceString, Object[] args){
        if(sourceString == null || sourceString.length() == 0)
            return "";
        if(null == args || args.length == 0)
            return sourceString;
        return MessageFormat.format(sourceString, args);
    }
	
    /**
     * 验证字符串是否是可转换为数字格式
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
	 * @param bytes 所要解码的字符串
	 * @return 返回解码后的字符串
	 */
	public static String Hex2String(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}
    
    /**
     * 将字符串数组转换成Long数组
     * @param sArray
     * @return
     */
    public static Long[] stringArray2LongArray(String[] sArray){
        if(null == sArray || sArray.length == 0)
            return null;
        Long[] longArray = new Long[sArray.length];
        for(int i = 0; i < sArray.length; i++){
            try {
                longArray[i] = new Long(sArray[i]);
            } catch (NumberFormatException e) {
                longArray[i] = new Long(0);
            } 
        }
        return longArray;
    }
    
	/**
	 * 在字符串两边添加双引号
	 * 
	 * @param str
	 *            源字符串
	 * @return
	 */
	public static String wrapQuota(String str) {
		return "\"" + str + "\"";
	}
}