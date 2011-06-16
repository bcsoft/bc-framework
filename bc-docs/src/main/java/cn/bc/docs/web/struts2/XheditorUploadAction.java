/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.util.DateUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 上传附件Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class XheditorUploadAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 16 * 1024;
	// private static Log logger = LogFactory.getLog(UploadAction.class);
	/**
	 * json的格式 参考: http://xheditor.com/manual/2 的上传程序开发规范
	 * 返回内容必需是标准的json字符串，结构可以是如下： {"err":"","msg":"200906030521128703.gif"}
	 * 或者
	 * {"err":"","msg":{"url":"200906030521128703.jpg","localfile":"test.jpg"
	 * ,"id":"1"}} 注：第二种结构适用于html5
	 */
	public String json;
	private File filedata;
	private String filedataContentType;
	private String filedataFileName;

	public String img() throws Exception {
		String imageFileName = DateUtils.formatDateTime(new Date(),"yyyyMMddHHmmssSSSS") + getExtention(filedataFileName);
		File imageFile = new File(getText("app.data.xheditor") + File.separator + imageFileName);
		copy(filedata, imageFile);
		this.json = "{\"err\":\"\",\"msg\":\"1111.gif\"}";
		return "json";
	}

	public String flash() throws Exception {
		this.json = "{\"err\":\"\",\"msg\":\"1111.swf\"}";
		return "json";
	}

	public String media() throws Exception {
		this.json = "{\"err\":\"\",\"msg\":\"1111.avi\"}";
		return "json";
	}

	private static void copy(File src, File dst) {
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(src),
						BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dst),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (in.read(buffer) > 0) {
					out.write(buffer);
				}
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getExtention(String fileName) {
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(pos);
	}

	public File getFiledata() {
		return filedata;
	}

	public void setFiledata(File filedata) {
		this.filedata = filedata;
	}

	public String getFiledataContentType() {
		return filedataContentType;
	}

	public void setFiledataContentType(String filedataContentType) {
		this.filedataContentType = filedataContentType;
	}

	public String getFiledataFileName() {
		return filedataFileName;
	}

	public void setFiledataFileName(String filedataFileName) {
		this.filedataFileName = filedataFileName;
	}
}
