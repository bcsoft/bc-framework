package cn.bc.form.service;

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

	/**
	 * 初始化自定义表单
	 * 
	 * @param tpl
	 *            表单编码
	 */
	public void initForm(String tpl);

	/**
	 * 获取格式化后的表单
	 * 
	 * @return
	 */
	public String getFormattedForm();

	/**
	 * 获取模板参数
	 * 
	 * @return
	 */
	public Map<String, Object> getTemplArgs();

	/**
	 * 设置模板参数
	 * 
	 * @return
	 */
	public void setTemplArgs(Map<String, Object> templArgs);
}
