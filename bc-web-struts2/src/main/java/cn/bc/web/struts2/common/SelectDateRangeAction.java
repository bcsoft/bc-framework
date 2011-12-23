/**
 * 
 */
package cn.bc.web.struts2.common;

import java.util.Calendar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择日期范围的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectDateRangeAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	public String title = getText("title.selectDateRange");// 对话框标题
	private Calendar startDate;// 开始日期 yyyy-MM-dd
	private Calendar endDate;// 结束日期 yyyy-MM-dd

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
}