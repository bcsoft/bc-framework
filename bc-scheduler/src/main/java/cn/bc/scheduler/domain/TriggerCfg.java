package cn.bc.scheduler.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.json.JSONObject;

import cn.bc.core.EntityImpl;

/**
 * 触发器配置
 * 
 * @author dragon
 * @since 2011-08-29
 */
@Entity
@Table(name = "BC_SD_CFG_TRIGGER")
public class TriggerCfg extends EntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态常数：禁用 */
	public static final int STATUS_DISABLED = 0;
	/** 状态常数：启用 */
	public static final int STATUS_ENABLED = 1;

	private int status; // 状态：参考以STATUS_开头的常数
	private String name; // 名称
	private JobCfg jobCfg; // 要触发的任务配置
	private String cron; // 时间表达式
	private String description; // 备注
	private String orderNo; // 排序号

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "JOB_ID", referencedColumnName = "ID")
	public JobCfg getJobCfg() {
		return jobCfg;
	}

	public void setJobCfg(JobCfg jobConfig) {
		this.jobCfg = jobConfig;
	}

	@Column(name = "CRON")
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
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
