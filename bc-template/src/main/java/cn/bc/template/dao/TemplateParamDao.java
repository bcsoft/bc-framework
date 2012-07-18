package cn.bc.template.dao;

import java.util.List;
import java.util.Map;

import cn.bc.core.dao.CrudDao;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数Dao接口
 * 
 * @author lbj
 * 
 */
public interface TemplateParamDao extends CrudDao<TemplateParam> {
	
	/**
	 * 获取只返回一行数据,将行中列的数据放在Map集合中
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	Map<String,Object> getMap(String sql) throws Exception;
	
	/**
	 * 获取只返回会一列的数据,将整列数据放在一个list集合中
	 * 
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	List<Object> getListIncludeObject(String sql) throws Exception;
	
	/**
	 * 获取返回多行多列的数据，将每一行中的数据方在Map集合中，然后将Map加入list中
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> getListIncludeMap(String sql) throws Exception;
	
	/**
	 * 获取返回多行多列的数据，将每一行的数据方在list的集合中，
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	List<List<Object>> getListIncludeList(String sql) throws Exception;
}
