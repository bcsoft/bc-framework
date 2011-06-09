/**
 * 
 */
package cn.bc.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.DefaultEntity;

/**
 * 资源
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_SECURITY_MODULE")
public class Module extends DefaultEntity {
	private static final long serialVersionUID = 4623393538293916992L;
	
	/**资源类型为文件夹*/
	public static final int TYPE_FOLDER = 1;
	/**资源类型为内部链接*/
	public static final int TYPE_INNER_LINK = 2;
	/**资源类型为外部链接*/
	public static final int TYPE_OUTER_LINK = 3;
	/**资源类型为HTML*/
	public static final int TYPE_HTML = 4;
	/**资源类型为按钮操作*/
	public static final int TYPE_OPERATE = 5;

	private String name;//名称
	private String code;//编码
	private int type;//资源类型，TYPE_*定义的相关常数
	private String url;//资源地址
	private Module belong;//所隶属的模块
	private String iconClass;//图标样式
	private String option;//额外配置
	
	public String getIconClass() {
		return iconClass;
	}
	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "OPTION_")
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@ManyToOne(fetch=FetchType.EAGER, optional = true)
	@JoinColumn(name = "BELONG", referencedColumnName = "ID")
	public Module getBelong() {
		return belong;
	}
	public void setBelong(Module belong) {
		this.belong = belong;
	}
	
	public String toString() {
		return "{id:" + getId() + ",type:" + type + ",code:" + code+ ",name:" + name + ",url:" + url + "}";
	}
}
