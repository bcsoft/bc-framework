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
	public static final int TYPE_EXCEL = 1;
	/**
	 * Word文件
	 */
	public static final int TYPE_WORD = 2;
	/**
	 * Text文件
	 */
	public static final int TYPE_TEXT = 3;
	/**
	 * HTML文件
	 */
	public static final int TYPE_HTML = 4;
	/**
	 * 其它文件
	 */
	public static final int TYPE_OTHER = 5;

	/**
	 * 内置：是
	 */
	public static final int INNER_TURE = 0;
	/**
	 * 内置：否
	 */
	public static final int INNER_FALSE = 1;

	public static final String KEY_CODE_EXCEL = "excel.tpl";
	public static final String KEY_CODE_WORD = "word.tpl";
	public static final String KEY_CODE_TEXT = "text.tpl";
	public static final String KEY_CODE_HTML = "html.tpl";
	public static final String KEY_CODE_OTHER = "other.tpl";

	private String order;// 排序号
	private Integer type;// 类型：1-Excel文件、2-Word文件、3-文本文件、4-Html文件、5-其它文件
	private String code;// 编码：全局唯一
	private String name;// 模板名称
	private String templateFileName;// 模板文件
	private String content;// 模板内容：文本和Html类型显示模板内容
	private Integer inner;// 内置：0-是、1-否，默认否

	@Column(name = "ORDER_")
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Column(name = "TYPE_")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "TEMPLATE_FILE_NAME")
	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "INNER_")
	public Integer getInner() {
		return inner;
	}

	public void setInner(Integer inner) {
		this.inner = inner;
	}

	/**
	 * 判断是否是纯文本型模板
	 * 
	 * @return
	 */
	@Transient
	public boolean isPureText() {
		int type = getType().intValue();
		if (type == Template.TYPE_TEXT || (type == Template.TYPE_WORD)) {
			return true;
		} else {
			return false;
		}
	}
}
