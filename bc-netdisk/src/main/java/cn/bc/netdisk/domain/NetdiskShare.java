/**
 * 
 */
package cn.bc.netdisk.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 网络文件访问权限
 * 
 * @author zxr
 */
@Entity
@Table(name = "BC_NETDISK_SHARE")
public class NetdiskShare extends EntityImpl {
	private static final long serialVersionUID = 1L;
	//private static Log logger = LogFactory.getLog(NetdiskShare.class);
	private Long pid;// 文件ID
	private String role;// 访问权限 :
						// 用4为数字表示(wrfd:w-编辑,r-查看,f-评论,d-下载),每位数的值为0或1,1代表拥有此权限'
	private Long aid;// 模板类型名称

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "ROLE_")
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getAid() {
		return aid;
	}

	public void setAid(Long aid) {
		this.aid = aid;
	}

}
