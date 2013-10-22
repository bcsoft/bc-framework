package cn.bc.form.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Form;

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
	

	
}
