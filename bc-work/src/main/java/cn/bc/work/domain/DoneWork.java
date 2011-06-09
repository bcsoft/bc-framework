/**
 * 
 */
package cn.bc.work.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 已办事项
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_WORK_DONE")
public class DoneWork extends Base {
	private static final long serialVersionUID = 1L;
	private Calendar finishDate;// 完成时间
	private int finishYear;// 完成时间的年度
	private int finishMonth;// 完成时间的月份(1-12)
	private int finishDay;// 完成时间的日(1-31)

	@Column(name = "FINISH_DATE")
	public Calendar getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Calendar finishDate) {
		this.finishDate = finishDate;
	}

	@Column(name = "FINISH_YEAR")
	public int getFinishYear() {
		return finishYear;
	}

	public void setFinishYear(int finishYear) {
		this.finishYear = finishYear;
	}

	@Column(name = "FINISH_MONTH")
	public int getFinishMonth() {
		return finishMonth;
	}

	public void setFinishMonth(int finishMonth) {
		this.finishMonth = finishMonth;
	}

	@Column(name = "FINISH_DAY")
	public int getFinishDay() {
		return finishDay;
	}

	public void setFinishDay(int finishDay) {
		this.finishDay = finishDay;
	}
}
