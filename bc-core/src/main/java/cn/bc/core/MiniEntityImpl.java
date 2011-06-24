/**
 * 
 */
package cn.bc.core;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 默认的迷你实体接口实现
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class MiniEntityImpl implements cn.bc.core.MiniEntity<Long> {
	private static final long serialVersionUID = 7826313222480961654L;
	private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public boolean isNew() {
		return getId() == null || getId() <= 0;
	}
}
