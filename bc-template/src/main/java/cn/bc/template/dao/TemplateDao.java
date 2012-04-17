package cn.bc.template.dao;

import java.util.List;
import java.util.Map;

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

	// == 以下为待清理的接口 ==

	/**
	 * 根据Id查找模板
	 * 
	 * @param id
	 * @return type模板类型、name模板名称、tfname文件名称
	 */
	public List<Map<String, String>> findOneTemplateById(Integer id);

	/**
	 * 根据编码查找模板
	 * 
	 * @param Code
	 * @return type模板类型、name模板名称、tfname文件名称
	 */
	public List<Map<String, String>> findOneTemplateByCode(String code);

	/**
	 * 返回相同文件名称的数量
	 * 
	 * @param type
	 * @param fileNmae
	 * @return
	 */
	public int countTemplateFileName(String fileName);
}
