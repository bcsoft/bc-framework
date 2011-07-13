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
 * 基本实体接口的实现
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class EntityImpl implements cn.bc.core.Entity<Long> {
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
