package cn.bc.template.engine;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.exception.CoreException;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;

/**
 * 模板引擎渲染抽象化接口
 * 
 * @author dragon
 * 
 */
public class TemplateEngineImpl<T extends Object> implements TemplateEngine<T> {
	// private static Log logger = LogFactory.getLog(TemplateEngineImpl.class);
	private Map<String, TemplateEngine<Object>> engines;
	private TemplateService templateService;

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@SuppressWarnings("unchecked")
	public T render(String code, Map<String, Object> args) {
		// 加载模板信息
		Template tpl = this.templateService.loadByCode(code);
		if (tpl == null) {
			throw new CoreException("没有找到编码为'" + code + "'的模板!");
		}

		// 根据模板类型获取相应的渲染引擎
		String typeCode = tpl.getTemplateType().getCode();
		@SuppressWarnings("rawtypes")
		TemplateEngine engine = engines.get(typeCode);
		if (engine == null) {
			throw new CoreException("没有找到编码为'" + typeCode + "'的模板引擎!");
		}

		return (T) engine.render(tpl.getContent(), args);
	}
}