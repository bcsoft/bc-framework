/**
 * 
 */
package cn.bc.identity.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cn.bc.core.EntityImpl;

/**
 * 文档实体基类：包含创建人信息和最后修改人信息
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class FileEntityImpl extends EntityImpl implements
		FileEntity<Long> {
	private static final long serialVersionUID = 1L;
	private Calendar fileDate;// 创建时间
	private ActorHistory author;// 创建人
	private Calendar modifiedDate;// 最后修改时间
	private ActorHistory modifier;// 最后修改人

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
	public ActorHistory getAuthor() {
		return author;
	}

	public void setAuthor(ActorHistory author) {
		this.author = author;
	}

	@Column(name = "MODIFIED_DATE")
	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "MODIFIER_ID", referencedColumnName = "ID")
	public ActorHistory getModifier() {
		return modifier;
	}

	public void setModifier(ActorHistory modifier) {
		this.modifier = modifier;
	}
}
