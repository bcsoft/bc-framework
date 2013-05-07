package cn.bc.spider.parser;

import cn.bc.template.util.FreeMarkerUtils;

/**
 * 基于FreeMarker模版的数据格式化处理
 * 
 * @author dragon
 * 
 */
public class FreeMarkerTemplateParser implements Parser<Object> {
	private String tpl;

	public FreeMarkerTemplateParser(String tpl) {
		this.tpl = tpl;
	}

	public Object parse(Object data) {
		if (this.tpl == null)
			return null;

		return FreeMarkerUtils.format(this.tpl, data);
	}
}
