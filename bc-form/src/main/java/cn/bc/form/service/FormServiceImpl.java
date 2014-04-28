package cn.bc.form.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Form;

/**
 * 自定义表单Service
 * 
 * @author hwx
 * 
 */

public class FormServiceImpl extends DefaultCrudService<Form> implements
		FormService {

	private FormDao formDao;

	@Autowired
	public void setFromDao(FormDao formDao) {
		this.setCrudDao(formDao);
		this.formDao = formDao;
	}

	public Form findByTPC(String type, Long pid, String code) {
		AndCondition ac = new AndCondition();
		ac.add(new EqualsCondition("type", type));
		ac.add(new EqualsCondition("pid", pid));
		ac.add(new EqualsCondition("code", code));
		if (this.createQuery().condition(ac).count() == 0) {
			return null;
		} else {
			return this.createQuery().condition(ac).list().get(0);
		}

	}
	
	public List<Form> findList(String type, Long pid) {
		AndCondition ac = new AndCondition();
		ac.add(new EqualsCondition("type", type));
		ac.add(new EqualsCondition("pid", pid));
		if (this.createQuery().condition(ac).count() == 0) {
			return null;
		} else {
			return this.createQuery().condition(ac).list();
		}
	}

	public List<Form> findList(String type) {
		AndCondition ac = new AndCondition();
		ac.add(new EqualsCondition("type", type));
		if (this.createQuery().condition(ac).count() == 0) {
			return null;
		} else {
			return this.createQuery().condition(ac).list();
		}
	}

}
