package cn.bc.template.service;

import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.JsonUtils;
import cn.bc.core.util.TemplateUtils;
import cn.bc.template.dao.TemplateParamDao;
import cn.bc.template.domain.TemplateParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板参数Service接口的实现
 *
 * @author lbj
 */
public class TemplateParamServiceImpl extends DefaultCrudService<TemplateParam>
	implements TemplateParamService, ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(TemplateParamServiceImpl.class);

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

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapParams(TemplateParam templateParam,
	                                        Map<String, Object> mapFormatSql) {
		// 声明返回的map集合
		Map<String, Object> mapParams = new HashMap<String, Object>();

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

				// ---类型判断开始---
				if ("Map".equals(type)) {
					Map<String, Object> m = templateParamDao
						.getMap(formattedSql);
					if (m != null)
						mapParams.putAll(m);

				} else if ("Json2Map".equals(type)) {
					Map<String, Object> m = JsonUtils
						.toMap(this.templateParamDao
							.getJsonsString(formattedSql));
					if (m != null)
						mapParams.putAll(m);

				} else if ("List<Object>".equals(type)) {
					List<Object> list = templateParamDao
						.getListIncludeObject(formattedSql);
					if (list.size() > 0)
						mapParams.put(jo.get("key").toString(), list);

				} else if ("Object[]".equals(type)) {
					List<Object> list = templateParamDao
						.getListIncludeObject(formattedSql);
					if (list.size() > 0)
						mapParams.put(jo.get("key").toString(), list);

				} else if ("Json2List".equals(type)) {
					List<Object> list = (List<Object>) JsonUtils
						.toCollection(this.templateParamDao
							.getJsonsString(formattedSql));
					if (list.size() > 0)
						mapParams.put(jo.get("key").toString(), list);

				} else if ("Json2Array".equals(type)) {
					List<Object> list = (List<Object>) JsonUtils
						.toCollection(this.templateParamDao
							.getJsonsString(formattedSql));
					if (list.size() > 0)
						mapParams.put(jo.get("key").toString(), list);

				} else if ("List<Map<String,Object>>".equals(type)) {
					List<Map<String, Object>> list = templateParamDao
						.getListIncludeMap(formattedSql);
					if (list.size() > 0)
						mapParams.put(jo.get("key").toString(), list);

				} else if ("Json2ListMap".equals(type)) {
					List<Object> lo = (List<Object>) JsonUtils
						.toCollection(this.templateParamDao
							.getJsonsString(formattedSql));
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for (Object o : lo) {
						list.add((Map<String, Object>) o);
					}
					if (list.size() > 0)
						mapParams.put(jo.get("key").toString(), list);

				} else if ("spel".equals(type)) {
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
				// ---类型判断结束---
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			throw new CoreException(e.getMessage(), e);
		}
		return mapParams;
	}

}
