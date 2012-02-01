/**
 * 
 */
package cn.bc.identity.web.formater;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.bc.identity.domain.ActorDetail;
import cn.bc.web.formater.AbstractFormater;

/**
 * 性别的格式化
 * 
 * @author dragon
 * 
 */
public class SexFormater extends AbstractFormater<String> {
	private Map<String, String> sexes;

	public SexFormater setSexes(Map<String, String> sexes) {
		this.sexes = sexes;
		return this;
	}

	public SexFormater() {
		sexes = new LinkedHashMap<String, String>();
		sexes.put(String.valueOf(ActorDetail.SEX_MAN), "男");
		sexes.put(String.valueOf(ActorDetail.SEX_WOMAN), "女");
		sexes.put(String.valueOf(ActorDetail.SEX_NONE), "");
	}

	public SexFormater(Map<String, String> sexes) {
		this.sexes = sexes;
	}

	public String format(Object context, Object value) {
		if (value == null)
			return null;
		if (sexes == null)
			return "undefined";

		String f = sexes.get(value.toString());
		return f == null ? "undefined" : f;
	}
}
