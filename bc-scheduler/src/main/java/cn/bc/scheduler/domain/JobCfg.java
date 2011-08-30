package cn.bc.scheduler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.json.JSONObject;

import cn.bc.core.EntityImpl;

/**
 * 任务配置
 * 
 * @author dragon
 * @since 2011-08-29
 */
@Entity
@Table(name = "BC_SD_CFG_JOB")
public class JobCfg extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private String name; // 任务名称
	private String bean; // 要调用的SpringBean名
	private String method; // 要调用的SpringBean方法名
	private String description; // 备注
	private boolean stopInError = false; // 发现异常是否终止继续调度

	@Column(name = "SIE")
	public boolean isStopInError() {
		return stopInError;
	}

	public void setStopInError(boolean stopInError) {
		this.stopInError = stopInError;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "BEAN")
	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	@Column(name = "METHOD")
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return new JSONObject(this).toString();
	}
}