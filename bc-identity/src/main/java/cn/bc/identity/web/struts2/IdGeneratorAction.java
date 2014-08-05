/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.identity.service.IdGeneratorService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

/**
 * 认证信息Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class IdGeneratorAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public String type;
	public String html;
	@Autowired
	private IdGeneratorService idGeneratorService;

	// 重置密码
	public String nextUid() throws Exception {
		Assert.hasText(this.type, "type couldn't be empty.");
		html = idGeneratorService.next(this.type);
		return "page";
	}
}
