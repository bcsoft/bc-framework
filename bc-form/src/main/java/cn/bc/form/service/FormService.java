package cn.bc.form.service;

import java.util.List;
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
	 * 获取指定业务对象的表单
	 * @param type 类别
	 * @param code 编码
	 * @param pid 业务ID
	 * @param ver 表单版本号
	 * @return
	 */
	public Form load(String type, String code, Long pid, Float ver);

	/**
	 * 保存指定的表单数据
	 * @param type 类型
	 * @param code 编码
	 * @param pid 业务ID
	 * @param ver 版本号
	 * @param data 表单数据，格式为：{name:{value:"aa",type:"string",scope:"form"},...}
	 */
	Form save(String type, String code, Long pid, Float ver, Map<String, Object> data);

	/**
	 * 删除指定的表单
	 * @param type 类型
	 * @param code 编码
	 * @param pid 业务ID
	 * @param ver 版本号
	 */
	void delete(String type, String code, Long pid, Float ver);

	/**
	 * 查找指定的表单
	 * @param type 类别
	 * @param pid 其他模块调用此模块时，该模块记录的id
	 * @param code 其他模块调用此模块时，使用的编码
	 * @return
	 */
	public Form findByTPC(String type, Long pid, String code);

	/**
	 * 查找指定类别和pid的表单集合
	 * @param type 类别
	 * @param pid 其他模块调用此模块时，该模块记录的id
	 */
	public List<Form> findList(String type, Long pid);
	
	/**
	 * 查找指定类别和pid的表单集合
	 * @param type 类别
	 * @return
	 */
	public List<Form> findList(String type);

	/**
	 * 获取当前的最新版本号
	 * @param type
	 * @param code
	 * @param pid
	 * @return
	 */
	Float getNewestVer(String type, String code, Long pid);
}
