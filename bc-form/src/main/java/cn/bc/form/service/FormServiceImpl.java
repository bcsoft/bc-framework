package cn.bc.form.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.TemplateUtils;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Form;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;

/**
 * 自定义表单Service
 * 
 * @author hwx
 * 
 */

public class FormServiceImpl extends DefaultCrudService<Form> implements
		FormService {

	private FormDao formDao;
	private TemplateService templateService;
	private Map<String, Object> templArgs = new HashMap<String, Object>();
	private String templContent;	
	
	@Autowired
	public void setFromDao(FormDao formDao) {
		this.setCrudDao(formDao);
		this.formDao = formDao;
	}
	
	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}
	
	public void initForm(String tpl) {
		templContent = templateService.getContent(tpl);
		List<String> keyNames = TemplateUtils.findMarkers(templContent);

		for(int i=0; i<keyNames.size(); i++) {
			templArgs.put(keyNames.get(i), "");
		}
	}
	
	public String getFormattedForm() {
		String fomattedCont = TemplateUtils.format(templContent, templArgs);
		return fomattedCont;
	}

	public Map<String, Object> getTemplArgs() {
		return templArgs;
	}
	
	public void setTemplArgs(Map<String, Object> templArgs) {
		this.templArgs = templArgs;
	}
}
