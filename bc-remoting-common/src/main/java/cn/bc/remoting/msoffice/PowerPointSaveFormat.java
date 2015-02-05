/**
 * 
 */
package cn.bc.remoting.msoffice;

import cn.bc.core.exception.CoreException;

/**
 * 调用 PowerPoint 的 SaveAs2 方法转换文档格式时使用的格式参数常数定义
 * 
 * @author dragon
 * @see PowerPoint 2010 VBA 中 PpSaveAsFileType  权举的定义 https://msdn.microsoft.com/en-us/library/office/ff746389(v=office.14).aspx
 * Office2007 https://msdn.microsoft.com/en-us/library/bb251061(v=office.12).aspx
 */
public enum PowerPointSaveFormat {
	/** PDF 格式 (*.pdf) */
	PDF("pdf", 32),

	/** PowerPoint 默认格式(*.pptx) */
	PPTX("pptx", 1),

	/** PowerPoint 旧格式(*.ppt) */
	PPT("ppt", 0),

	/** PowerPoint 默认格式(*.pptx) */
	DEFAULT("pptx", 11),

	/** PowerPoint 2007+ 启用宏的文档 (*.pptm) */
	PPTM("pptm", 5),

	/** PNG 格式 (*.png) */
	BMP("bmp", 19),

	/** PNG 格式 (*.png) */
	PNG("png", 18),

    /** JPG 格式 (*.jpg) */
    JPG("png", 17),

    /** GIF 格式 (*.gif) */
    GIF("gif", 7),

    /** slideshow 格式 (*.ppsx) */
    SHOW("ppsx", 16),

    /** GIF 格式 (*.tif) */
    TIF("tif", 21),

	/** RTF 格式 (*.rtf) */
	RTF("rtf", 6),

	/** HTML 格式 (*.html) */
	HTML("html", 12);

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

	PowerPointSaveFormat(String key, int value) {
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
	public static PowerPointSaveFormat get(String key) {
		if (key == null)
			return null;
		if (key.equalsIgnoreCase(PowerPointSaveFormat.PPTX.getKey())) {
			return PowerPointSaveFormat.PPTX;
        } else if (key.equalsIgnoreCase(PowerPointSaveFormat.DEFAULT.getKey())) {
            return PowerPointSaveFormat.DEFAULT;
		} else if (key.equalsIgnoreCase(PowerPointSaveFormat.PDF.getKey())) {
			return PowerPointSaveFormat.PDF;
		} else if (key.equalsIgnoreCase(PowerPointSaveFormat.PPT.getKey())) {
			return PowerPointSaveFormat.PPT;
		} else if (key.equalsIgnoreCase(PowerPointSaveFormat.PPTM.getKey())) {
			return PowerPointSaveFormat.PPTM;
		} else if (key.equalsIgnoreCase(PowerPointSaveFormat.BMP.getKey())) {
			return PowerPointSaveFormat.BMP;
        } else if (key.equalsIgnoreCase(PowerPointSaveFormat.PNG.getKey())) {
            return PowerPointSaveFormat.PNG;
        } else if (key.equalsIgnoreCase(PowerPointSaveFormat.JPG.getKey())) {
            return PowerPointSaveFormat.JPG;
        } else if (key.equalsIgnoreCase(PowerPointSaveFormat.GIF.getKey())) {
            return PowerPointSaveFormat.GIF;
        } else if (key.equalsIgnoreCase(PowerPointSaveFormat.TIF.getKey())) {
            return PowerPointSaveFormat.TIF;
		} else if (key.equalsIgnoreCase(PowerPointSaveFormat.HTML.getKey())) {
			return PowerPointSaveFormat.HTML;
		} else if (key.equalsIgnoreCase(PowerPointSaveFormat.SHOW.getKey())) {
			return PowerPointSaveFormat.SHOW;
		} else {
			throw new CoreException("unsupport format:key=" + key);
		}
	}
}