package cn.bc.spider.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.SpringUtils;
import cn.bc.template.service.TemplateService;
import cn.bc.core.util.FreeMarkerUtils;

/**
 * 基于模版的数据格式化处理
 * 
 * @author dragon
 * 
 */
public class TemplateParser implements Parser<Object> {
	private String code;// 模版的编码
	private TemplateService templateService;

	public TemplateParser(String code) {
		Assert.notNull(code, "code can't be null.");
		this.code = code;
		templateService = SpringUtils.getBean(TemplateService.class);
	}

	public Object parse(Object data) {
		String tpl = this.templateService.getContent(code);
		if (tpl == null || tpl.isEmpty())
			throw new CoreException("can't get template: code=" + code);
		if (data == null)
			return tpl;

		boolean needWrap = (data instanceof Collection)
				|| (data.getClass().isArray());
		data.getClass().isArray();
		if (needWrap) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("_d", data);
			return FreeMarkerUtils.format(tpl, args);
		} else {
			return FreeMarkerUtils.format(tpl, data);
		}
	}
}
