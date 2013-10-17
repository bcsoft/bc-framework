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
	 * 保存回复信息
	 * 
	 * @param pid
	 *            自定义表单的id
	 * @param form
	 *            自定义表单 对象
	 * @return
	 */
	void saveForm(Long pid, Form form);
	
	/**
	 * 获取格式化后的表单
	 * 
	 * @param tplCode
	 *            表单编码
	 * @return
	 */
	public String getFormattedForm(String tplCode);

}
