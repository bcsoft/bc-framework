package cn.bc.form.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;

/**
 * 表单字段Service接口
 * 
 * @author hwx
 * 
 */

public interface FieldService extends CrudService<Field>{

	/**
	 * 获取表单字段集合
	 * @param form
	 * @return
	 */
	List<Field> findList(Form form);
	
	/**
	 * 
	 * @param form
	 * @param name
	 * @return
	 */
	public Field findField(Form form, String name);
}
