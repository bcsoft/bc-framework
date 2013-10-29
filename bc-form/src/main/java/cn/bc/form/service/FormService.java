package cn.bc.form.service;

import cn.bc.core.service.CrudService;
import cn.bc.form.domain.Form;

/**
 * 自定义表单Service接口
 * 
 * @author hwx
 * 
 */
public interface FormService extends CrudService<Form> {
	
	/**
	 * 查找是否存指定的表单
	 * @param type 类别
	 * @param pid 其他模块调用此模块时，该模块记录的id
	 * @param code 其他模块调用此模块时，使用的编码
	 * @return
	 */
	public Form findForm(String type, Long pid, String code);

	
}
