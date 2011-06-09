package cn.bc.web.struts;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.action.ActionForm;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import cn.bc.core.Page;
import cn.bc.core.service.CrudService;
import cn.bc.test.Example;

import servletunit.HttpServletResponseSimulator;
import servletunit.struts.MockStrutsTestCase;

public class StrutsCRUDActionTest extends MockStrutsTestCase {
	private CrudService<Example> crudService;

	public StrutsCRUDActionTest(String testName) {
		super(testName);
		// NOTE: By default, the Struts ActionServlet will look for the file
		// WEB-INF/struts-config.xml, so you must place the directory that
		// contains WEB-INF in your CLASSPATH. If you would like to use an
		// alternate configuration file, please see the setConfigFile() method
		// for details on how this file is located.

		// org.springframework.web.context.support.org.springframework.web.context.support.ROOT
		// XmlWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
		// ContextLoaderPlugIn.SERVLET_CONTEXT_PREFIX;
		// new ModuleConfig();
	}

	@SuppressWarnings( { "unchecked", "deprecation" })
	protected void setUp() throws Exception {
		super.setUp();

		// 获取crudService的实现:只适用于没有使用struts的module功能
		ApplicationContext wac = (ApplicationContext) getActionServlet()
				.getServletContext()
				.getAttribute(
						org.springframework.web.struts.ContextLoaderPlugIn.SERVLET_CONTEXT_PREFIX);

		// checked this for future change in spring 3
		Assert.assertEquals(XmlWebApplicationContext.class, wac.getClass());

		crudService = (CrudService<Example>) wac.getBean("crudService");
		Assert.assertNotNull(crudService);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		crudService = null;
	}

	public void testOpenSuccess() {
		// 保存一条记录
		Example entity = new Example("dragon");
		crudService.save(entity);
		Long id = entity.getId();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);

		// 打开
		setRequestPathInfo("/example");
		addRequestParameter("method", "open");
		addRequestParameter("id", id.toString());
		addRequestParameter("name", "newName");// 可以注释掉
		actionPerform();

