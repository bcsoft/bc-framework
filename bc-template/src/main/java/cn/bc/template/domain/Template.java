/**
 * 
 */
package cn.bc.template.domain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import cn.bc.docs.domain.Attach;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 
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

	/**
	 * Excel文件
	 */
	public static final int TYPE_EXCEL = 1;
	/**
	 * Word文件
	 */
	public static final int TYPE_WORD = 2;
	/**
	 * 纯文本文件
	 */
	public static final int TYPE_TEXT = 3;
	/**
	 * 其它附件
	 */
	public static final int TYPE_OTHER = 4;
	/**
	 * 自定义文本
	 */
	public static final int TYPE_CUSTOM = 5;
	private String order;// 排序号
	private int type;// 类型：1-Excel模板、2-Word模板、3-纯文本模板、4-其它附件、5-自定义文本
	private String code;// 编码：全局唯一
	private String path;// 物理文件保存的相对路径（相对于全局配置的app.data.realPath或app.data.subPath目录下的子路径，如"2011/bulletin/xxxx.doc"）
	private String subject;// 标题
	private String content;// 模板内容：文本和Html类型显示模板内容
	private boolean inner;// 内置：是、否，默认否
	private String desc;// 备注

	@Column(name = "ORDER_")
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	/**
	 * 获取模板的文本字符串
	 * <p>
	 * 如果附件不是纯文本类型返回null,如果是自定义文本内容直接返回配置的内容,如果是纯文本附件返回附件的内容
	 * </p>
	 * 
	 * @return
	 */
	public String getContent() {
		// 自定义文本
		if (this.getType() == TYPE_CUSTOM)
			return this.content;

		// 不处理非纯文本类型
		if (!this.isPureText())
			return null;

		// 读取文件流的字符串内容并返回
		String p = Attach.DATA_REAL_PATH + "/" + DATA_SUB_PATH + "/"
				+ this.getPath();
		File file = new File(p);
		try {
			return FileCopyUtils.copyToString(new FileReader(file));
		} catch (FileNotFoundException e) {
			logger.warn("getContent 附件文件不存在:file=" + p);
			return null;
		} catch (IOException e) {
			logger.warn("getContent 读取模板文件错误:file=" + p + ",error=" + e.getMessage());
			return null;
		}
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
		if (this.getType() == TYPE_CUSTOM) {
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
		return type == Template.TYPE_CUSTOM
				|| (this.getPath() != null && (this.getPath().endsWith(".xml")
						|| this.getPath().endsWith(".txt")
						|| this.getPath().endsWith(".cvs") || this.getPath()
						.endsWith(".log")));
	}
}
