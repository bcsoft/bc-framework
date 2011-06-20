package cn.bc.docs.web;

public class AttachUtils {
	/**
	 * 获取指定扩展名的文件对应的ContentType
	 * <p>http://www.w3.org/TR/html4/types.html</p>
	 * @param extend
	 *            文件的扩展名
	 * @return
	 */
	public static String getContentType(String extend) {
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
			return "text/plain";
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
			return "text/html";
		} else if ("css".equalsIgnoreCase(extend)) {
			return "text/css";
		} else if ("js".equalsIgnoreCase(extend)) {
			return "text/javascript";
		} else if ("tif".equalsIgnoreCase(extend)
				|| "tiff".equalsIgnoreCase(extend)) {
			return "image/tiff";
		} else if ("rtf".equalsIgnoreCase(extend)) {
			return "application/rtf";
		} else if ("zip".equalsIgnoreCase(extend)) {
			return "application/zip";
		} else {
			return "application/" + extend;
		}
	}
}
