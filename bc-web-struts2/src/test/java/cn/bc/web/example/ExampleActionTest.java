package cn.bc.web.example;

import java.util.Map;

import org.apache.struts2.StrutsSpringTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import cn.bc.core.service.CrudService;
import cn.bc.test.Example;
import cn.bc.test.mock.CrudServiceMock;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

public class ExampleActionTest extends StrutsSpringTestCase {
	private CrudService<Example> crudService;

	@Override
	protected String getContextLocations() {
		return "classpath:applicationContext-test.xml";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.crudService = (CrudService<Example>) applicationContext
				.getBean("crudService");
		assertNotNull(this.crudService);
	}

	// 测试Action配置文件是否正确
	public void testGetActionMappingWithoutMethod() {
		ActionMapping mapping = getActionMapping("/example");
		assertNotNull(mapping);
		assertEquals("/", mapping.getNamespace());
		assertEquals("example", mapping.getName());
		assertNull(mapping.getMethod());
		assertNull(mapping.getExtension());
		assertNull(mapping.getParams());
		assertNull(mapping.getResult());
	}

	// 测试Action配置文件是否正确
	public void testGetActionMappingWithMethod() {
		ActionMapping mapping = getActionMapping("/example!open");
		assertNotNull(mapping);
		assertEquals("/", mapping.getNamespace());
		assertEquals("example", mapping.getName());
		assertEquals("open", mapping.getMethod());
		assertNull(mapping.getExtension());
		assertNull(mapping.getParams());
		assertNull(mapping.getResult());
	}

	// 测试Action的运行
	public void testExecute() throws Exception {
		//在调用getActionProxy方法前设置请求的参数
		request.setParameter("id", "1");
		request.setParameter("name", "dragon");
		
		// 测试ActionProxy的配置
		// ActionProxy proxy = getActionProxy("/example");
		ActionProxy proxy = getActionProxy("/example.action");
		assertNotNull(proxy);
		assertEquals("/", proxy.getNamespace());
		assertEquals("example", proxy.getActionName());
		assertEquals("execute", proxy.getMethod());
		assertEquals(true, proxy.getExecuteResult());
		
		// 测试ActionConfig的配置
		ActionConfig cfg = proxy.getConfig();
		assertNotNull(cfg);
		assertEquals("example", cfg.getName());
		assertEquals("example", cfg.getClassName());
		assertEquals("default", cfg.getPackageName());

		Map<String, ResultConfig> results = cfg.getResults();
		assertNotNull(results);

		// 测试success result的配置
		assertTrue("No 'success' result defined for action 'example'",
				results.containsKey("success"));
		assertEquals("/example/success.jsp", results.get("success").getParams()
				.get("location"));
		assertTrue(
				"Not config from '...classes/struts.xml'",
				results.get("success").getLocation().getURI()
						.endsWith("classes/struts.xml"));

		// 测试input result的配置
		assertTrue("No 'input' result defined for action 'example'",
				results.containsKey("input"));
		assertEquals("/example/input.jsp", results.get("input").getParams()
				.get("location"));
		assertTrue(
				"Not config from '...classes/struts.xml'",
				results.get("input").getLocation().getURI()
						.endsWith("classes/struts.xml"));

		// 测试action中的service是否通过spring注入了
		ExampleAction action = (ExampleAction) proxy.getAction();
		assertNotNull(action);
		assertNotNull(action.getCrudService());
		assertTrue(action.getCrudService() instanceof CrudServiceMock);

		//action未执行前参数值应未设置
		assertNull(action.getId());
		assertNull(action.getName());
		
		//运行action并检验返回值
		String result = proxy.execute();
		assertEquals(Action.SUCCESS, result);

		//action执行后检验参数的值
		assertEquals("1", action.getId());
		assertEquals("1|dragon", action.getName());
	}
	
	// 测试Action的运行
	public void testOpen() throws Exception {
		//保存一个对象
		Example e = new Example();
		this.crudService.save(e);
		Long id = e.getId();
		
		//在调用getActionProxy方法前设置请求的参数
		request.setParameter("id", id.toString());
		
		// 测试ActionProxy的配置
		// ActionProxy proxy = getActionProxy("/example");
		ActionProxy proxy = getActionProxy("/example!open.action");
		assertNotNull(proxy);
		assertEquals("open", proxy.getMethod());

		// 测试action中的service是否通过spring注入了
		ExampleAction action = (ExampleAction) proxy.getAction();
		assertNotNull(action);
		assertNotNull(action.getCrudService());
		
		//运行action并检验返回值
		String result = proxy.execute();
		assertEquals("open", result);

		//action执行后检验参数的值
		assertEquals(id.toString(), action.getId());
		assertNotNull(action.getEntity());
		assertEquals(id,action.getEntity().getId());
	}
}
