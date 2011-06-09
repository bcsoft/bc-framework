/**
 * 
 */
package cn.bc.security.web;

import java.util.HashMap;
import java.util.Map;

import cn.bc.security.domain.Module;
import cn.bc.web.formater.Formater;

/**
 * 资源类型值的格式化
 * 
 * @author dragon
 * 
 */
public class ModuleTypeFormater implements Formater {
	private Map<String, String> types;

	public ModuleTypeFormater() {
		types = new HashMap<String, String>();
		types.put(String.valueOf(Module.TYPE_FOLDER), "模块");
		types.put(String.valueOf(Module.TYPE_OPERATE), "操作");
		types.put(String.valueOf(Module.TYPE_INNER_LINK), "内部链接");
		types.put(String.valueOf(Module.TYPE_OUTER_LINK), "外部链接");
		//types.put(String.valueOf(Module.TYPE_HTML), "HTML");
	}

	public ModuleTypeFormater(Map<String, String> types) {
		this.types = types;
	}

	public String format(Object value) {
		if (value == null)
			return null;

		String f = types.get(value.toString());
		return f == null ? "undefined" : f;
	}
}
