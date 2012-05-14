package cn.bc.template.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.template.domain.Template;

/**
 * 模板类型Dao接口
 * 
 * @author lbj
 * 
 */
public interface TemplateDao extends CrudDao<Template> {
	/**
	 * 根据编码(和版本号)获取模板对象
	 * 
	 * @param code
	 *            如果含字符":"，则进行分拆，前面部分为编码，后面部分为版本号，如果没有字符":"，将获取当前状态为正常的版本
	 * @return 指定编码(和版本号)的模板对象
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
	public boolean isUniqueCodeAndVersion(Long currentId, String code,
			String version);

}
