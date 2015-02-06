package cn.bc.remoting.service;

import java.rmi.RemoteException;

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
	 *            原始文档的相对路径，开头不要字符"/"，父路径由服务端的配置决定
	 * @param toFile
	 *            转换后文档存放的相对路径，开头不要字符"/"，父路径由服务端的配置决定
	 * @return 成功返回true
	 * @throws RemoteException
	 */
	boolean convertFormat(String token, String fromFile, String toFile) throws RemoteException;

	/**
	 * 将文档转换为指定格式
	 * 
	 * @param token
	 *            执行令牌
	 * @param source
	 *            原始文档的字节数据
	 * @param fromFormat
	 *            原始文档的格式，对应文件的扩展名，如doc
	 * @param toFormat
	 *            转换后文档的格式，对应文件的扩展名，如pdf
	 * @return 返回转换后文档的字节数据
	 * @throws RemoteException
	 */
	byte[] convertFormat(String token, byte[] source, String fromFormat, String toFormat) throws RemoteException;
}
