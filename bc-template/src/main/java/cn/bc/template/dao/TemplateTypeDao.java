package cn.bc.template.dao;

import java.util.List;
import java.util.Map;

import cn.bc.core.dao.CrudDao;
import cn.bc.template.domain.TemplateType;

/**
 * 模板类型Dao接口
 * 
 * @author lbj
 * 
 */
public interface TemplateTypeDao extends CrudDao<TemplateType> {
	/**
	 * 判断指定的编码是否唯一
	 * 
	 * @param currentId
	 *            当前模板的id
	 * @param code
	 *            当前模板要使用的编码        
	 * @return
	 */
	public boolean isUniqueCode(Long currentId, String code);
	
	/**
	 * 查找模板类型
	 * 
	 * @return
	 */
	public List<Map<String,String>> findTemplateTypeOption();

}
