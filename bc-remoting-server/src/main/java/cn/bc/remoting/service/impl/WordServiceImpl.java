package cn.bc.remoting.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import cn.bc.remoting.msoffice.ExcelSaveFormat;
import cn.bc.remoting.msoffice.WordSaveFormat;
import cn.bc.remoting.service.WordService;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

/**
 * 文档转换服务接口的实现
 *
 * @author dragon
 */
public class WordServiceImpl implements WordService {
	private static Logger logger = LoggerFactory.getLogger(WordServiceImpl.class);
	private static String TEMP_DIR = "/data_rmi/temp";
	private static String FROM_ROOT_DIR = "/data_rmi/source";
	private static String TO_ROOT_DIR = "/data_rmi/convert";
	private boolean compatible = false;
	private DateFormat df4fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
	private DateFormat df4yearMonth = new SimpleDateFormat("yyyyMM");

	public void setTempDir(String tempDir) {
		TEMP_DIR = tempDir;
	}

	public void setFromRootDir(String fromRootDir) {
		FROM_ROOT_DIR = fromRootDir;
	}

	public void setToRootDir(String toRootDir) {
		TO_ROOT_DIR = toRootDir;
	}

	public void setCompatible(boolean compatible) {
		this.compatible = compatible;
	}

	public boolean test(String token) throws RemoteException {
		Assert.hasText(token, "token could not be null.");
		logger.debug("test:token={}", token);
		return true;
	}

	public boolean convertFormat(String token, String fromFile, String toFile) throws RemoteException {
		// 参数验证
		Assert.notNull(token, "token could not be null.");
		Assert.hasText(fromFile, "fromFile could not be null.");
		Assert.hasText(toFile, "toFile could not be null.");

		// 根据文件名获取文件格式
		String fromFormat = StringUtils.getFilenameExtension(fromFile).toLowerCase();
		String toFormat = StringUtils.getFilenameExtension(toFile).toLowerCase();

		// 检测来源文件必须存在，否则抛出异常
		fromFile = FROM_ROOT_DIR + "/" + fromFile;
		toFile = TO_ROOT_DIR + "/" + toFile;

		// 转换路径中的/为\避免Windows打开保存文件的路径错误
		fromFile = fromFile.replaceAll("/", "\\\\");
		toFile = toFile.replaceAll("/", "\\\\");
		if (logger.isDebugEnabled()) {
			logger.debug("convertFormat:token={}", token);
			logger.debug("convertFormat:fromFile={}", fromFile);
			logger.debug("convertFormat:toFile={}", toFile);
		}

		File from = new File(fromFile);
		if (!from.exists()) {
			throw new RemoteException("FileNotFound:fromFile=" + fromFile);
		}

		// 创建目标文件所在的目录
		File f = new File(toFile);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		// 根据来源文件格式调用相应的方法进行文档格式转换
		if (isWordFormat(fromFormat)) {// Word文档格式转换
			convertByWord(fromFile, toFile, WordSaveFormat.get(toFormat).getValue());
		} else if (isExcelFormat(fromFormat)) {// Excel文档格式转换
			convertByExcel(fromFile, toFile, ExcelSaveFormat.get(toFormat).getValue());
		} else {
			throw new RemoteException("unsupport file type: fromFormat="+ fromFormat);
		}

		// 记录一条转换日志
		logger.warn("convert:token={}, fromFile={}, toFile={}", token, fromFile, toFile);
		return true;
	}

	/**
	 * 判断是否是 Excel 可以打开的文档格式
	 *
	 * @param fromFormat
	 * @return
	 */
	private boolean isExcelFormat(String fromFormat) {
		return "xls".equals(fromFormat) || "xlsx".equals(fromFormat)
				|| "xlsm".equals(fromFormat) || "csv".equals(fromFormat);
	}

	/**
	 * 判断是否是 Word 可以打开的文档格式
	 *
	 * @param fromFormat
	 * @return
	 */
	private boolean isWordFormat(String fromFormat) {
		return "doc".equals(fromFormat) || "docx".equals(fromFormat)
				|| "docm".equals(fromFormat) || "txt".equals(fromFormat)
				|| "rtf".equals(fromFormat);
	}

	public byte[] convertFormat(String token, byte[] source, String fromFormat, String toFormat) throws RemoteException {
		Assert.notNull(source, "source could not be null.");
		InputStream t = convert(token, new ByteArrayInputStream(source), fromFormat, toFormat);
		try {
			return FileCopyUtils.copyToByteArray(t);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			throw new RemoteException(e.getMessage(), e);
		}
	}

