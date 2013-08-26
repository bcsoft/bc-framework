package cn.bc.template.service;

import java.util.Map;

/**
 * 获取模块替换模板参数的Service接口
 * 
 * @author lbj
 * 
 */
public interface ParamsExcutorService{

	/**
	 * 获取模块替换模板参数
	 * @param id 模块的id
	 * @return
	 */
	Map<String,Object> getParamsExcutor(Long id);
}
