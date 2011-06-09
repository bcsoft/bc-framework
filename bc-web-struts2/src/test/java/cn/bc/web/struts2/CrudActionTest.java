package cn.bc.web.struts2;

import java.util.Map;

import org.apache.struts2.StrutsSpringTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import cn.bc.core.service.CrudService;
import cn.bc.test.Example;
import cn.bc.test.mock.CrudServiceMock;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ResultConfig;

public class CrudActionTest extends StrutsSpringTestCase {
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
	public void testGetActionMapping() {
		ActionMapping mapping = getActionMapping("/crud");
		assertNotNull(mapping);
		assertEquals("/", mapping.getNamespace());
		assertEquals("crud", mapping.getName());
	}

	// 测试Action配置文件是否正确
	public void testExecute() throws Exception {
		//在调用getActionProxy方法前设置请求的参数
		request.setParameter("id", "1");
		
		// 测试ActionProxy的配置
		ActionProxy proxy = getActionProxy("/crud");
		assertNotNull(proxy);
		assertEquals("/", proxy.getNamespace());
		assertEquals("crud", proxy.getActionName());
		assertEquals("execute", proxy.getMethod());
		assertEquals("default", proxy.getConfig().getPackageName());

		Map<String, ResultConfig> results = proxy.getConfig().getResults();
		assertNotNull(results);

		// 测试result的配置
		assertTrue("No 'success' result defined for action 'example'",
				results.containsKey("success"));
		assertTrue("No 'open' result defined for action 'crud'",
				results.containsKey("open"));
		assertEquals("/crud/Open.jsp", results.get("open").getParams()
				.get("location"));
		assertTrue("No 'edit' result defined for action 'crud'",
				results.containsKey("edit"));
		assertTrue("No 'save' result defined for action 'crud'",
				results.containsKey("save"));
		assertTrue("No 'delete' result defined for action 'crud'",
				results.containsKey("delete"));
		assertTrue("No 'view' result defined for action 'crud'",
				results.containsKey("view"));
		
		//测试配置文件的位置
		assertTrue(
				"Not config from '...classes/struts.xml'",
				results.get("open").getLocation().getURI()
						.endsWith("classes/struts.xml"));

		// 测试action中的service是否通过spring注入了
		@SuppressWarnings("unchecked")
		StrutsCRUDAction<Example> action = (StrutsCRUDAction<Example>) proxy
				.getAction();
		assertNotNull(action);
		assertNotNull(action.getCrudService());
		assertTrue(action.getCrudService() instanceof CrudServiceMock);

		//action未执行前参数值应未设置
		assertNull(action.getId());
		
		//运行action并检验返回值
		String result = proxy.execute();
		assertEquals(Action.SUCCESS, result);

		//action执行后检验参数的值
		assertEquals("1", action.getId());
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
		ActionProxy proxy = getActionProxy("/crud!open.action");
		assertNotNull(proxy);
		assertEquals("open", proxy.getMethod());

		// 测试action中的service是否通过spring注入了
		@SuppressWarnings("unchecked")
		StrutsCRUDAction<Example> action = (StrutsCRUDAction<Example>) proxy.getAction();
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
