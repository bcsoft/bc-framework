package cn.bc.remoting.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import cn.bc.remoting.msoffice.WordSaveFormat;
import cn.bc.remoting.service.WordService;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

/**
 * 文档转换服务接口的实现
 * 
 * @author dragon
 * 
 */
public class WordServiceImpl implements WordService {
	private static Log logger = LogFactory.getLog(WordServiceImpl.class);
	private static String ROOT_PATH = "D:/t";
	private DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

	public boolean test(String token) {
		Assert.hasText(token, "token could not be null.");
		if (logger.isDebugEnabled()) {
			logger.debug("test:token=" + token);
		}
		return true;
	}

	public boolean convertFormat(String token, String fromFile, String toFile)
			throws IOException {
		Assert.notNull(token, "token could not be null.");
		Assert.hasText(fromFile, "fromFile could not be null.");
		Assert.hasText(toFile, "toFile could not be null.");
		WordSaveFormat fromFormat = WordSaveFormat.get(getFileSufix(fromFile));
		WordSaveFormat toFormat = WordSaveFormat.get(getFileSufix(toFile));

		fromFile = ROOT_PATH + "/" + fromFile;
		toFile = ROOT_PATH + "/" + toFile;
		if (logger.isDebugEnabled()) {
			logger.debug("fromFile=" + fromFile);
			logger.debug("toFile=" + toFile);
		}
		File from = new File(fromFile);
		if (!from.exists()) {
			throw new FileNotFoundException();
		}

		if (fromFormat == WordSaveFormat.DOC
				|| fromFormat == WordSaveFormat.DOCX
				|| fromFormat == WordSaveFormat.TXT
				|| fromFormat == WordSaveFormat.RTF) {
			convertByWord(fromFile, toFile, toFormat.getValue());
		} else {
			throw new RemoteException("unsupport file type: fromFormat="
					+ fromFormat);
		}

		return true;
	}

	public byte[] convertFormat(String token, byte[] source,
			WordSaveFormat fromFormat, WordSaveFormat toFormat)
			throws IOException {
		Assert.notNull(source, "source could not be null.");
		InputStream t = convert(token, new ByteArrayInputStream(source),
				fromFormat, toFormat);
		return FileCopyUtils.copyToByteArray(t);
	}

	private InputStream convert(String token, InputStream source,
			WordSaveFormat fromFormat, WordSaveFormat toFormat)
			throws IOException {
		Assert.hasText(token, "token could not be null.");
		Assert.notNull(source, "source could not be null.");
		Assert.notNull(fromFormat, "fromFormat could not be null.");
		Assert.notNull(toFormat, "toFormat could not be null.");
		if (logger.isDebugEnabled()) {
			logger.debug("token=" + token);
			logger.debug("fromFormat=" + fromFormat.getKey());
			logger.debug("toFormat=" + toFormat.getKey());
		}
		Date now = new Date();

		// 将原始文件流保存为临时文件
		String fromDir = ROOT_PATH + "/from/" + fromFormat.getKey() + "/";
		File dir = new File(fromDir);
		if (!dir.exists())
			dir.mkdirs();
		String fromName = df.format(now) + "." + fromFormat.getKey();
		String fromFile = fromDir + fromName;
		if (logger.isDebugEnabled()) {
			logger.debug("fromFile=" + fromFile);
		}
		FileCopyUtils.copy(source, new FileOutputStream(fromFile));

		// 转换后的文件保存到的路径
		String toDir = ROOT_PATH + "/to/" + toFormat.getKey() + "/";
		dir = new File(toDir);
		if (!dir.exists())
			dir.mkdirs();
		String toName = df.format(now) + "." + toFormat.getKey();
		String toFile = toDir + toName;
		if (logger.isDebugEnabled()) {
			logger.debug("toFile=" + toFile);
		}

		if (fromFormat == WordSaveFormat.DOC
				|| fromFormat == WordSaveFormat.DOCX
				|| fromFormat == WordSaveFormat.TXT
				|| fromFormat == WordSaveFormat.RTF) {
			convertByWord(fromFile, toFile, toFormat.getValue());
		} else {
			throw new RemoteException("unsupport file type: fromFormat="
					+ fromFormat);
		}

		// 检测转换后的文件是否存在
		File target = new File(toFile);
		if (!target.exists()) {
			throw new RemoteException("convert failed: fromFormat="
					+ fromFormat.getKey() + ",toFile=" + toFile);
		}

		// 返回文件流
		InputStream t = new FileInputStream(target);
		return t;
	}

	private void convertByWord(String inputFile, String toFile, int toFormat) {
		if (logger.isDebugEnabled()) {
			logger.debug("inputFile=" + inputFile);
			logger.debug("toFile=" + toFile);
			logger.debug("toFormat=" + toFormat);
		}
		// 打开word应用程序
		ActiveXComponent wordApp = new ActiveXComponent("Word.Application");

		// 设置word不可见
		wordApp.setProperty("Visible", false);

		// 调用Application对象的Documents属性，获得Documents对象
		Dispatch wordDocuments = wordApp.getProperty("Documents").toDispatch();

		// 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
		Dispatch doc = Dispatch.call(wordDocuments, "Open",// 调用Documents对象的Open方法
				inputFile,// 输入文件路径全名
				false,// ConfirmConversions，设置为false表示不显示转换框
				true// ReadOnly
				).toDispatch();

		// Office 2010 调用Document对象的SaveAs2方法
		Dispatch.call(doc, "SaveAs2", toFile, toFormat);

		// Office 2007+ 通用
		// (2007需要按装插件才能支持此方法：http://www.microsoft.com/zh-cn/download/details.aspx?id=7)
		// Dispatch.call(doc, "ExportAsFixedFormat", toFile, toFormat);

		// 关闭文档
		Dispatch.call(doc, "Close", false);

		wordApp.invoke("Quit", 0);
	}

	/**
	 * 获取文件扩展名，如“a/b.txt”返回“txt”
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	private static String getFileSufix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(splitIndex + 1);
	}

}
