package cn.bc.remoting.service;

import java.io.IOException;

import cn.bc.remoting.msoffice.WordSaveFormat;

/**
 * Microsoft Word 2010 文档服务接口
 * 
 * @author dragon
 * 
 */
public interface WordService extends CommonService {
	/**
	 * 将文档转换为指定格式
	 * 
	 * @param token
	 *            执行令牌
	 * @param fromFile
	 *            原始文档的相对路径，开头不要字符"/"
	 * @param toFile
	 *            转换后文档存放的相对路径，开头不要字符"/"
	 * @return 成功返回true
	 * @throws IOException
	 */
	boolean convertFormat(String token, String fromFile, String toFile)
			throws IOException;

	/**
	 * 将文档转换为指定格式
	 * 
	 * @param token
	 *            执行令牌
	 * @param source
	 *            原始文档的字节数据
	 * @param fromFormat
	 *            原始文档的格式
	 * @param toFormat
	 *            转换后文档的格式
	 * @return 返回转换后文档的字节数据
	 * @throws IOException
	 */
	byte[] convertFormat(String token, byte[] source,
			WordSaveFormat fromFormat, WordSaveFormat toFormat)
			throws IOException;
}
