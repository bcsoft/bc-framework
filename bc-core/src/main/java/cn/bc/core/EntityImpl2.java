/**
 *
 */
package cn.bc.core;

import javax.persistence.*;

/**
 * 基本实体接口的实现
 * (主键使用 IDENTITY 生成策略)
 *
 * @author dragon 2015-09-16
 */
@MappedSuperclass
public abstract class EntityImpl2 implements Entity<Long> {
	private static final long serialVersionUID = 7826313222480961654L;
	private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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