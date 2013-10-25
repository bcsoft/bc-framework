package cn.bc.form.service;

import java.util.List;

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

	void save(Form form,List<Field> fields,JSONObject jo) throws Exception ;
	
	
}
