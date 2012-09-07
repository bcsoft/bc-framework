/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.util.OfficeUtils;
import cn.bc.docs.web.AttachUtils;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 直接的物理附件处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FileAction extends ActionSupport {
	private static Log logger = LogFactory.getLog(FileAction.class);
	private static final long serialVersionUID = 1L;
	private AttachService attachService;

	public String filename;
	public String contentType;
	public String ct;// 用户自定义的contentType
	public long contentLength;
	public InputStream inputStream;
	public String path;
	public String ptype;
	public String puid;

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	// 下载附件
	public String download() throws Exception {
		Date startTime = new Date();
		// 附件的绝对路径名
		String path = Attach.DATA_REAL_PATH + "/" + this.f;

		// 附件的扩展名
		String extension = StringUtils.getFilenameExtension(path);

		// 下载的文件名
		if (this.n == null || this.n.length() == 0)
			this.n = StringUtils.getFilename(this.f);
		if (this.n.lastIndexOf(".") == -1)
			this.n += "." + extension;

		// debug
		if (logger.isDebugEnabled()) {
			logger.debug("path=" + path);
			logger.debug("extension=" + extension);
			logger.debug("n=" + n);
		}

		// 设置下载文件的参数
		if (ct != null && ct.length() > 0) {
			this.contentType = this.ct;
		} else {
			this.contentType = AttachUtils.getContentType(extension);
		}
		this.filename = WebUtils.encodeFileName(
				ServletActionContext.getRequest(), this.n);
		File file = new File(path);
		this.contentLength = file.length();
		this.inputStream = new FileInputStream(file);
		if (logger.isDebugEnabled()) {
			logger.debug("download:" + DateUtils.getWasteTime(startTime));
		}

		// 创建文件上传日志
		saveAttachHistory(AttachHistory.TYPE_DOWNLOAD, this.f, extension);

		return SUCCESS;
	}

	// 创建文件上传日志
	private void saveAttachHistory(int type, String path, String extension) {
		if (ptype != null && puid != null) {
			AttachHistory history = new AttachHistory();
			history.setPtype(ptype);
			history.setPuid(puid);
			history.setType(type);
			history.setAuthor(SystemContextHolder.get().getUserHistory());
			history.setFileDate(Calendar.getInstance());
			history.setPath(path);
			history.setAppPath(false);
			history.setFormat(extension);
			history.setSubject(n);
			String[] c = WebUtils.getClient(ServletActionContext.getRequest());
			history.setClientIp(c[0]);
			history.setClientInfo(c[2]);
			this.attachService.saveHistory(history);
		} else {
			logger.warn("没有指定ptype、puid参数，不保存文件上传记录");
		}
	}

	private static final int BUFFER = 4096;
	public String from;// 指定原始文件的类型，默认为文件扩展名
	public String to;// 预览时转换到的文件类型，默认为pdf
	public String f;// 要下载的文件，相对于Attach.DATA_REAL_PATH下的子路径，前后均不带/
	public String n;// [可选]指定下载为的文件名

	// 支持在线打开文档查看的文件下载
	public String inline() throws Exception {
		Date startTime = new Date();
		// 附件的绝对路径名
		String path = Attach.DATA_REAL_PATH + "/" + this.f;

		// 附件的扩展名
		String extension = StringUtils.getFilenameExtension(path);

		// 下载的文件名
		if (this.n == null || this.n.length() == 0)
			this.n = StringUtils.getFilename(this.f);

		// debug
		if (logger.isDebugEnabled()) {
			logger.debug("path=" + path);
			logger.debug("extension=" + extension);
			logger.debug("n=" + n);
			logger.debug("to=" + to);
		}

		if (isConvertFile(extension)) {
			if (this.from == null || this.from.length() == 0)
				this.from = extension;
			if (this.to == null || this.to.length() == 0) {
				this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf
				// if ("xls".equalsIgnoreCase(this.from)
				// || "xlsx".equalsIgnoreCase(this.from)
				// || "xlsm".equalsIgnoreCase(attach.getFormat())) {
				// this.to = "html";// excel默认转换为html格式（因为转pdf的A4纸张导致大报表换页乱了）
				// } else {
				// this.to = getText("jodconverter.to.extension");//
				// 没有指定就是用系统默认的配置转换为pdf
				// }
			}

			// 转换文档
			this.inputStream = OfficeUtils.convert(this.f, this.to, true);

			// 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
			this.contentType = AttachUtils.getContentType(this.to);
			this.contentLength = this.inputStream.available();
			this.filename = WebUtils.encodeFileName(ServletActionContext
					.getRequest(), this.n == null ? "bc" : this.n + "."
					+ this.to);

			if (logger.isDebugEnabled()) {
				logger.debug("convert:" + DateUtils.getWasteTime(startTime));
			}
		} else {
			// 设置下载文件的参数
			if (ct != null && ct.length() > 0) {
				this.contentType = this.ct;
			} else {
				this.contentType = AttachUtils.getContentType(extension);
			}
			this.filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), this.n);

			// 无需转换的文档直接下载处理
			File file = new File(path);
			this.contentLength = file.length();
			this.inputStream = new FileInputStream(file);
		}

		// 创建文件上传日志
		saveAttachHistory(AttachHistory.TYPE_INLINE, this.f, extension);

		return SUCCESS;
	}

	// 判断指定的扩展名是否为配置的要转换的文件类型
	private boolean isConvertFile(String extension) {
		String[] extensions = getText("jodconverter.from.extensions")
				.split(",");
		for (String ext : extensions) {
			if (ext.equalsIgnoreCase(extension))
				return true;
		}
		return false;
	}
}
