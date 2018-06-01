/**
 *
 */
package cn.bc.remoting.msoffice;

import cn.bc.core.exception.CoreException;

/**
 * 调用Word的SaveAs2方法转换文档格式时使用的格式参数常数定义
 *
 * @author dragon
 * @see Word 2010 VBA 中 WdSaveFormat 权举的定义 https://msdn.microsoft.com/en-us/library/office/ff839952.aspx
 */
public enum WordSaveFormat {
  /**
   * PDF 格式 (*.pdf)
   */
  PDF("pdf", 17),

  /**
   * Word 2007+ 文档 (*.docx)
   */
  DOCX("docx", 12),

  /**
   * Word 2007+ 启用宏的文档 (*.docm)
   */
  DOCM("docm", 13),

  /**
   * Word 2007+ XML 文档 (*.xml)
   */
  XML_2007PLUS("xml", 19),

  /**
   * Word 97-2003 文件 (*.doc)
   */
  DOC("doc", 0),

  /**
   * Word 2003 XML 文档 (*.xml)
   */
  XML_2003("xml", 11),

  /**
   * 纯文本 (*.txt)
   */
  TXT("txt", 2),

  /**
   * RTF 格式 (*.rtf)
   */
  RTF("rtf", 6),

  /**
   * HTML 格式 (*.html)
   */
  HTML("html", 8),

  /**
   * 单个文件网页 (*.mht)
   */
  MHT("mht", 9);

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

  WordSaveFormat(String key, int value) {
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
  public static WordSaveFormat get(String key) {
    if (key == null)
      return null;
    if (key.equalsIgnoreCase(WordSaveFormat.DOCX.getKey())) {
      return WordSaveFormat.DOCX;
    } else if (key.equalsIgnoreCase(WordSaveFormat.PDF.getKey())) {
      return WordSaveFormat.PDF;
    } else if (key.equalsIgnoreCase(WordSaveFormat.DOC.getKey())) {
      return WordSaveFormat.DOC;
    } else if (key.equalsIgnoreCase(WordSaveFormat.DOCM.getKey())) {
      return WordSaveFormat.DOCM;
    } else if (key.equalsIgnoreCase(WordSaveFormat.TXT.getKey())) {
      return WordSaveFormat.TXT;
    } else if (key.equalsIgnoreCase(WordSaveFormat.RTF.getKey())) {
      return WordSaveFormat.RTF;
    } else if (key.equalsIgnoreCase(WordSaveFormat.HTML.getKey())) {
      return WordSaveFormat.HTML;
    } else if (key.equalsIgnoreCase(WordSaveFormat.MHT.getKey())) {
      return WordSaveFormat.MHT;
    } else if (key.equalsIgnoreCase(WordSaveFormat.TXT.getKey())) {
      return WordSaveFormat.TXT;
    } else if (key.equalsIgnoreCase(WordSaveFormat.XML_2007PLUS.getKey())) {
      return WordSaveFormat.XML_2007PLUS;
    } else {
      throw new CoreException("unsupport format:key=" + key);
    }
  }
}
