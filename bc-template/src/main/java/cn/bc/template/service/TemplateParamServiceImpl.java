package cn.bc.template.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.template.dao.TemplateParamDao;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数Service接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateParamServiceImpl extends DefaultCrudService<TemplateParam> implements
		TemplateParamService {
	@SuppressWarnings("unused")
	private TemplateParamDao templateParamDao;

	@Autowired
	public void setTemplateParamDao(TemplateParamDao templateParamDao) {
		this.setCrudDao(templateParamDao);
		this.templateParamDao = templateParamDao;
	}

	
}
