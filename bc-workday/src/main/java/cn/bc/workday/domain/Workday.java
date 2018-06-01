package cn.bc.workday.domain;

import cn.bc.core.EntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 工作日的实体类
 *
 * @author LeeDane
 */
@Entity
@Table(name = "BC_WORKDAY")
public class Workday extends EntityImpl {

  private static final long serialVersionUID = 1L;

  private boolean dayOff; //TRUE: 休息日, FALSE: 工作日
  private Date fromDate;  //开始日期
  private Date toDate; //结束日期
  private String desc_; //备注

  @Column(name = "DAYOFF")
  public boolean isDayOff() {
    return dayOff;
  }

  public void setDayOff(boolean dayOff) {
    this.dayOff = dayOff;
  }

  @Column(name = "FROM_DATE")
  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  @Column(name = "TO_DATE")
  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  @Column(name = "DESC_", length = 1000)
  public String getDesc_() {
    return desc_;
  }

  public void setDesc_(String desc_) {
    this.desc_ = desc_;
  }
}
