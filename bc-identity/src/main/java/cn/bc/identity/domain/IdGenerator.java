/**
 * 
 */
package cn.bc.identity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 标识生成器,用于生成主键或唯一编码用
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_IDENTITY_IDGENERATOR")
public class IdGenerator {
	@Id
	@Column(name = "TYPE_")
	private String type;// 分类

	@Column(name = "VALUE")
	private Long value;// 当前值
	/**
	 * 格式模板 如"case-${V}"、"${T}-${V}" 1) ${V}代表value的值 2) ${T}代表type的值 3)
	 * ${DATE}代表执行的日期，格式为yyyyMMdd 4) ${TIME}代表执行的时间，格式为HHmmss 5)
	 * ${S}代表执行的毫秒数，格式为SSS
	 */
	@Column(name = "FORMAT")
	private String format;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
