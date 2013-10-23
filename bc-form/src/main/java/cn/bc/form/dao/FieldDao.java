package cn.bc.form.dao;

import java.util.List;
import java.util.Map;

import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;
import cn.bc.form.domain.Field;

/**
 * 表单字段Dao
 * 
 * @author hwx
 * 
 */

public interface FieldDao extends CrudDao<Field> {
	/**
	 * 查找自定义表单字段列表
	 * @parma formId 表单id
	 * @return
	 */
	public List<Map<String, Object>> fieldList(Long formId);
}
