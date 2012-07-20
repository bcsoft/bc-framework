/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.Map;

import cn.bc.web.formater.AbstractFormater;

/**
 * 是否需要评分的格式化
 * 
 * @author dragon
 * 
 */
public class IsGradeFormater extends AbstractFormater<Object> {
	public IsGradeFormater setKvs(Map<String, ? extends Object> kvs) {
		return this;
	}

	public IsGradeFormater() {
	}

	public IsGradeFormater(Map<String, ? extends Object> kvs) {
	}

	public Object format(Object context, Object value) {
		if (value == null) {
			return "否";
		} else {
			return "是";
		}

	}
}
