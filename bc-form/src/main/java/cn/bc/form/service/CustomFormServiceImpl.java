package cn.bc.form.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.util.DateUtils;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.FieldLog;
import cn.bc.form.domain.Form;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 自定义表单Service
 * 
 * @author lbj & hwx
 * 
 */

public class CustomFormServiceImpl implements CustomFormService {
	private FormService formService;
	private FieldService fieldService;
	private FieldLogService fieldLogService;

	@Autowired
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	@Autowired
	public void setFieldService(FieldService fieldService) {
		this.fieldService = fieldService;
	}

	@Autowired
	public void setFieldLogService(FieldLogService fieldLogService) {
		this.fieldLogService = fieldLogService;
	}

	public void save(JSONObject formInfoJO, JSONArray formDataJA)
			throws Exception {
		ActorHistory actor = SystemContextHolder.get().getUserHistory();
		List<Field> fields = new ArrayList<Field>();
		List<FieldLog> fieldLogs = new ArrayList<FieldLog>();

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

				FieldLog fieldLog = new FieldLog();
				fieldLog.setField(field);
				fieldLog.setValue(formDataJO.getString("value"));
				fieldLog.setUpdator(actor);
				fieldLog.setUpdateTime(Calendar.getInstance());
				fieldLog.setBatchNo(DateUtils.formatCalendar(
						Calendar.getInstance(), "yyyyMMddHmmssSSSS"));
				fieldLogs.add(fieldLog);
			}
		} else {// 编辑保存
			// 表单字段处理
			JSONObject formDataJO;
			for (int i = 0; i < formDataJA.length(); i++) {
				formDataJO = (JSONObject) formDataJA.get(i);
				Field field = null;
				field = this.fieldService.findByPidAndName(form,
						formDataJO.getString("name"));

				if (field == null) { // 如果字段为新添加
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
				} else { // 如果字段已存在
					if (!formDataJO.getString("value").equals(field.getValue())) { // 和数据库中的字段值对比，如果不等,设置新值
						field.setValue(formDataJO.getString("value"));
						fields.add(field);
					}
				}
				FieldLog fieldLog = new FieldLog();
				fieldLog.setField(field);
				fieldLog.setValue(formDataJO.getString("value"));
				fieldLog.setUpdator(actor);
				fieldLog.setUpdateTime(Calendar.getInstance());
				fieldLog.setBatchNo(DateUtils.formatCalendar(
						Calendar.getInstance(), "yyyyMMddHmmssSSSS"));
				fieldLogs.add(fieldLog);
			}
		}
		if (fields.size() > 0) {
			// 表单信息处理
			form.setModifier(actor);
			form.setModifiedDate(Calendar.getInstance());
			this.formService.save(form);
			this.fieldService.save(fields);
			this.fieldLogService.save(fieldLogs);
		}

	}

	public void delete(String type, long pid, String code) {
		// 根据自定义表单type、pid、code，获取相应的自定义表单表单对象
		Form form = this.formService.findByTPC(type, pid, code);

		List<Field> fields = this.fieldService.findList(form);
		List<FieldLog> fieldLogs;

		if (fields != null && fields.size() != 0) {
			Long[] fieldIds = new Long[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				fieldIds[i] = fields.get(i).getId();
				// 批量 删除审计日志
				fieldLogs = this.fieldLogService.findList(fields.get(i));
				this.fieldLogService.delete(fieldLogs);
			}
			// 批量 删除表单字段
			this.fieldService.delete(fieldIds);
		}
		this.formService.delete(form.getId());
	}

	public void delete(Long id) {
		// 根据自定义表单id，获取相应的自定义表单表单对象
		Form form = this.formService.load(id);

		List<Field> fields = this.fieldService.findList(form);
		List<FieldLog> fieldLogs;

		if (fields != null && fields.size() != 0) {
			Long[] fieldIds = new Long[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				fieldIds[i] = fields.get(i).getId();
				// 批量 删除审计日志
				fieldLogs = this.fieldLogService.findList(fields.get(i));
				this.fieldLogService.delete(fieldLogs);
			}
			// 批量 删除表单字段
			this.fieldService.delete(fieldIds);
		}

		this.formService.delete(form.getId());
	}

	public void delete(Long[] ids) {

		for (int i = 0; i < ids.length; i++) {
			// 根据自定义表单id，获取相应的自定义表单表单对象
			Form form = this.formService.load(ids[i]);

			List<Field> fields = this.fieldService.findList(form);
			List<FieldLog> fieldLogs;

			if (fields != null && fields.size() != 0) {
				Long[] fieldIds = new Long[fields.size()];
				for (int j = 0; j < fields.size(); j++) {
					fieldIds[j] = fields.get(j).getId();
					// 批量 删除审计日志
					fieldLogs = this.fieldLogService.findList(fields.get(j));
					this.fieldLogService.delete(fieldLogs);
				}
				// 批量 删除表单字段
				this.fieldService.delete(fieldIds);
			}

		}
		this.formService.delete(ids);
	}
}