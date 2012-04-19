/**
 * 
 */
package cn.bc.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	
	/** 模板存储的子路径，开头带"/"，末尾不要带"/" */
	public static String DATA_SUB_PATH = "/template";

	/**
	 * Excel文件
	 */
	public static final int TYPE_EXCEL=1;
	/**
	 * Word文件
	 */
	public static final int TYPE_WORD=2;
	/**
	 * 纯文本文件
	 */
	public static final int TYPE_TEXT=3;
	/**
	 * 其它附件
	 */
	public static final int TYPE_OTHER=4;
	/**
	 * 自定义文本
	 */
	public static final int TYPE_CUSTOM=5;
	private String order;//排序号
	private int type;//类型：1-Excel模板、2-Word模板、3-纯文本模板、4-其它附件、5-自定义文本
	private String code;//编码：全局唯一
	private String path;// 物理文件保存的相对路径（相对于全局配置的app.data.realPath或app.data.subPath目录下的子路径，如"2011/bulletin/xxxx.doc"）
	private String subject;//标题
	private String content;//模板内容：文本和Html类型显示模板内容
	private boolean inner;//内置：是、否，默认否
	private String desc;//备注
	
	@Column(name="ORDER_")
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

	public String getContent() {
		return content;
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

	@Column(name="INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}
	
	@Column(name="DESC_")
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
		int type = getType();
		if (type == Template.TYPE_TEXT || (type == Template.TYPE_WORD)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是否是自定义文本
	 * 
	 * @return
	 */
	@Transient
	public boolean isCustomText() {
		int type = getType();
		if (type == Template.TYPE_CUSTOM) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是否是文件
	 * 
	 * @return
	 */
	@Transient
	public boolean isFile() {
		int type = getType();
		if (type == Template.TYPE_TEXT || (type == Template.TYPE_WORD)
				|| (type == Template.TYPE_EXCEL)
				|| (type == Template.TYPE_OTHER)) {
			return true;
		} else {
			return false;
		}
	}
}
