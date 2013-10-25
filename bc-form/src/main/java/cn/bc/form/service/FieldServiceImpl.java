package cn.bc.form.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FieldDao;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;

/**
 * 表单字段Service
 * 
 * @author hwx
 * 
 */

public class FieldServiceImpl extends DefaultCrudService<Field> implements FieldService {
	private FieldDao fieldDao;

	@Autowired
	public void setFromDao(FieldDao fieldDao) {
		this.setCrudDao(fieldDao);
		this.fieldDao = fieldDao;
	}

	public List<Field> findList(Form form) {
		return this.createQuery().condition(new EqualsCondition("form", form)).list();
	}
}
