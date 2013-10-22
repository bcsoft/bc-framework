package cn.bc.form.service;

import java.util.Set;

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

	void save(Form form,Set<Field> fields,JSONObject jo);
	
	
}
