/**
 * 
 */
package cn.bc.docs.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.SpringUtils;
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
	private static Log logger = LogFactory.getLog(OfficeUtils.class);
	private static OpenOfficeConnection connection;
	private static DocumentConverter converter;
	private static DocumentFormatRegistry formaters;
	private static boolean useRemotingService;
	private static WordService wordService;

	public void setUseRemotingService(boolean useRemotingService) {
		OfficeUtils.useRemotingService = useRemotingService;
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
			if (useRemotingService
					&& (fromType.equalsIgnoreCase("doc") || fromType
							.equalsIgnoreCase("docx"))) {// 使用远程服务执行转换:暂时只支持doc和docx文档
				if(logger.isInfoEnabled())
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