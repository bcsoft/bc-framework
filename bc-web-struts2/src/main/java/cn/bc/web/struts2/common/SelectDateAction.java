/**
 *
 */
package cn.bc.web.struts2.common;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

/**
 * 选择日期的Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectDateAction extends ActionSupport {
  private static final long serialVersionUID = 1L;

  public String title = getText("title.selectDate");// 对话框标题
  private Calendar curDate;// 日期 yyyy-MM-dd
  public boolean time;// 是否使用时间

  public Calendar getCurDate() {
    return curDate;
  }

  public void setCurDate(Calendar startDate) {
    this.curDate = startDate;
  }

  @Override
  public String execute() throws Exception {
    // 默认为当前时间
    if (curDate == null)
      curDate = Calendar.getInstance();
    return SUCCESS;
  }
}