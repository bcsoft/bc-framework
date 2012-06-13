/**
 * 
 */
package cn.bc.template.domain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.FileCopyUtils;

import cn.bc.BCConstants;
import cn.bc.core.util.TemplateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.identity.domain.FileEntityImpl;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.XlsUtils;
import cn.bc.template.util.XlsxUtils;

/**
 * 模板
 * 
 * @author lbj
 */
@Entity
@Table(name = "BC_TEMPLATE")
public class Template extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(Template.class);

	/** 模板存储的子路径，开头末尾不要带"/" */
	public static String DATA_SUB_PATH = "template";

	private String orderNo;// 排序号
	private String code;// 编码
	private String path;// 物理文件保存的相对路径（相对于全局配置的app.data.realPath或app.data.subPath目录下的子路径，如"2011/bulletin/xxxx.doc"）
	private String subject;// 标题
	private String content;// 模板内容：文本和Html类型显示模板内容
	private boolean inner;// 内置：是、否，默认否
	private String desc;// 备注
	private int status;// 状态：0-正常,1-禁用
	private String version;// 版本号
	private String category;// 所属分类
	private TemplateType templateType;
	private Long size;// 文件的大小(单位为字节) 默认0
	private boolean formatted;// 格式化：模板是否允许格式化 默认否

	@Column(name = "SIZE_")
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Column(name = "FORMATTED")
	public boolean isFormatted() {
		return formatted;
	}

	public void setFormatted(boolean formatted) {
		this.formatted = formatted;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "TYPE_ID", referencedColumnName = "ID")
	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "VERSION_")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getContent() {
		return content;
	}

	/**
	 * 获取模板的文本字符串
	 * <p>
	 * 如果附件不是纯文本类型返回null,如果是自定义文本内容直接返回配置的内容,如果是纯文本附件返回附件的内容
	 * </p>
	 * 
	 * @return
	 */
	@Transient
	public String getContentEx() {
		return getContentEx(null);
	}

	/**
	 * 获取模板的文本字符串
	 * <p>
	 * 如果附件不是纯文本类型返回null,如果是自定义文本内容直接返回配置的内容,如果是纯文本附件返回附件的内容
	 * </p>
	 * 
	 * @param args
	 *            格式化参数，为空代表不执行格式化
	 * @return
	 */
	public String getContentEx(Map<String, Object> args) {
		// 不处理非纯文本类型
		if (!this.isPureText())
			return null;

		String txt = null;
		// 自定义文本
		if (this.getTemplateType().getCode().equals("custom")) {
			txt = this.content;
		} else {
			// 读取文件流的字符串内容
			String p = Attach.DATA_REAL_PATH + "/" + DATA_SUB_PATH + "/"
					+ this.getPath();
			File file = new File(p);
			try {
				txt = FileCopyUtils.copyToString(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
			} catch (FileNotFoundException e) {
				logger.warn("getContent 附件文件不存在:file=" + p);
			} catch (IOException e) {
				logger.warn("getContent 读取模板文件错误:file=" + p + ",error="
						+ e.getMessage());
			}
		}

		if (txt == null || args == null)
			return txt;

		// 格式化处理
		return TemplateUtils.format(txt, args);
	}

	/**
	 * 获取模板的附件流
	 * <p>
	 * 如果是自定义文本内容返回由此内容构成的内存流,如果是附件类型返回附件流
	 * </p>
	 * 
	 * @return
	 */
	@Transient
	public InputStream getInputStream() {
		// 自定义文本,返回由此内容构成的字节流
		if (this.getTemplateType().getCode().equals("custom")) {
			if (this.content == null)
				return null;
			return new ByteArrayInputStream(this.content.getBytes());
		}

		// 读取文件流并返回
		String p = Attach.DATA_REAL_PATH + "/" + DATA_SUB_PATH + "/"
				+ this.getPath();
		File file = new File(p);
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.warn("getInputStream 附件文件不存在:file=" + p);
			return null;
		}
	}

	/**
	 * 获取模板的附件长度
	 * <p>
	 * 如果是自定义文本内容返回此内容字节的长度,如果是附件类型返回附件长度
	 * </p>
	 * 
	 * @return
	 */
	@Transient
	public long getSizeEx() {
		// 自定义文本
		if (this.getTemplateType().getCode().equals("custom")) {
			if (this.content == null)
				return 0;
			return this.content.getBytes().length;
		}
		//
		String p = Attach.DATA_REAL_PATH + "/" + DATA_SUB_PATH + "/"
				+ this.getPath();
		File file = new File(p);
		return file.length();
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 判断是否是纯文本型模板
	 * 
	 * @return
	 */
	@Transient
	public boolean isPureText() {
		return this.templateType.isPureText();
	}

	/**
	 * 用指定的参数格式化此模板，并将结果保存为附件
	 * 
	 * @param params
	 * @param ptype
	 * @param puid
	 * @return
	 * @throws IOException
	 */
	public Attach format2Attach(Map<String, Object> params, String ptype,
			String puid) throws IOException {
		Attach attach = new Attach();
		attach.setAuthor(SystemContextHolder.get().getUserHistory());
		attach.setFileDate(Calendar.getInstance());
		attach.setSubject(this.getSubject());
		attach.setAppPath(false);
		attach.setPtype(ptype);
		attach.setPuid(puid);
		String extension;
		if ("custom".equals(this.getTemplateType().getCode()))
			extension = "txt";
		else
			extension = this.getPath().substring(
					this.getPath().lastIndexOf(".") + 1);
		attach.setFormat(extension);
		attach.setStatus(BCConstants.STATUS_ENABLED);

		// 文件存储的相对路径（年月），避免超出目录内文件数的限制
		Calendar now = Calendar.getInstance();
		String datedir = new SimpleDateFormat("yyyyMM").format(now.getTime());

		// 要保存的物理文件
		String realpath;// 绝对路径名
		String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(now
				.getTime()) + "." + extension;// 不含路径的文件名
		realpath = Attach.DATA_REAL_PATH + "/" + datedir + "/" + fileName;

		// 构建文件要保存到的目录
		File file = new File(realpath);
		if (!file.getParentFile().exists()) {
			if (logger.isInfoEnabled()) {
				logger.info("mkdir=" + file.getParentFile().getAbsolutePath());
			}
			file.getParentFile().mkdirs();
		}

		logger.info("realpath=" + realpath);
		if (this.isFormatted()) {
			// 格式化并保存到文件
			if ("docx".equalsIgnoreCase(this.getTemplateType().getExtension())) {// Word2007+
				XWPFDocument docx = DocxUtils.format(this.getInputStream(),
						params);
				FileOutputStream out = new FileOutputStream(realpath);
				docx.write(out);
				out.close();
			} else if ("xls".equalsIgnoreCase(this.getTemplateType()
					.getExtension())) {// Excel97-2003
				HSSFWorkbook xls = XlsUtils.format(this.getInputStream(),
						params);
				FileOutputStream out = new FileOutputStream(realpath);
				xls.write(out);
				out.close();
			} else if ("xlsx".equalsIgnoreCase(this.getTemplateType()
					.getExtension())) {// Excel2007+
				XSSFWorkbook xlsx = XlsxUtils.format(this.getInputStream(),
						params);
				FileOutputStream out = new FileOutputStream(realpath);
				xlsx.write(out);
				out.close();
			} else if (this.getTemplateType().isPureText()) {// 纯文本
				String s = this.getContentEx(params);
				FileOutputStream out = new FileOutputStream(realpath);
				out.write(s.getBytes());
				out.close();
			} else {// 其它类型直接复制附件
				if (logger.isInfoEnabled())
					logger.info("pure copy file");
				FileCopyUtils.copy(this.getInputStream(), new FileOutputStream(
						realpath));
			}
		} else {
			// 直接复制附件
			if (logger.isInfoEnabled())
				logger.info("pure copy file");
			FileCopyUtils.copy(this.getInputStream(), new FileOutputStream(
					realpath));
		}

		// 设置附件大小
		attach.setSize(new File(realpath).length());

		// 设置附件相对路径
		attach.setPath(datedir + "/" + fileName);

		return attach;
	}
}
