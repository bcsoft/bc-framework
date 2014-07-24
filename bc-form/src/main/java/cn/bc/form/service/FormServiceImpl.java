package cn.bc.form.service;

import java.util.List;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.form.dao.FieldDao;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Form;
import org.springframework.util.Assert;

/**
 * 自定义表单Service
 * 
 * @author hwx
 * 
 */

public class FormServiceImpl extends DefaultCrudService<Form> implements FormService {
	private FormDao formDao;
	@Autowired
	private FieldDao fieldDao;

	@Autowired
	public void setFromDao(FormDao formDao) {
		this.setCrudDao(formDao);
		this.formDao = formDao;
	}

	public Form findByParent(String type, String code, Long pid, String ver) {
		Assert.hasText(type, "type couldn't be empty");
		Assert.hasText(code, "code couldn't be empty");
		Assert.notNull(pid, "pid couldn't be empty");

		AndCondition and = new AndCondition();
		and.add(new EqualsCondition("type", type));
		and.add(new EqualsCondition("code", code));
		and.add(new EqualsCondition("pid", pid));
		and.add(new OrderCondition("version", Direction.Desc));
		if(ver != null && !ver.isEmpty()) {
			and.add(new EqualsCondition("ver", ver));
		}
		List<Form> forms = this.createQuery().condition(and).list(1, 1);
		if (forms != null && !forms.isEmpty()) {
			Form form = forms.get(0);

			// 获取表单的所有字段配置
			form.setFields(this.fieldDao.findByParent(form.getId()));

			return form;
		} else {
			return null;
		}
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

	public void delete(String type, String code, Long pid, String ver) {
		throw new CoreException("TODO: unimplement method.");
	}
}