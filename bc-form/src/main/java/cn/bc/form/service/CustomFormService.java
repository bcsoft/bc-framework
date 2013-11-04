package cn.bc.form.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;

/**
 * 自定义表单Service接口
 * 
 * @author lbj
 * 
 */
public interface CustomFormService {

	void save(Form form, List<Field> fields, JSONObject jo) throws Exception;
	
	/**
	 * 保存自定义表单
	 * @param formInfoJO 表单信息
	 * @param formDataJA 表单字段信息
	 * @throws Exception
	 */
	void save(JSONObject formInfoJO, JSONArray formDataJA) throws Exception;

}
