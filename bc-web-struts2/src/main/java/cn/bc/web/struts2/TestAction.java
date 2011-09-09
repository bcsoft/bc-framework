/**
 * 
 */
package cn.bc.web.struts2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 测试页面用的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TestAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(getClass());

	// 选择日期的测试页面
	public String datepicker() {
		return SUCCESS;
	}
}