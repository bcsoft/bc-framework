package cn.bc.form.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 自定义表单Service
 * 
 * @author hwx
 * 
 */

public class FormServiceImpl extends DefaultCrudService<Form> implements
		FormService {

	private FormDao formDao;

	
	@Autowired
	public void setFromDao(FormDao formDao) {
		this.setCrudDao(formDao);
		this.formDao = formDao;
	}
	
	public void saveForm(Map<String, Object> formInfoMap,Collection<Map<String, Object>> formDataMap) {
		Form form = new Form();
		form.setUid((String) formInfoMap.get("uid"));
		form.setType((String) formInfoMap.get("type"));
		form.setStatus((Integer) formInfoMap.get("status"));
		form.setSubject((String) formInfoMap.get("subject"));
		form.setTpl((String) formInfoMap.get("tpl"));
		form.setAuthor(SystemContextHolder.get().getUserHistory());
		form.setFileDate(Calendar.getInstance());
		
		this.save(form);
		
		List<Field> fields = new ArrayList<Field>();
		for (Map<String, Object> m : formDataMap) {
			Field field = new Field();
			field.setName((String)m.get("name"));
			field.setLabel("label");
			field.setType((String)m.get("type"));
			field.setValue((String)m.get("value"));
			fields.add(field);
		}
	}
	
}
