package cn.bc.form.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FieldDao;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Field;

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
}
