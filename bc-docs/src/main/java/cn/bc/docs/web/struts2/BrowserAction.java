/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.Context;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.web.SystemContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 图片剪切处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BrowserAction extends ActionSupport implements SessionAware {
	//private static Log logger = LogFactory.getLog(BrowserAction.class);
	private static final long serialVersionUID = 1L;
	private AttachService attachService;
	protected Map<String, Object> session;
	public List<Attach> browsers4html5;
	public List<Attach> browsers4old;

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public SystemContext getContext() {
		return (SystemContext) this.session.get(Context.KEY);
	}

	@Override
	public String execute() throws Exception {
		// 获取支持html5的浏览器列表
		this.browsers4html5 = this.attachService.findByPtype("browser4html5");

		// 获取不支持html5的浏览器列表
		this.browsers4old = this.attachService.findByPtype("browser4old");

		return super.execute();
	}
}
