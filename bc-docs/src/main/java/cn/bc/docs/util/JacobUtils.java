/**
 * 
 */
package cn.bc.docs.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

/**
 * Jacob 文档转换工具
 * 
 * @author dragon
 * 
 */
public class JacobUtils {
	private static Log logger = LogFactory.getLog(JacobUtils.class);
	private static final int wdFormatPDF = 17;
	private static final int xlTypePDF = 0;
	private static final int ppSaveAsPDF = 32;

	public void convert2PDF(String inputFile, String pdfFile) {
		String suffix = getFileSufix(inputFile);
		File file = new File(inputFile);
		if (!file.exists()) {
			logger.error("文件不存在！");
			return;
		}
		if (suffix.equals("pdf")) {
			logger.error("PDF not need to convert!");
			return;
		}
		if (suffix.equals("doc") || suffix.equals("docx")
				|| suffix.equals("txt")) {
			word2PDF(inputFile, pdfFile);
		} else if (suffix.equals("ppt") || suffix.equals("pptx")) {
			ppt2PDF(inputFile, pdfFile);
		} else if (suffix.equals("xls") || suffix.equals("xlsx")) {
			excel2PDF(inputFile, pdfFile);
		} else {
			logger.error("文件格式不支持转换!");
		}
	}

	public void convert2PDF(String inputFile) {
		String pdfFile = getFilePrefix(inputFile) + ".pdf";
		convert2PDF(inputFile, pdfFile);

	}

	public static void word2PDF(String inputFile, String pdfFile) {
		// 打开word应用程序
		ActiveXComponent app = new ActiveXComponent("Word.Application");

		// 设置word不可见
		app.setProperty("Visible", false);

		// 调用Application对象的Documents属性，获得Documents对象
		Dispatch docs = app.getProperty("Documents").toDispatch();

		// 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
		Dispatch doc = Dispatch.call(docs, "Open",// 调用Documents对象的Open方法
				inputFile,// 输入文件路径全名
				false,// ConfirmConversions，设置为false表示不显示转换框
				true// ReadOnly
				).toDispatch();

		// Office 2011 调用Document对象的SaveAs方法，将文档保存为pdf格式
		// Dispatch.call(doc, "SaveAs", pdfFile, wdFormatPDF);

		// Office 2007+
		Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF);

		// 关闭文档
		Dispatch.call(doc, "Close", false);

		// 关闭word应用程序
		app.invoke("Quit", 0);
	}

	public static void excel2PDF(String inputFile, String pdfFile) {
		ActiveXComponent app = new ActiveXComponent("Excel.Application");
		app.setProperty("Visible", false);
		Dispatch excels = app.getProperty("Workbooks").toDispatch();
		Dispatch excel = Dispatch.call(excels, "Open", inputFile, false, true)
				.toDispatch();
		Dispatch.call(excel, "ExportAsFixedFormat", xlTypePDF, pdfFile);
		Dispatch.call(excel, "Close", false);
		app.invoke("Quit");

	}

	public static void ppt2PDF(String inputFile, String pdfFile) {

		ActiveXComponent app = new ActiveXComponent("PowerPoint.Application");
		// app.setProperty("Visible", msofalse);
		Dispatch ppts = app.getProperty("Presentations").toDispatch();

		Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true,// ReadOnly
				true,// Untitled指定文件是否有标题
				false// WithWindow指定文件是否可见
				).toDispatch();

		Dispatch.call(ppt, "SaveAs", pdfFile, ppSaveAsPDF);

		Dispatch.call(ppt, "Close");

		app.invoke("Quit");

	}

	private static String getFilePrefix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, splitIndex);
	}

	private static String getFileSufix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(splitIndex + 1);
	}

}