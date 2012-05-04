/**
 * 
 */
package cn.bc.docs.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import cn.bc.core.exception.CoreException;

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
	private static OpenOfficeConnection connection;
	private static DocumentConverter converter;
	private static DocumentFormatRegistry formaters;

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
		} catch (ConnectException e) {
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