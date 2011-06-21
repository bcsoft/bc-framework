/**
 * 
 */
package cn.bc.identity.web;

import java.util.HashMap;
import java.util.Map;

import cn.bc.identity.domain.Resource;
import cn.bc.web.formater.Formater;

/**
 * 资源类型值的格式化
 * 
 * @author dragon
 * 
 */
public class ResourceTypeFormater implements Formater {
	private Map<String, String> types;

	public ResourceTypeFormater() {
		types = new HashMap<String, String>();
		types.put(String.valueOf(Resource.TYPE_FOLDER), "分类");
		types.put(String.valueOf(Resource.TYPE_INNER_LINK), "内部链接");
		types.put(String.valueOf(Resource.TYPE_OUTER_LINK), "外部链接");
		//types.put(String.valueOf(Module.TYPE_HTML), "HTML");
	}

	public ResourceTypeFormater(Map<String, String> types) {
		this.types = types;
	}

	public String format(Object value) {
		if (value == null)
			return null;

		String f = types.get(value.toString());
		return f == null ? "undefined" : f;
	}
}
