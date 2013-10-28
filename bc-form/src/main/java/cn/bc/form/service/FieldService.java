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
	 * 获取字段对象
	 * @param form 表单id
	 * @param name 字段名称
	 * @return
	 */
	public Field findByPidAndName(Form form, String name);
}
