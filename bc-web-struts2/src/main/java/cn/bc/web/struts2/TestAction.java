/**
 * 
 */
package cn.bc.web.struts2;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import cn.bc.web.util.WebUtils;

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

	public String url;

	// 显示指定的jsp页面
	public String redirect() {
		Assert.hasText(url);
		logger.warn("url=" + url);
		return SUCCESS;
	}

	public String tpl;

	// 显示指定的jsp页面
	public String show() {
		Assert.hasText(tpl);
		logger.warn("tpl=" + tpl);
		return SUCCESS;
	}

	// 显示指定的freemarker页面
	public String showfm() {
		Assert.hasText(tpl);
		logger.warn("tpl=" + tpl);
		return SUCCESS;
	}

	public String html;
	public boolean trace = false;

	// 显示请求的信息
	public String showHeader() {
		HttpServletRequest r = ServletActionContext.getRequest();

		// -- headers
		html = "<fieldSet class='ui-corner-all ui-widget-content'><legend>headers</legend>";
		html += "<table>";
		@SuppressWarnings("rawtypes")
		Enumeration e = r.getHeaderNames();
		Object name;
		while (e.hasMoreElements()) {
			name = e.nextElement();
			html += "\r\n<tr><td style='width:200px;text-align: right;' data-type='"
					+ name.getClass() + "'>" + name.toString() + ": </td>";
			html += "\r\n<td style='margin-left:;'>"
					+ r.getHeader(name.toString()) + "</td>";
			html += "\r\n</tr>";
		}

		html += "\r\n</table>";
		html += "\r\n</fieldSet>";

		// -- properties
		html += "\r\n<fieldSet class='ui-corner-all ui-widget-content'><legend>properties</legend>";
		html += "\r\n<table>";
		html += oneMethodString("getLocale()", r.getLocale());
		html += oneMethodString("getProtocol()", r.getProtocol());
		html += oneMethodString("isSecure()", r.isSecure());
		html += oneMethodString("getMethod()", r.getMethod());
		html += oneMethodString("getScheme()", r.getScheme());
		html += oneMethodString("getServerName()", r.getServerName());
		html += oneMethodString("getServerPort()", r.getServerPort());
		html += oneMethodString("getRemoteAddr()", r.getRemoteAddr());
		html += oneMethodString("getRemoteHost()", r.getRemoteHost());
		html += oneMethodString("getRemotePort()", r.getRemotePort());
		html += oneMethodString("getLocalAddr()", r.getLocalAddr());
		html += oneMethodString("getLocalName()", r.getLocalName());
		html += oneMethodString("getLocalPort()", r.getLocalPort());
		html += oneMethodString("getCharacterEncoding()",
				r.getCharacterEncoding());
		html += oneMethodString("getContentType()", r.getContentType());
		html += oneMethodString("getContentLength()", r.getContentLength());
		html += oneMethodString("getRequestURL()", r.getRequestURL());
		html += oneMethodString("getRequestURI()", r.getRequestURI());
		html += oneMethodString("getContextPath()", r.getContextPath());
		html += oneMethodString("getQueryString()", r.getQueryString());
		html += oneMethodString("getPathInfo()", r.getPathInfo());
		html += oneMethodString("getPathTranslated()", r.getPathTranslated());
		html += oneMethodString("getServletPath()", r.getServletPath());
		html += oneMethodString("getRemoteUser()", r.getRemoteUser());
		html += oneMethodString("getUserPrincipal()", r.getUserPrincipal());
		html += oneMethodString("getAuthType()", r.getAuthType());

		html += "\r\n</table>";
		html += "</fieldSet>";

		// -- trace
		html += "\r\n<fieldSet class='ui-corner-all ui-widget-content'><legend>trace=" + trace + "</legend>";
		html += "\r\n<table>";
		String remoteAddress = getRemoteAddress(r);
		html += oneMethodString("remoteAddress", remoteAddress);
		if (trace)
			try {
				logger.warn("start trace mac...");
				html += oneMethodString("remoteMAC", WebUtils.getMac(remoteAddress));
				logger.warn("end trace mac.");
			} catch (Exception e1) {
				logger.error(e1.getMessage(),e1);
				
				html += e1.getMessage();
			}

		html += "\r\n</table>";
		html += "</fieldSet>";

		logger.info("html=" + html);
		return "page";
	}

	private String oneMethodString(String key, Object value) {
		String html = "\r\n<tr><td style='width:200px;text-align: right;'>"
				+ key + ": </td>";
		html += "\r\n<td style='margin-left:;'>" + value + "</td>";
		html += "\r\n</tr>";
		return html;
	}

	private String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
			ip = request.getRemoteAddr();
		return ip;
	}
}