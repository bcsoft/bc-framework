/**
 * 
 */
package cn.bc.log.web.struts2;

import java.util.HashMap;
import java.util.Map;

import cn.bc.log.domain.Syslog;
import cn.bc.web.formater.AbstractFormater;

/**
 * 系统日志分类值的格式化
 * 
 * @author dragon
 * 
 */
public class SyslogTypeFormater extends AbstractFormater<String> {
	private Map<String, String> types;

	public SyslogTypeFormater() {
		types = new HashMap<String, String>();
		types.put(String.valueOf(Syslog.TYPE_LOGIN), "登录");
		types.put(String.valueOf(Syslog.TYPE_LOGOUT), "主动注销");
		types.put(String.valueOf(Syslog.TYPE_LOGIN_TIMEOUT), "超时注销");
		types.put(String.valueOf(Syslog.TYPE_RELOGIN), "重登录");
	}

	public SyslogTypeFormater(Map<String, String> types) {
		this.types = types;
	}

	public String format(Object context, Object value) {
		if (value == null)
			return null;

		String f = types.get(value.toString());
		return f == null ? "undefined" : f;
	}
}
