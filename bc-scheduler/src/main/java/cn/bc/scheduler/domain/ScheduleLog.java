package cn.bc.scheduler.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 任务运行日志
 * 
 * @author dragon
 * @since 2011-08-29
 */
@Entity
@Table(name = "BC_SD_LOG")
public class ScheduleLog extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Calendar startDate; // 任务的启动时间
	private Calendar endDate; // 任务的结束时间
	private boolean success = false; // 任务是否处理成功
	private String errorType; // 异常分类
	private String msg; // 任务信息：如果任务运行异常则为异常信息
	private String cfgName;// 对应JobCfg的name
	private String cfgGroup;// 对应JobCfg的groupn
	private String cfgCron;// 对应TriggerCfg的cron
	private String cfgBean;// 对应JobCfg的bean
	private String cfgMethod;// 对应JobCfg的method

	@Column(name = "ERROR_TYPE")
	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	@Column(name = "START_DATE")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Column(name = "CFG_NAME")
	public String getCfgName() {
		return cfgName;
	}

	public void setCfgName(String cfg_name) {
		this.cfgName = cfg_name;
	}

	@Column(name = "CFG_GROUP")
	public String getCfgGroup() {
		return cfgGroup;
	}

	public void setCfgGroup(String cfgGroup) {
		this.cfgGroup = cfgGroup;
	}

	@Column(name = "CFG_CRON")
	public String getCfgCron() {
		return cfgCron;
	}

	public void setCfgCron(String cfg_cron) {
		this.cfgCron = cfg_cron;
	}

	@Column(name = "CFG_BEAN")
	public String getCfgBean() {
		return cfgBean;
	}

	public void setCfgBean(String cfg_bean) {
		this.cfgBean = cfg_bean;
	}

	@Column(name = "CFG_METHOD")
	public String getCfgMethod() {
		return cfgMethod;
	}

	public void setCfgMethod(String cfg_method) {
		this.cfgMethod = cfg_method;
	}

	public String toString() {
		return this.getMsg();
	}
}
