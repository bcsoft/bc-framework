package cn.bc.form.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;


/**
 * 自定义表单Service
 * 
 * @author lbj
 * 
 */

public class CustomFormServiceImpl implements CustomFormService {
	private static Log logger = LogFactory.getLog(CustomFormServiceImpl.class);
	private FormService formService;
	private FieldService fieldService;
	
	@Autowired
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	@Autowired
	public void setFieldService(FieldService fieldService) {
		this.fieldService = fieldService;
	}

	public void doSave(Form form, List<Field> fields, JSONObject jo) {
		
		try {
			form = this.formService.save(form);
			jo.put("id", form.getId());
			JSONArray ja=new JSONArray();
			JSONObject _jo;
			for(Field f:fields){
				f.setForm(form);
				f=this.fieldService.save(f);
				_jo=new JSONObject();
				_jo.put("id", f.getId()+"");
				_jo.put("name", f.getName());
				ja.put(_jo);
			}
			jo.put("formData", ja);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

}
