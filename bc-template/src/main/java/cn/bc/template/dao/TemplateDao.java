package cn.bc.template.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.template.domain.Template;

/**
 * Dao接口
 * 
 * @author lbj
 * 
 */
public interface TemplateDao extends CrudDao<Template> {
	/**
	 * 根据编码获取模板对象
	 * 
	 * @param code
	 * @return 指定编码的模板对象
	 */
	public Template loadByCode(String code);

	/**
	 * 判断指定的编码是否唯一
	 * 
	 * @param currentId
	 *            当前模板的id
	 * @param code
	 *            当前模板要使用的编码
	 * @return
	 */
	public boolean isUnique(Long currentId, String code);

	
}
