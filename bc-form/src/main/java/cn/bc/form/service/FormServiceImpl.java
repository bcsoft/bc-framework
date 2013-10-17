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

	@Autowired
	public void setFromDao(FormDao formDao) {
		this.setCrudDao(formDao);
		this.formDao = formDao;
	}
	
	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}


	public void saveForm(Long pid, Form form) {
		// TODO Auto-generated method stub

	}

	public String getFormattedForm(String tplCode) {
		String Templcontent = templateService.getContent(tplCode);
		List<String> keyNames = TemplateUtils.findMarkers(Templcontent);
		Map<String, Object> args = new HashMap<String, Object>();
		
		for(int i=0; i<keyNames.size(); i++) {
			args.put(keyNames.get(i), "");
		}
		
		String fomattedCont = TemplateUtils.format(Templcontent, args);
		return fomattedCont;
	}
}