		// 验证
		verifyForward("ExampleForm");
		verifyNoActionErrors();
		ActionForm form = this.getActionForm();
		Assert.assertNotNull(form);
		Assert.assertTrue(form instanceof DynaBean);
		DynaBean bean = (DynaBean) form;
		Assert.assertEquals(id, bean.get("id"));
		Assert.assertEquals("dragon", bean.get("name"));// 保证name没有被请求参数改变
		Assert.assertEquals(true, getRequest().getAttribute("readOnly"));
	}

	public void testOpenNotExist() {
		// 打开不存在的记录
		setRequestPathInfo("/example");
		addRequestParameter("method", "open");
		addRequestParameter("id", Integer.MAX_VALUE + "");
		actionPerform();

		// 验证
		verifyForward("error");
		verifyActionErrors(new String[] { "error.entity.notexist" });
		Assert.assertEquals(true, getRequest().getAttribute("readOnly"));
	}

	public void testEditSuccess() {
		// 保存一条记录
		Example entity = new Example("dragon");
		crudService.save(entity);
		Long id = entity.getId();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);

		// 打开
		setRequestPathInfo("/example");
		addRequestParameter("method", "edit");
		addRequestParameter("id", id.toString());
		addRequestParameter("name", "newName");
		actionPerform();

		// 验证
		verifyForward("ExampleForm");
		verifyNoActionErrors();
		ActionForm form = this.getActionForm();
		Assert.assertNotNull(form);
		Assert.assertTrue(form instanceof DynaBean);
		DynaBean bean = (DynaBean) form;
		Assert.assertEquals(id, bean.get("id"));
		Assert.assertEquals("dragon", bean.get("name"));// 保证name没有被请求参数改变
		Assert.assertEquals(false, getRequest().getAttribute("readOnly"));
	}

	public void testEditFailed_notExist() {
		// 打开不存在的记录
		setRequestPathInfo("/example");
		addRequestParameter("method", "edit");
		addRequestParameter("id", Integer.MAX_VALUE + "");
		actionPerform();

		// 验证
		verifyForward("error");
		verifyActionErrors(new String[] { "error.entity.notexist" });
		Assert.assertEquals(false, getRequest().getAttribute("readOnly"));
	}

	public void testSaveNewSuccess() throws IOException {
		// 保存新纪录
		setRequestPathInfo("/example");
		addRequestParameter("method", "save");
		addRequestParameter("id", "");// or 0、-1
		addRequestParameter("name", "newName");
		actionPerform();

		// 验证
		verifyForward(null);
		verifyNoActionErrors();

		String actual = ((HttpServletResponseSimulator) getResponse())
				.getWriterBuffer().toString();
		Assert.assertEquals(
				"{\"success\":true,\"id\":\"1\",\"msg\":\"save success!\"}",
				actual);
	}

	public void testSaveExistSuccess() throws IOException {
		// 保存一条记录
		Example entity = new Example("dragon");
		crudService.save(entity);
		Long id = entity.getId();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);

		// 保存纪录
		setRequestPathInfo("/example");
		addRequestParameter("method", "save");
		addRequestParameter("id", id.toString());
		addRequestParameter("name", "newName");
		addRequestParameter("code", entity.getCode());
		actionPerform();

		// 验证
		verifyForward(null);
		verifyNoActionErrors();

		String actual = ((HttpServletResponseSimulator) getResponse())
				.getWriterBuffer().toString();
		Assert.assertEquals("{\"success\":true,\"id\":\"" + id.toString()
				+ "\",\"msg\":\"save success!\"}", actual);

		entity = crudService.load(id);
		Assert.assertEquals("newName", entity.getName());
	}

	public void testDeleteNotExist() throws IOException {
		setRequestPathInfo("/example");
		addRequestParameter("method", "delete");
		addRequestParameter("id", Integer.MAX_VALUE + "");
		actionPerform();

		// 验证
		verifyForward(null);
		verifyNoActionErrors();

		String actual = ((HttpServletResponseSimulator) getResponse())
				.getWriterBuffer().toString();
		Assert.assertEquals("{\"success\":true,\"msg\":\"delete success!\"}",
				actual);
	}

	public void testDeleteSuccess() throws IOException {
		// 保存一条记录
		Example entity = new Example("dragon");
		crudService.save(entity);
		Long id = entity.getId();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);

		// do action
		setRequestPathInfo("/example");
		addRequestParameter("method", "delete");
		addRequestParameter("id", id.toString());
		actionPerform();

		// 验证
		verifyForward(null);
		verifyNoActionErrors();

		String actual = ((HttpServletResponseSimulator) getResponse())
				.getWriterBuffer().toString();
		Assert.assertEquals("{\"success\":true,\"msg\":\"delete success!\"}",
				actual);

		entity = crudService.load(id);
		Assert.assertNull(entity);
	}

	public void testDeleteMulSuccess() throws IOException {
		// 保存2条记录
		Example entity = new Example("dragon1");
		crudService.save(entity);
		Long id1 = entity.getId();
		Assert.assertNotNull(id1);
		Assert.assertTrue(id1 > 0);
		
		entity = new Example("dragon2");
		crudService.save(entity);
		Long id2 = entity.getId();
		Assert.assertNotNull(id2);
		Assert.assertTrue(id2 > 0);

		// do action
		setRequestPathInfo("/example");
		addRequestParameter("method", "delete");
		addRequestParameter("id", id1.toString() + "," + id2.toString());
		actionPerform();

		// 验证
		verifyForward(null);
		verifyNoActionErrors();

		String actual = ((HttpServletResponseSimulator) getResponse())
				.getWriterBuffer().toString();
		Assert.assertEquals("{\"success\":true,\"msg\":\"delete success!\"}",
				actual);

		entity = crudService.load(id1);
		Assert.assertNull(entity);
		entity = crudService.load(id2);
		Assert.assertNull(entity);
	}

	@SuppressWarnings("unchecked")
	public void testViewEmptySuccess() throws IOException {
		// do action
		setRequestPathInfo("/example");
		addRequestParameter("method", "view");
		actionPerform();

		// 验证
		verifyForward("ExampleView");
		verifyNoActionErrors();
		Page<Example> page = (Page<Example>)getRequest().getAttribute("page");
		Assert.assertNotNull(page);
	}

	@SuppressWarnings("unchecked")
	public void testViewNotEmptySuccess() throws IOException {
		// 保存一条记录
		Example entity = new Example("dragon");
		crudService.save(entity);
		Long id = entity.getId();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);

		// do action
		setRequestPathInfo("/example");
		addRequestParameter("method", "view");
		actionPerform();

		// 验证
		verifyForward("ExampleView");
		verifyNoActionErrors();
		Page<Example> page = (Page<Example>)getRequest().getAttribute("page");
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getData());
		Assert.assertEquals(1, page.getData().size());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(50, page.getPageSize());
		Assert.assertEquals(1, page.getPageCount());
		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());
	}
}
