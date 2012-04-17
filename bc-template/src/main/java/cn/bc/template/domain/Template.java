/**
 * 
 */
package cn.bc.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
	public static final int TYPE_FILE_EXCEL=1;
	/**
	 * Word文件
	 */
	public static final int TYPE_FILE_WORD=2;
	/**
	 * 纯文本文件
	 */
	public static final int TYPE_FILE_TEXT=3;
	/**
	 * 其它附件
	 */
	public static final int TYPE_FILE_OTHER=4;
	/**
	 * 自定义文本
	 */
	public static final int TYPE_CUSTOM_TEXT=5;
	
	/**
	 * 内置：是
	 */
	public static final boolean INNER_TURE=true;
	/**
	 * 内置：否
	 */
	public static final boolean INNER_FALSE=false;
	
	private String order;//排序号
	private int type;//类型：1-Excel模板、2-Word模板、3-纯文本模板、4-其它附件、5-自定义文本
	private String code;//编码：全局唯一
	private String name;//模板名称
	private String templateFileName;//模板文件：系统定义的文件名
	private String userFileName;//用户文件：用户定义的文件名
	private String content;//模板内容：文本和Html类型显示模板内容
	private boolean inner;//内置：0-是、1-否，默认否
	
	
	@Column(name="ORDER_")
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Column(name="TYPE_")
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="TEMPLATE_FILE_NAME")
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

	@Column(name="USER_FILE_NAME")
	public String getUserFileName() {
		return userFileName;
	}

	public void setUserFileName(String userFileName) {
		this.userFileName = userFileName;
	}

	@Column(name="INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}

	
	

	
	
	
	

	
}
