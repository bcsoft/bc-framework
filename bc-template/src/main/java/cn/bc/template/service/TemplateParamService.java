package cn.bc.template.service;

import java.util.Map;

import cn.bc.core.service.CrudService;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数Service接口
 * 
 * @author lbj
 * 
 */
public interface TemplateParamService extends CrudService<TemplateParam> {
	
	/**
	 * 详细配置格式：json对象数组。
	 * 对象属性解释，type:返回的的类型，slq:数据库查询的语句,key:替换指定的键值
	 * 
	 * {type:"Map",sql:"select name as key1,company as key2,code as key3 from bs_car"}  
	 * 		-- sql查询只返回一行的数据，每一列数据对应一个key值，模板占位参数直接填写${key1},${key2},${key2}
	 * {type:"List<Object>",sql:"...",key:"key"}    	
	 * 		-- sql查询只返回一列数据，整个集合对应一个key值，模板占位参数应填写${key[0]},${key[1]},${key[2]}
	 * {type:"List<Map<String,Object>>",sql:"select name as key1,company as key2,code as key3 from bs_car",key:"key"}	 
	 * 		-- sql查询返回多列数据，每一行的数据为一个map的集合，整个集合对应一个key值，模板占位参数应填写${key[0].key1},${key[0].key2,${key[0].key3}
	 * {type:"List<list<Object>>",sql:"...",key:"key"}	
	 * 		-- sql查询返回多列数据，每一"行"的数据为一个list的集合，整个集合对应一个key值，模板占位参数应填写${key[0][0]},${key[0][1]}
	 * {type:"Object[]",sql:"...",key:"key"}		 	
	 * 		-- 与类型[List<Object>]情况一样。
	 * {type:"List<Object[]>",sql:"...",key:"key"}		
	 * 		-- 与类型[List<list<Object>>]情况一样。
	 * 
	 * {type:"Json2Map",sql:"...",key:"key"}		
	 * 		-- 与类型[Map]情况一样，sql必须返回只有一个字符串值
	 * {type:"Json2ListMap",sql:"...",key:"key"}		
	 * 		-- 与类型[List<Map<String,Object>>]，sql必须返回只有一个字符串值
	 * {type:"Json2Array",sql:"...",key:"key"}		
	 * 		-- 与类型[List<Object>]，sql必须返回只有一个字符串值
	 * {type:"Json2List",sql:"...",key:"key"}		
	 * 		-- 与类型[List<Object>]，sql必须返回只有一个字符串值
	 * 
	 * @param templateParam 模板参数
	 * 
	 * @param mapFormatSql 格式化sql上的占位符，sql集合的集合
	 * 
	 */
	Map<String,Object> getMapParams(TemplateParam templateParam,Map<String,Object> mapFormatSql);
}
