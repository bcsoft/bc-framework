package cn.bc.form.service;

import java.util.Collection;
import java.util.Map;

import cn.bc.core.service.CrudService;
import cn.bc.form.domain.Form;

/**
 * 自定义表单Service接口
 * 
 * @author hwx
 * 
 */
public interface FormService extends CrudService<Form> {

	public void saveForm(Map<String, Object> formInfoMap,Collection<Map<String, Object>> formDataMap);
}
