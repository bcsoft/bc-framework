package cn.bc.template.service;

import java.util.Map;

import cn.bc.core.service.CrudService;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数Service接口
 * 
 * @author lbj
 * 
 */
public interface TemplateParamService extends CrudService<TemplateParam> {
	
	/**
	 * 获取此模板参数的模板替换的键值集合
	 * 
	 * @param templateParam 模板参数
	 * @param mapFormatSql 格式化此模板参数详细配置sql中的占位符
	 * @return
	 */
	Map<String,Object> getMapParams(TemplateParam templateParam,Map<String,Object> mapFormatSql);
}
