/**
 * 
 */
package cn.bc.docs.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.bc.identity.domain.FileEntity;

/**
 * 附件
 * <p>
 * 记录文档与其相关附件之间的关系
 * </p>
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_DOCS_ATTACH")
public class Attach extends FileEntity {
	private static final long serialVersionUID = 1L;

	private String puid;// 所关联文档的UID
	private String ptype;// 所关联文档的分类
	private String extend;// 附件扩展名：如png、doc、mp3等
	private String path;// 物理文件保存的相对路径（相对于全局配置的附件根目录下的子路径，如"2011/bulletin/xxxx.doc"）
	private long size;// 文件的大小(单位为byte)
	private boolean appPath = false;//指定path的值是相对于应用部署目录下路径还是相对于全局配置的app.data目录下的路径
	private static NumberFormat format = new DecimalFormat("#.#");

	/**
	 * 将数值转化为友好的显示字符串
	 * 
	 * @return
	 */
	@Transient
	public String getSizeInfo() {
		if (size < 1024)// 字节
			return size + "Bytes";
		else if (size < 1024 * 1024)// KB
			return format.format(size) + "KB";
		else
			// MB
			return format.format(size) + "MB";
	}

	public boolean isAppPath() {
		return appPath;
	}

	public void setAppPath(boolean appPath) {
		this.appPath = appPath;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String euid) {
		this.puid = euid;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}