package cn.bc.form.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.ActorHistory;

/**
 * 表单字段更新日志
 * 
 * @author dragon & hwx
 * 
 */
@Entity
@Table(name = "BC_FORM_FIELD_LOG")
public class FieldLog extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Field field;// 所属字段
	private String value;// 值
	private Calendar updateTime;// 更新时间
	private ActorHistory updator;// 更新人
	private String BatchNo;

	@JoinColumn(name = "PID", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	@Column(name = "VALUE_")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "UPDATE_TIME")
	public Calendar getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Calendar updateTime) {
		this.updateTime = updateTime;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "UPDATOR", referencedColumnName = "ID")
	public ActorHistory getUpdator() {
		return updator;
	}

	public void setUpdator(ActorHistory updator) {
		this.updator = updator;
	}
	
	@Column(name = "BATCH_NO")
	public String getBatchNo() {
		return BatchNo;
	}

	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}

	@Override
	public String toString() {
		return this.getValue();
	}
}