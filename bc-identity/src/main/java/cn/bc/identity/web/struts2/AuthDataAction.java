/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 认证信息Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AuthDataAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(getClass());
	public String ids;// 要更新密码的用户id，逗号连接多个
	private UserService userService;
	public String password;
	public boolean success;
	public String msg;
	private Map<String, Object> session;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	// 设置密码对话框
	public String setPassword() throws Exception {
		this.password = getText("user.password.default");
		return "form";
	}

	// 重置密码
	public String updatePassword() throws Exception {
		try {
			if (this.ids == null || this.ids.length() == 0) {// 更新当前用户
				this.ids = ((Actor) this.session.get("user")).getId()
						.toString();
				logger.debug("update current user. id=" + this.ids);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("ids=" + this.ids);
				logger.debug("password=" + password);
			}

			// 字符串转换为Long
			String[] __ids = this.ids.split(",");
			Long[] _ids = new Long[__ids.length];
			for (int i = 0; i < __ids.length; i++) {
				_ids[i] = new Long(__ids[i]);
			}

			// 更新密码
			String md5;
			if (this.password.length() != 32) {// 明文密码先进行md5加密
				md5 = DigestUtils.md5DigestAsHex(this.password
						.getBytes("UTF-8"));
			} else {// 已加密的密码
				md5 = this.password;
			}
			int count = this.userService.updatePassword(_ids, md5);
			this.success = true;
			this.msg = getText("user.password.update.success");
			if (logger.isDebugEnabled()) {
				logger.debug("count=" + count);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			this.success = false;
			this.msg = getText("user.password.update.failed");
		}
		return "commonSuccess";
	}
}
