package cn.bc.docs.web;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AttachUtils {
  /**
   * 获取指定扩展名的文件对应的ContentType
   * <p>
   * http://www.w3.org/TR/html4/types.html
   * </p>
   *
   * @param extend 文件的扩展名
   * @return
   */
  public static String getContentType(String extend) {
    //if(true) return "application/x-msdownload";
    if ("pdf".equalsIgnoreCase(extend)) {
      return "application/pdf";
    } else if ("svg".equalsIgnoreCase(extend)) {
      return "image/svg+xml";
    } else if ("jpg".equalsIgnoreCase(extend)
      || "jpeg".equalsIgnoreCase(extend)) {
      return "image/jpeg";
    } else if ("png".equalsIgnoreCase(extend)) {
      return "image/png";
    } else if ("gif".equalsIgnoreCase(extend)) {
      return "image/gif";
    } else if ("txt".equalsIgnoreCase(extend)) {
      return "text/plain;charset=UTF-8";
    } else if ("xls".equalsIgnoreCase(extend)) {
      return "application/vnd.ms-excel";
    } else if ("xlsx".equalsIgnoreCase(extend)) {
      return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    } else if ("doc".equalsIgnoreCase(extend)) {
      return "application/msword";
    } else if ("docx".equalsIgnoreCase(extend)) {
      return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    } else if ("html".equalsIgnoreCase(extend)
      || "htm".equalsIgnoreCase(extend)) {
      return "text/html;charset=UTF-8";
    } else if ("css".equalsIgnoreCase(extend)) {
      return "text/css;charset=UTF-8";
    } else if ("js".equalsIgnoreCase(extend)) {
      return "text/javascript;charset=UTF-8";
    } else if ("tif".equalsIgnoreCase(extend)
      || "tiff".equalsIgnoreCase(extend)) {
      return "image/tiff";
    } else if ("rtf".equalsIgnoreCase(extend)) {
      return "application/rtf";
    } else if ("zip".equalsIgnoreCase(extend)) {
      return "application/zip";
    } else if ("apk".equalsIgnoreCase(extend)) {
      return "application/vnd.android.package-archive";
    } else if ("exe".equalsIgnoreCase(extend)) {
      return "application/x-msdownload";//application/x-msdos-program
    } else if ("mp3".equalsIgnoreCase(extend)) {
      return "audio/mp3";
    } else if ("wav".equalsIgnoreCase(extend)) {
      return "audio/wav";
    } else {
      return "application/octet-stream";
    }
  }

  private static NumberFormat sizeFormater = new DecimalFormat("#.#");

  /**
   * 将文件大小的数值转化为友好的显示字符串
   *
   * @param size
   * @return
   */
  public static String getSizeInfo(long size) {
    if (size < 1024)// 字节
      return size + "Bytes";
    else if (size < 1024 * 1024)// KB
      return sizeFormater.format(((float) size) / 1024f) + "KB";
    else
      // MB
      return sizeFormater.format(((float) size) / 1024f / 1024f) + "MB";
  }
}
