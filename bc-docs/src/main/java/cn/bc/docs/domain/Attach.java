/**
 * 
 */
package cn.bc.docs.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
	private String extension;// 附件扩展名：如png、doc、mp3等
	private String path;// 物理文件保存的相对路径（相对于全局配置的附件根目录下的子路径，如"2011/bulletin/xxxx.doc"）
	private long size;// 文件的大小(单位为byte)
	private long count;// 文件的下载次数
	private boolean appPath = false;// path的值是相对于应用部署目录下路径还是相对于全局配置的app.data目录下的路径

	@Column(name = "COUNT_")
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
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

	@Column(name = "EXT")
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}