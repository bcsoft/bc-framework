package cn.bc.form.dao.hibernate.jpa;

import cn.bc.core.exception.CoreException;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Form;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 自定义表单DaoImpl
 * 
 * @author hwx
 * 
 */

public class FormDaoImpl extends HibernateCrudJpaDao<Form> implements FormDao{
	public void delete(String type, String code, Long pid, Float ver) {
		String sql = "delete from bc_form where type_=? and code=? and pid=? and ver_=?";
		this.executeSql(sql, new Object[]{type, code, pid, ver});
	}
}
