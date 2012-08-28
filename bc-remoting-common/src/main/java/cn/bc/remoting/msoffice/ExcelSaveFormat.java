/**
 * 
 */
package cn.bc.remoting.msoffice;

import cn.bc.core.exception.CoreException;

/**
 * 调用Excel的Sheet.SaveAs方法转换文档格式时使用的格式参数常数定义
 * 
 * @author dragon
 * @see Word 2010 VBA 中 WdSaveFormat 权举的定义
 */
public enum ExcelSaveFormat {
	/** PDF 格式 (*.pdf) */
	PDF("pdf", 0),

	/** XPS 文档 (*.xps) */
	XPS("xps", 1),

	/** Excel 97-2003 工作簿 (*.xls) */
	XLS("xls", 56),

	/** Excel 2007+ 工作簿 (*.xlsx) */
	XLSX("xlsx", 51),

	/** Excel 2007+ 启用宏的工作簿 (*.xlsm) */
	XLSM("xlsm", 52),

	/** Excel 2003 XML 电子表格 (*.xml) */
	XML_2003("xml", 46),

	/** 纯文本 (*.txt) */
	TXT("txt", 42),

	/** CSV 格式 (*.csv) */
	CSV("csv", 6),

	/** HTML 格式 (*.html) */
	HTML("html", 44),

	/** 单个文件网页 (*.mht) */
	MHT("mht", 45);

	private int value;
	private String key;

	/**
	 * 整型值
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 扩展名
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	ExcelSaveFormat(String key, int value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return "{name:" + this.name() + ",ordinal:" + this.ordinal() + ",key:"
				+ key + ",value:" + value + "}";
	}

	/**
	 * 获取格式类型
	 * 
	 * @param key
	 * @return
	 */
	public static ExcelSaveFormat get(String key) {
		if (key == null)
			return null;
		if (key.equalsIgnoreCase(ExcelSaveFormat.XLSX.getKey())) {
			return ExcelSaveFormat.XLSX;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.PDF.getKey())) {
			return ExcelSaveFormat.PDF;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.XLS.getKey())) {
			return ExcelSaveFormat.XLS;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.CSV.getKey())) {
			return ExcelSaveFormat.CSV;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.XLSM.getKey())) {
			return ExcelSaveFormat.XLSM;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.HTML.getKey())) {
			return ExcelSaveFormat.HTML;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.MHT.getKey())) {
			return ExcelSaveFormat.MHT;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.TXT.getKey())) {
			return ExcelSaveFormat.TXT;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.XPS.getKey())) {
			return ExcelSaveFormat.XPS;
		} else if (key.equalsIgnoreCase(ExcelSaveFormat.XML_2003.getKey())) {
			return ExcelSaveFormat.XML_2003;
		} else {
			throw new CoreException("unsupport format:key=" + key);
		}
	}
}
