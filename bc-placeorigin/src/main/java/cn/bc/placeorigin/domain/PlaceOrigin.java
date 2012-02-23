/**
 * 
 */
package cn.bc.placeorigin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.BCConstants;
import cn.bc.identity.domain.FileEntityImpl;


/**
 * 籍贯
 * 
 * @author lbj
 */
@Entity
@Table(name = "BC_PLACEORIGIN")
public class PlaceOrigin extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	
	/**类别：国家 */
	public static final int TYPE_COUNTRY_LEVEL=0;
	/**类别：省级 */
	public static final int TYPE_PROVINCE_LEVEL=1;
	/**类别： 地级*/
	public static final int TYPE_PLACE_LEVEL=2;
	/**类别： 县级*/
	public static final int TYPE_COUNTY_LEVEL=3;
	/**类别：乡级 */
	public static final int TYPE_TOWNSHIP_LEVEL=4;
	/**类别：村级 */
	public static final int TYPE_VILLAGE_LEVEL=5;
	
	private int status= BCConstants.STATUS_ENABLED;
	private String code;//编码 县及县以上行政区划代码
	private String fullcode;//全编码 统计用区划代码和城乡划分代码
	private int type;//类型(0-国家,1-省级,2-地级,3-县级,4-乡级,5-村级)
	private String name;//名称 例如：荔湾区
	private Long pid;//所隶属的上级地方ID
	private String fullname;//全名 例如：广东省广州市荔湾区
	private String desc;//备注
	
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name="TYPE_")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	@Column(name="FULL_NAME")
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Column(name = "FULL_CODE")
	public String getFullcode() {
		return fullcode;
	}
	public void setFullcode(String fullcode) {
		this.fullcode = fullcode;
	}
	
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