	/**
	 * 转换文件流
	 *
	 * @param token
	 * @param source
	 * @param fromFormat
	 * @param toFormat
	 * @return
	 * @throws IOException
	 */
	private InputStream convert(String token, InputStream source, String fromFormat, String toFormat) throws RemoteException {
		try {
			Assert.hasText(token, "token could not be null.");
			Assert.notNull(source, "source could not be null.");
			Assert.notNull(fromFormat, "fromFormat could not be null.");
			Assert.notNull(toFormat, "toFormat could not be null.");
			if (logger.isDebugEnabled()) {
				logger.debug("convert:token=" + token);
				logger.debug("convert:fromFormat=" + fromFormat);
				logger.debug("convert:toFormat=" + toFormat);
			}
			Date now = new Date();

			String dateDir = df4yearMonth.format(now);
			String fileName = df4fileName.format(now);
			// 将原始文件流保存为临时文件
			String tempDir = TEMP_DIR + "/" + dateDir + "/";
			File dir = new File(tempDir);
			if (!dir.exists())
				dir.mkdirs();
			String fromName = fileName + "." + fromFormat;
			String fromFile = tempDir + fromName;
			fromFile = fromFile.replaceAll("/", "\\\\");// 转换路径中的/为\避免Windows打开保存文件的路径错误
			logger.debug("convert:fromFile={}", fromFile);
			FileCopyUtils.copy(source, new FileOutputStream(fromFile));

			// 转换后的文件保存到的路径
			String toName = fileName + ".to." + toFormat;
			String toFile = tempDir + toName;
			toFile = toFile.replaceAll("/", "\\\\");// 转换路径中的/为\避免Windows打开保存文件的路径错误
			logger.debug("convert:toFile={}", toFile);

			if (isWordFormat(fromFormat)) {// Word文档格式转换
				convertByWord(fromFile, toFile, WordSaveFormat.get(toFormat).getValue());
			} else if (isExcelFormat(fromFormat)) {// Excel文档格式转换
				convertByExcel(fromFile, toFile, ExcelSaveFormat.get(toFormat).getValue());
			} else {
				throw new RemoteException("unsupport file type: fromFormat="+ fromFormat);
			}

			// 检测转换后的文件是否存在
			File target = new File(toFile);
			if (!target.exists()) {
				throw new RemoteException("convert failed: fromFormat=" + fromFormat + ",toFile=" + toFile);
			}

			// 返回文件流
			return new FileInputStream(target);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			throw new RemoteException(e.getMessage(), e);
		}
	}

	/**
	 * 使用jacob调用Office服务执行文档格式转换
	 *
	 * @param fromFile 来源文件的绝对路径
	 * @param toFile   转换后的文件要保存到的绝对路径
	 * @param toFormat
	 */
	private void convertByWord(String fromFile, String toFile, int toFormat) {
		ActiveXComponent wordApp = null;
		try {
			logger.info("----Word: Start----");

			// 打开word应用程序
			wordApp = new ActiveXComponent("Word.Application");

			// 设置word不可见
			wordApp.setProperty("Visible", false);

			// 调用Application对象的Documents属性，获得Documents对象
			Dispatch wordDocuments = wordApp.getProperty("Documents").toDispatch();

			// 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
			Dispatch doc = Dispatch.call(wordDocuments, "Open",// 调用Documents对象的Open方法
					fromFile,// 输入文件路径全名
					false,// ConfirmConversions，设置为false表示不显示转换框
					true// ReadOnly
			).toDispatch();

			// 检测目标文件是否存在，存在就先删除
			File f = new File(toFile);
			if (f.exists()) {
				logger.warn("file exists, delete it first: file={}", toFile);
				f.delete();
			}

			if (this.compatible) {
				// Office 2007+ 调用ExportAsFixedFormat:目标文件不能存在，否则报错
				// (2007需要按装插件才能支持此方法：http://www.microsoft.com/zh-cn/download/details.aspx?id=7)
				logger.debug("----Word: ExportAsFixedFormat----");
				Dispatch.call(doc, "ExportAsFixedFormat", toFile, toFormat);
			} else {
				// Office 2010 调用Document对象的SaveAs2方法:目标文件不能存在，否则报错
				logger.debug("----Word: SaveAs2----");
				Dispatch.call(doc, "SaveAs2", toFile, toFormat);
			}

			// 关闭文档
			Dispatch.call(doc, "Close", false);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (wordApp != null) {
				try {
					logger.info("----Word: Quit----");
					wordApp.invoke("Quit", 0);
				} catch (Exception e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
	}

	private void convertByExcel(String fromFile, String toFile, int toFormat) {
		ActiveXComponent excelApp = null;
		try {
			if (logger.isInfoEnabled()) {
				logger.info("----Excel: Start----");
				logger.info("fromFile={}", fromFile);
				logger.info("toFile={}", toFile);
				logger.info("toFormat={}", toFormat);
			}

			// 打开Excel应用程序
			excelApp = new ActiveXComponent("Excel.Application");

			// 设置Excel不可见
			excelApp.setProperty("Visible", false);

			// 调用Application对象的Documents属性，获得Documents对象
			Dispatch workbooks = excelApp.getProperty("Workbooks").toDispatch();

			// 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
			Dispatch workbook = Dispatch.call(workbooks, "Open",// 调用Documents对象的Open方法
					fromFile,// 输入文件路径全名
					false,// UpdateLinks
					true// ReadOnly
			).toDispatch();

			// Dispatch sheet = Dispatch.get(workbook,
			// "ActiveSheet").toDispatch();// 获取Workbook对象的ActiveSheet属性

			// 检测目标文件是否存在，存在就先删除
			File f = new File(toFile);
			if (f.exists()) {
				logger.warn("file exists, delete it first: file={}", toFile);
				f.delete();
			}

			if (this.compatible || toFormat == ExcelSaveFormat.PDF.getValue()) {
				// Office 2007+ 调用ExportAsFixedFormat:目标文件不能存在，否则报错
				// (2007需要按装插件才能支持此方法：http://www.microsoft.com/zh-cn/download/details.aspx?id=7)
				logger.debug("----Excel: ExportAsFixedFormat----");
				Dispatch.call(workbook, "ExportAsFixedFormat", toFormat, toFile);
			} else {
				// Office 2010 调用Document对象的SaveAs方法:目标文件不能存在，否则报错
				logger.debug("----Excel: SaveAs----");
				Dispatch.call(workbook, "SaveAs", toFile, toFormat);
			}

			// 关闭文档
			Dispatch.call(workbook, "Close", false);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (excelApp != null) {
				try {
					logger.info("----Excel: Quit----");
					excelApp.invoke("Quit");
				} catch (Exception e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
	}
}