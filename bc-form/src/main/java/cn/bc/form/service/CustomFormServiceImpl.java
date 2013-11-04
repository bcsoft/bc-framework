package cn.bc.form.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.util.DateUtils;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 自定义表单Service
 * 
 * @author lbj
 * 
 */

public class CustomFormServiceImpl implements CustomFormService {
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

	public void save(Form form, List<Field> fields, JSONObject jo)
			throws Exception {
		form = this.formService.save(form);
		jo.put("id", form.getId());
		jo.put("pid", form.getPid());
		jo.put("code", form.getCode());
		JSONArray ja = new JSONArray();
		JSONObject _jo;
		for (Field f : fields) {
			f.setForm(form);
			f = this.fieldService.save(f);
			_jo = new JSONObject();
			_jo.put("id", f.getId() + "");
			_jo.put("name", f.getName());
			ja.put(_jo);
		}
		jo.put("formData", ja);
	}

	public void save(JSONObject formInfoJO, JSONArray formDataJA) throws Exception {
		ActorHistory actor = SystemContextHolder.get().getUserHistory();
		List<Field> fields = new ArrayList<Field>();
		
		String type = formInfoJO.getString("type");
		long pid = formInfoJO.getLong("pid");
		String code = formInfoJO.getString("code");
		
		Form form = this.formService.findByTPC(type, pid, code);
		// 新建保存
		if (form == null) {
			// 表单信息处理
			form = new Form();
			form.setPid(pid);
			form.setUid(formInfoJO.getString("uid"));
			form.setType(type);
			form.setCode(code);
			form.setStatus(formInfoJO.getInt("status"));
			form.setSubject(formInfoJO.getString("subject"));
			form.setTpl(formInfoJO.getString("tpl"));
			form.setAuthor(actor);
			form.setFileDate(DateUtils.getCalendar(formInfoJO
					.getString("fileDate")));
			form.setModifier(actor);
			form.setModifiedDate(Calendar.getInstance());
			this.formService.save(form);

			// 表单字段处理
			
			JSONObject formDataJO;
			for (int i = 0; i < formDataJA.length(); i++) {
				Field field = new Field();
				formDataJO = (JSONObject) formDataJA.get(i);
				field.setForm(form);
				field.setName(formDataJO.getString("name"));
				field.setType(formDataJO.getString("type"));
				field.setValue(formDataJO.getString("value"));
				if (formDataJO.isNull("label")) {
					field.setLabel("");
				} else {
					field.setLabel(formDataJO.getString("label"));
				}
				fields.add(field);
			}
			this.fieldService.save(fields);
		} else {// 编辑保存
			// 表单信息处理
			form.setModifier(actor);
			form.setModifiedDate(Calendar.getInstance());
			this.formService.save(form);
			
			// 表单字段处理
			
			JSONObject formDataJO;
			for (int i = 0; i < formDataJA.length(); i++) {
				formDataJO = (JSONObject) formDataJA.get(i);
				Field field = null;
				field = this.fieldService.findByPidAndName(form,
						formDataJO.getString("name"));
				if (field != null) {
					field.setValue(formDataJO.getString("value"));
				} else {
					field = new Field();
					field.setForm(form);
					field.setName(formDataJO.getString("name"));
					field.setType(formDataJO.getString("type"));
					field.setValue(formDataJO.getString("value"));
					if (formDataJO.isNull("label")) {
						field.setLabel("");
					} else {
						field.setLabel(formDataJO.getString("label"));
					}
					fields.add(field);
				}
				
			}
			this.fieldService.save(fields);
		}

	}

}
