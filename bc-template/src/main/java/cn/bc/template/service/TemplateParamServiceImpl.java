package cn.bc.template.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.JsonUtils;
import cn.bc.core.util.TemplateUtils;
import cn.bc.template.dao.TemplateParamDao;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数Service接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateParamServiceImpl extends DefaultCrudService<TemplateParam>
		implements TemplateParamService, ApplicationContextAware {
	protected final Log logger = LogFactory.getLog(getClass());
	private TemplateParamDao templateParamDao;
	private ExpressionParser expressionParser;
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Autowired
	public void setTemplateParamDao(TemplateParamDao templateParamDao) {
		this.setCrudDao(templateParamDao);
		this.templateParamDao = templateParamDao;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	/**
	 * 详细配置格式：json对象数组。 对象属性解释，type:返回的的类型，slq:数据库查询的语句,key:替换指定的键值
	 * 
	 * {type:"Map",sql:
	 * "select name as key1,company as key2,code as key3 from bs_car"} --
	 * sql查询只返回一行的数据，每一列数据对应一个key值，模板占位参数直接填写${key1},${key2},${key2}
	 * {type:"List<Object>",sql:"...",key:"key"} --
	 * sql查询只返回一列数据，整个集合对应一个key值，模板占位参数应填写${key[0]},${key[1]},${key[2]}
	 * {type:"List<Map<String,Object>>"
	 * ,sql:"select name as key1,company as key2,code as key3 from bs_car"
	 * ,key:"key"} --
	 * sql查询返回多列数据，每一行的数据为一个map的集合，整个集合对应一个key值，模板占位参数应填写${key[0].
	 * key1},${key[0].key2,${key[0].key3}
	 * {type:"List<list<Object>>",sql:"...",key:"key"} --
	 * sql查询返回多列数据，每一"行"的数据为一个list的集合
	 * ，整个集合对应一个key值，模板占位参数应填写${key[0][0]},${key[0][1]}
	 * {type:"Object[]",sql:"...",key:"key"} -- 与类型[List<Object>]情况一样。
	 * {type:"List<Object[]>",sql:"...",key:"key"} --
	 * 与类型[List<list<Object>>]情况一样。
	 * 
	 * {type:"Json2Map",sql:"...",key:"key"} -- 与类型[Map]情况一样，sql必须返回只有一个字符串值
	 * {type:"Json2ListMap",sql:"...",key:"key"} --
	 * 与类型[List<Map<String,Object>>]，sql必须返回只有一个字符串值
	 * {type:"Json2Array",sql:"...",key:"key"} --
	 * 与类型[List<Object>]，sql必须返回只有一个字符串值 {type:"Json2List",sql:"...",key:"key"}
	 * -- 与类型[List<Object>]，sql必须返回只有一个字符串值
	 * 
	 * @param templateParam
	 *            模板参数
	 * 
	 * @param mapFormatSql
	 *            格式化sql上的占位符，sql集合的集合
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapParams(TemplateParam templateParam,
			Map<String, Object> mapFormatSql) {
		// 声明返回的map集合
		Map<String, Object> mapParams = null;

		// 获取json对象数组
		JSONArray ja = templateParam.getConfigJson();
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				// 查询返回结果类型
				String type = jo.getString("type").toString();
				// 格式化后的sql
				String formattedSql = TemplateUtils.format(jo.get("sql")
						.toString(), mapFormatSql);
				if ("Map".equals(type) || "Json2Map".equals(type)) {
					Map<String, Object> m;
					if ("Json2Map".equals(type)) {
						m = JsonUtils.toMap(this.templateParamDao
								.getJsonsString(formattedSql));
					} else
						m = this.templateParamDao.getMap(formattedSql);

					if (m == null || m.isEmpty())
						// 继续循环
						continue;
					if (mapParams == null || mapParams.isEmpty()) {
						mapParams = m;
					} else {
						Set<String> tempSet = m.keySet();
						for (String key : tempSet)
							mapParams.put(key, m.get(key));
					}
				} else if ("List<Object>".equals(type)
						|| "Object[]".equals(type) || "Json2List".equals(type)
						|| "Json2Array".equals(type)) {
					List<Object> list;
					if (type.equals("Json2List") || type.equals("Json2Array")) {
						list = (List<Object>) JsonUtils
								.toCollection(this.templateParamDao
										.getJsonsString(formattedSql));
					} else
						list = this.templateParamDao
								.getListIncludeObject(formattedSql);
					if (list.size() == 0)
						continue;
					if (mapParams == null || mapParams.isEmpty())
						mapParams = new HashMap<String, Object>();
					mapParams.put(jo.get("key").toString(), list);
				} else if ("List<Map<String,Object>>".equals(type)
						|| "Json2ListMap".equals(type)) {
					List<Map<String, Object>> list;
					if ("Json2ListMap".equals(type)) {
						List<Object> lo = (List<Object>) JsonUtils
								.toCollection(this.templateParamDao
										.getJsonsString(formattedSql));
						list = new ArrayList<Map<String, Object>>();
						for (Object o : lo) {
							list.add((Map<String, Object>) o);
						}
					} else
						list = this.templateParamDao
								.getListIncludeMap(formattedSql);

					if (list.size() == 0)
						continue;
					if (mapParams == null || mapParams.isEmpty())
						mapParams = new HashMap<String, Object>();
					mapParams.put(jo.get("key").toString(), list);
				} else if ("List<list<Object>>".equals(type)
						|| "List<Object[]>".equals(type)) {
					List<List<Object>> list = this.templateParamDao
							.getListIncludeList(formattedSql);
					if (list.size() == 0)
						continue;
					if (mapParams == null || mapParams.isEmpty())
						mapParams = new HashMap<String, Object>();
					mapParams.put(jo.get("key").toString(), list);
				} else if ("spel".equals(type)) {//
					if (expressionParser == null)
						expressionParser = new SpelExpressionParser();
					Expression exp = expressionParser
							.parseExpression(formattedSql);
					StandardEvaluationContext context = new StandardEvaluationContext();
					context.setBeanResolver(new BeanFactoryResolver(
							this.applicationContext));
					context.setVariables(mapFormatSql);
					try {
						Map<String, Object> map = (Map<String, Object>) exp
								.getValue(context);
						if (map != null) {
							if (mapParams == null)
								mapParams = new HashMap<String, Object>();
							mapParams.putAll(map);
						}
					} catch (EvaluationException e) {
						logger.warn(e.getMessage());
						throw e;
					}
				} else {
					throw new CoreException("unsupport type:" + type);
				}
			}
		} catch (Exception e) {
			logger.warn(e);
			throw new CoreException(e.getMessage(), e);
		}
		return mapParams;
	}

}
