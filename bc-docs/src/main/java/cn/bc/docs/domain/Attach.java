/**
 * 
 */
package cn.bc.docs.domain;

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

	private String euid;// 所关联文档的UID
	private String classify;// 同一文档内附件的分类
	private String extend;// 文件扩展名：如png、doc、mp3等
	private String name;// 文件名称(不带扩展名和路径的部分)
	private String path;// 物理文件保存的相对路径（相对于全局配置的附件根目录下的子路径，如"2011/bulletin/xxxx.doc"）
	private int size;// 文件的大小(单位为byte)

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getEuid() {
		return euid;
	}

	public void setEuid(String euid) {
		this.euid = euid;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}