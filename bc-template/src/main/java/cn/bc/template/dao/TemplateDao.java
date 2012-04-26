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
	 * 根据编码获取状态为正常的模板对象
	 * 
	 * @param code
	 * @return 指定编码的模板对象
	 */
	public Template loadByCode(String code);

	/**
	 * 判断指定的编码与版本号是否唯一
	 * 
	 * @param currentId
	 *            当前模板的id
	 * @param code
	 *            当前模板要使用的编码
	 * @param version
	 *            当前模板要使用的版本号
	 * @return
	 */
	public boolean isUniqueCodeAndVersion(Long currentId, String code,String version);

	
}
