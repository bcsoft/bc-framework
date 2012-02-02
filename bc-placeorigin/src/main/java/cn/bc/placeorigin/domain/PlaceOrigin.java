/**
 * 
 */
package cn.bc.placeorigin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 籍贯
 * 
 * @author lbj
 */
@Entity
@Table(name = "BC_PLACEORIGIN")
public class PlaceOrigin extends RichFileEntityImpl {
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
		
	private Long core;//统计用区划代码和城乡划分代码
	private int type;//类型(0-国家,1-省级,2-地级,3-县级,4-乡级,5-村级)
	private String name;//名称 例如：荔湾区
	private Long pid;//所隶属的上级地方ID
	private String fullname;//全名 例如：广东省广州市荔湾区
	
	public Long getCore() {
		return core;
	}
	public void setCore(Long core) {
		this.core = core;
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
	
	
}
