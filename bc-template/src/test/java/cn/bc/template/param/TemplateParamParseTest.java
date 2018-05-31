package cn.bc.template.param;

import cn.bc.core.util.TemplateUtils;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.template.dao.TemplateParamDao;
import cn.bc.template.domain.TemplateParam;
import cn.bc.template.service.TemplateParamService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//模板参数json数据解释测试
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class TemplateParamParseTest {

	private String config = "[{type:\"Map\",sql:\"select a.company as aa , a.code as code from bs_car a where a.id=${cid}\"}" +
		",{type:\"List<Object>\",sql:\"select a.company from bs_car a where a.factory_type='${factoryType}'\",key:\"key\"}" +
		",{type:\"List<Map<String,Object>>\",sql:\"select a.company as key1,a.code as key2 from bs_car a where a.factory_type='${factoryType}'\",key:\"key\"}" +
		",{type:\"List<Object[]>\",sql:\"select a.company as key1,a.plate_no as key2 from bs_car a where a.factory_type='${factoryType}'\",key:\"key\"}]";

	private Map<String, Object> mapFormatSql;

	TemplateParamDao templateParamDao;
	ActorHistoryService actorHistoryService;
	TemplateParamService templateParamService;

	@Autowired
	public void setTemplateParamDao(TemplateParamDao templateParamDao) {
		this.templateParamDao = templateParamDao;
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Autowired
	public void setTemplateParamService(TemplateParamService templateParamService) {
		this.templateParamService = templateParamService;
		//this.crudOperations = templateParamService;
	}


	private void init() {
		mapFormatSql = new HashMap<String, Object>();
		mapFormatSql.put("cid", 100052580);
		mapFormatSql.put("factoryType", "起亚D");

	}


	protected TemplateParam createInstance() {
		TemplateParam tp = new TemplateParam();
		tp.setName("模板参数测试1");
		tp.setFileDate(Calendar.getInstance());
		tp.setAuthor(actorHistoryService.loadByCode("admin"));
		tp.setConfig(config);
		return tp;
	}

	@Test
	public void testParse() {
		init();
		TemplateParam tp = createInstance();
		Assert.assertEquals(tp.getConfig(), config);
		Assert.assertNotNull(tp.getConfigJson());
		JSONArray ja = tp.getConfigJson();
		//Assert.assertEquals(ja.length(), 2);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				String type = jo.getString("type").toString();
				if (type.equals("Map")) {
					System.out.println("Map===开始===");
					String sql = jo.get("sql").toString();
					String formattedSql = TemplateUtils.format(sql, mapFormatSql);
					System.out.println(formattedSql);
					/*Map<String,Object> m = this.templateParamDao.getMap(formattedSql);
					if(m!=null){
						for(String key:m.keySet()){
							System.out.println("key="+key);
							System.out.println("value="+m.get(key));
						}
					}*/
					System.out.println("Map===结束===");
				} else if (type.equals("List<Object>")) {
					System.out.println("List<Object>===开始===");
					String sql = jo.get("sql").toString();
					String formattedSql = TemplateUtils.format(sql, mapFormatSql);
					System.out.println(formattedSql);
				/*	List<Object> list = this.templateParamDao.getListIncludeObject(formattedSql);
					Assert.assertEquals(list.size(), 0);
					for(Object key:list){
						System.out.println("value="+key);
					}*/
					System.out.println("List<Object>===结束===");
				} else if (type.equals("List<Map<String,Object>>")) {
					System.out.println("List<Map<String,Object>>===开始===");
					String sql = jo.get("sql").toString();
					String formattedSql = TemplateUtils.format(sql, mapFormatSql);
					System.out.println(formattedSql);
					List<Map<String, Object>> lm = this.templateParamDao.getListIncludeMap(formattedSql);
					Assert.assertEquals(lm.size(), 0);
					for (Map<String, Object> m : lm) {
						String printStr = "";
						for (String key : m.keySet())
							printStr += "{key=" + key + "," + "value=" + m.get(key) + "} ";

						System.out.println("[" + printStr + "]");
					}
					System.out.println("List<Map<String,Object>>===结束===");
				} else if (type.equals("List<Object[]>")) {
					System.out.println("List<Object[]>===开始===");
					String sql = jo.get("sql").toString();
					String formattedSql = TemplateUtils.format(sql, mapFormatSql);
					System.out.println(formattedSql);
					List<List<Object>> lm = this.templateParamDao.getListIncludeList(formattedSql);
					Assert.assertEquals(lm.size(), 0);
					for (List<Object> oa : lm) {
						String printStr = "";
						for (Object o : oa)
							printStr += "value=" + o.toString() + ",";

						System.out.println("[" + printStr + "]");
					}
					System.out.println("List<Object[]>===结束===");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}