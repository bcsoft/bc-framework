package cn.bc.template.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.TemplateUtils;
import cn.bc.template.dao.TemplateParamDao;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数Service接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateParamServiceImpl extends DefaultCrudService<TemplateParam> implements
		TemplateParamService {
	protected final Log logger = LogFactory.getLog(getClass());
	private TemplateParamDao templateParamDao;
	@Autowired
	public void setTemplateParamDao(TemplateParamDao templateParamDao) {
		this.setCrudDao(templateParamDao);
		this.templateParamDao = templateParamDao;
	}

	/**
	 * 详细配置格式：json对象数组。
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
	 */
	public Map<String, Object> getMapParams(TemplateParam templateParam,
			Map<String, Object> mapFormatSql) {
		//声明返回的map集合
		Map<String, Object> mapParams=null;
		
		//获取json对象数组
		JSONArray ja=templateParam.getConfigJson();
		try {
			for(int i=0;i<ja.length();i++){
				JSONObject jo=ja.getJSONObject(i);
				//查询返回结果类型
				String type=jo.getString("type").toString();
				//格式化后的sql
				String formattedSql=TemplateUtils.format(jo.get("sql").toString(), mapFormatSql);
				if(type.equals("Map")){
					Map<String,Object> m = this.templateParamDao.getMap(formattedSql);
					if(m == null || m.isEmpty())
						//继续循环
						continue;
					if(mapParams == null || mapParams.isEmpty()){
						mapParams = m;
					}else{
						Set<String> tempSet = m.keySet();
						for(String key:tempSet)
							mapParams.put(key, m.get(key));
					}
				}else if(type.equals("List<Object>")||type.equals("Object[]")){
					List<Object> list = this.templateParamDao.getListIncludeObject(formattedSql);
					if(list.size()==0)
						continue;
					if(mapParams == null || mapParams.isEmpty())
						mapParams = new HashMap<String, Object>();
					mapParams.put(jo.get("key").toString(), list);
				}else if(type.equals("List<Map<String,Object>>")){
					List<Map<String,Object>> list = this.templateParamDao.getListIncludeMap(formattedSql);
					if(list.size()==0)
						continue;
					if(mapParams == null || mapParams.isEmpty())
						mapParams = new HashMap<String, Object>();
					mapParams.put(jo.get("key").toString(), list);
				}else if(type.equals("List<list<Object>>")||type.equals("List<Object[]>")){
					List<List<Object>> list = this.templateParamDao.getListIncludeList(formattedSql);
					if(list.size()==0)
						continue;
					if(mapParams == null || mapParams.isEmpty())
						mapParams = new HashMap<String, Object>();
					mapParams.put(jo.get("key").toString(), list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return mapParams;
	}

	
}
