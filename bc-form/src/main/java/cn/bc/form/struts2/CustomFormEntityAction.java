package cn.bc.form.struts2;

import java.util.Map;

import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.form.service.FormService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 自定义表单CRUD入口Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CustomFormEntityAction extends ActionSupport
	implements SessionAware, RequestAware{
	private static final long serialVersionUID = 1L;
	protected Map<String, Object> session;
	protected Map<String, Object> request;
	public Long id; //自定义表单的id
	public String ids; // 批量删除的id，多个id间用逗号连接
	public String html;// 后台生成的html页面
	private FormService formService;
	/**
	 * 模板编码：如果含字符":"，则进行分拆，前面部分为编码，后面部分为版本号，如果没有字符":"，将获取当前状态为正常的版本后格式化
	 * 
	 */
	public String tpl;

	@Autowired
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	// 创建自定义表单
	public String create() throws Exception {
		// 根据模板编码，调用相应的模板处理后输出格式化好的前台表单HTML代码
		html = formService.getFormattedForm(tpl);
		return "page";
	}
	
	// 编辑自定义表单
	public String edit() throws Exception {
		// 根据自定义表单id，获取相应的自定义表单表单对象，根据表单字段参数格式化模板后生成的前台表单HTML代码
		// TODO
		
		return "page";
	}
	
	// 查看自定表单
	public String open() throws Exception {
		
		return "page";
	}
}
