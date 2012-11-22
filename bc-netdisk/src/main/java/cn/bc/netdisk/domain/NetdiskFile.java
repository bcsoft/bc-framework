/**
 * 
 */
package cn.bc.netdisk.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.BCConstants;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 网络文件
 * 
 * @author zxr
 */
@Entity
@Table(name = "BC_NETDISK_FILE")
public class NetdiskFile extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	// private static Log logger = LogFactory.getLog(NetdiskFile.class);
	public static final String ATTACH_TYPE = NetdiskFile.class.getSimpleName();
	/** 模板存储的子路径，开头末尾不要带"/" */
	public static String DATA_SUB_PATH = "netdisk";
	/** 类型：文件 */
	public static final int TYPE_FILE = 1;
	/** 类型：文件夹 */
	public static final int TYPE_FOLDER = 0;

	/** 文件夹类型：个人 */
	public static final int FOLDER_TYPE_PERSONAL = 0;
	/** 文件夹类型：公共 */
	public static final int FOLDER_TYPE_PUBLIC = 1;

	/** 编辑权限：编辑者可修改 */
	public static final int ROLE_REVISABILITY = 0;
	/** 编辑权限：只有拥有者可修改 */
	public static final int ROLE_UNCHANGEABLE = 1;

	private int status = BCConstants.STATUS_ENABLED;
	private Long pid;// 所在文件夹ID
	private int type;// 类型 : 0-文件夹,1-文件
	private int folderType;// 文件夹类型 : 0-个人,1-公共
	private String name;// 名称 : (不带路径的部分)
	private Long size;// 大小 : 字节单位,文件大小或文件夹的总大小
	private String ext;// 扩展名 : 仅适用于文件类型
	private String orderNo;// 排序号
	private String path;// 保存路径 : 相对于[NETDISK]目录下的子路径,开头不要带符号/,仅适用于文件类型'
	private int editRole;// 编辑权限 : 0-编辑者可修改,1-只有拥有者可修改
	private String batchNo;// 批号:标识是否是上传文件夹时到一批上传的文件
	private Set<NetdiskShare> fileVisitors;// 遗失证照

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "FOLDER_TYPE")
	public int getFolderType() {
		return folderType;
	}

	public void setFolderType(int folderType) {
		this.folderType = folderType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "SIZE_")
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "EDIT_ROLE")
	public int getEditRole() {
		return editRole;
	}

	public void setEditRole(int editRole) {
		this.editRole = editRole;
	}

	@Column(name = "BATCH_NO")
	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	@OneToMany(mappedBy = "netdiskFile", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "orderNo asc")
	public Set<NetdiskShare> getFileVisitors() {
		return fileVisitors;
	}

	public void setFileVisitors(Set<NetdiskShare> fileVisitors) {
		this.fileVisitors = fileVisitors;
	}

}
