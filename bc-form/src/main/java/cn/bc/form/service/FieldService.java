package cn.bc.form.service;

import java.util.List;
import java.util.Map;

import cn.bc.core.service.CrudService;
import cn.bc.form.domain.Field;

/**
 * 表单字段Service接口
 * 
 * @author hwx
 * 
 */

public interface FieldService extends CrudService<Field>{
	/**
	 * 查找表单字段集合
	 * 
	 * @param formId 表单id
	 */
	public List<Map<String, Object>> loadFields(Long formId);
}
