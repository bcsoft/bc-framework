/**
 * 
 */
package cn.bc.netdisk.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	// private static Log logger = LogFactory.getLog(NetdiskShare.class);
	private String role;// 访问权限 :wrfd:0110-->表示无编辑有查看有评论无下载权限
						// 用4为数字表示(wrfd:w-编辑,r-查看,f-评论,d-下载),每位数的值为0或1,1代表拥有此权限'
	private Long aid;// 访问人id
	private int orderNo;// 排序号
	private NetdiskFile netdiskFile;// 网络硬盘文件

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public NetdiskFile getNetdiskFile() {
		return netdiskFile;
	}

	public void setNetdiskFile(NetdiskFile netdiskFile) {
		this.netdiskFile = netdiskFile;
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

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

}
