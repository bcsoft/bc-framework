/**
 * 
 */
package cn.bc.docs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.SpringUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.remoting.msoffice.WordSaveFormat;
import cn.bc.remoting.service.WordService;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * 文档转换工具
 * 
 * @author dragon
 * 
 */
public class OfficeUtils {
	private static String REMOTING_TOKEN = "BC";
	private static Log logger = LogFactory.getLog(OfficeUtils.class);
	private static OpenOfficeConnection connection;
	private static DocumentConverter converter;
	private static DocumentFormatRegistry formaters;
	private static boolean useRemotingService;
	private static WordService wordService;

	public void setUseRemotingService(boolean useRemotingService) {
		OfficeUtils.useRemotingService = useRemotingService;
	}

	public void setRemotingToken(String remotingToken) {
		OfficeUtils.REMOTING_TOKEN = remotingToken;
	}

	private static WordService getWordService() {
		if (wordService != null) {
			return wordService;
		}

		wordService = SpringUtils.getBean(WordService.class);
		return wordService;
	}

	/**
	 * 获取指定文档的pdf格式
	 * <p>
	 * 此方法将会对转换结果进行缓存，
	 * </p>
	 * 
	 * @param fromFile
	 *            原始文档的相对路径，开头不要字符"/"，父路径由Attach.DATA_REAL_PATH 的值决定
	 * @param toType
	 *            转换后文档的格式，如"pdf"
	 * @param cache
	 *            是否使用缓存值，为true则会先检测"convert/[fromFileName].[toType]"是否存在，
	 *            存在就直接返回此文件不作任何转换
	 */
	public static InputStream convert(String fromFile, String toType,
			boolean cache) {
		try {
			// 构建目标文件的默认路径
			int i = fromFile.lastIndexOf(".");
			String toFile = fromFile.substring(0, i) + "." + toType;
			if (logger.isDebugEnabled()) {
				logger.debug("convert by path:fromFile=" + fromFile);
				logger.debug("convert by path:toType=" + toType);
				logger.debug("convert by path:cache=" + cache);
				logger.debug("convert by path:toFile=" + toFile);
			}

			// 检测目标文件是否存在：所有转换文件放在convert子目录下
			File _toFile = new File(Attach.DATA_REAL_PATH + "/convert/"
					+ toFile);
			if (cache) {
				if (_toFile.exists()) {
					if (logger.isDebugEnabled()) {
						logger.debug("convert by path:target exists. just return it.");
					}
					// 直接返回缓存的文件
					return new FileInputStream(_toFile);
				}
			}

			String fromType = StringUtils.getFilenameExtension(fromFile)
					.toLowerCase();
			if (useMSOfficeService(fromType)) {// 使用远程服务执行转换:暂时只支持doc、docx、docm、xls、xlsx、xlsm文档
				if (logger.isInfoEnabled())
					logger.info("--use wordService--");
				getWordService()
						.convertFormat(REMOTING_TOKEN, fromFile, toFile);

				// 返回转换成的文件
				return new FileInputStream(_toFile);
			} else {// 使用OpenOffice执行转换
				// 打开连接
				if (!getConnection().isConnected())
					getConnection().connect();

				// 创建目标文件所在的目录
				if (!_toFile.getParentFile().exists()) {
					_toFile.getParentFile().mkdirs();
				}

				// 执行转换
				getConverter().convert(
						new FileInputStream(Attach.DATA_REAL_PATH + "/"
								+ fromFile),
						getFormaters().getFormatByFileExtension(fromType),
						new FileOutputStream(_toFile),
						getFormaters().getFormatByFileExtension(toType));

				// 关闭连接
				if (getConnection().isConnected())
					getConnection().disconnect();

				// 返回转换成的文件
				return new FileInputStream(_toFile);
			}
		} catch (IOException e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 是否使用MSOffice的文档转换服务
	 * 
	 * @param fromType
	 * @return
	 */
	private static boolean useMSOfficeService(String fromType) {
		// 使用远程服务执行转换:暂时只支持Word和Excel文档
		return useRemotingService
				&& (fromType.equals("doc") || fromType.equals("docx")
						|| fromType.equals("docm") || fromType.equals("xls")
						|| fromType.equals("xlsx") || fromType.equals("xlsm"));
	}

	/**
	 * 转换文档类型
	 * 
	 * @param is
	 * @param fromType
	 *            原始类型
	 * @param os
	 * @param toType
	 *            转换为的类型
	 */
	public static void convert(InputStream is, String fromType,
			OutputStream os, String toType) {
		try {
			if (useMSOfficeService(fromType)) {// 使用远程服务执行转换
				if (logger.isInfoEnabled())
					logger.info("--use wordService--");
				if (wordService == null) {
					wordService = SpringUtils.getBean(WordService.class);
				}
				byte[] result = wordService.convertFormat("bcsystem",
						FileCopyUtils.copyToByteArray(is),
						WordSaveFormat.get(fromType),
						WordSaveFormat.get(toType));

				FileCopyUtils.copy(result, os);
			} else {// 使用OpenOffice执行转换
				// 打开连接
				if (!getConnection().isConnected())
					getConnection().connect();

				// 执行转换
				getConverter().convert(is,
						getFormaters().getFormatByFileExtension(fromType), os,
						getFormaters().getFormatByFileExtension(toType));

				// 关闭连接
				if (getConnection().isConnected())
					getConnection().disconnect();
			}
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}

	private static OpenOfficeConnection getConnection() throws ConnectException {
		if (connection != null)
			return connection;

		// TODO change config
		connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
		connection.connect();

		return connection;
	}

	private static DocumentFormatRegistry getFormaters() {
		if (formaters != null)
			return formaters;

		formaters = new DefaultDocumentFormatRegistry();

		return formaters;
	}

	private static DocumentConverter getConverter() throws ConnectException {
		if (converter != null)
			return converter;

		converter = new OpenOfficeDocumentConverter(getConnection());

		return converter;
	}
}