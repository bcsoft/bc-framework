/**
 * 
 */
package cn.bc.web.struts;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;


import cn.bc.core.Page;
import cn.bc.core.SetEntityClass;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;
import cn.bc.web.WebCondition;
import cn.bc.web.struts.beanutils.ConverterUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 必须通过spring进行配置才能实现注入entityClass
 * 
 * @author dragon
 * 
 */
/**
 * @author dragon
 * 
 * @param <T>
 */
public class StrutsCRUDAction<T extends Object> extends DispatchAction
		implements InitializingBean, SetEntityClass<T> {
	protected final Log logger = LogFactory.getLog(getClass());
	private CrudService<T> crudService;
	private Class<T> entityClass;

	static {
		// 全局注册默认的转换器
		ConverterUtils.registDefault();
	}

	@Autowired
	@Required
	public void setCrudService(CrudService<T> crudService) {
		this.crudService = crudService;
	}

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		if (this.crudService.getEntityClass() == null) {
			if (this.crudService instanceof SetEntityClass) {
				if (logger.isDebugEnabled())
					logger.debug("reset crudService's entityClass to '"
							+ entityClass + "'");
				// 补充设置crudService的entityClass
				((SetEntityClass<T>) this.crudService)
						.setEntityClass(entityClass);
			}
		}
	}

	protected Class<T> getEntityClass() {
		return this.entityClass;
	}

	public void setEntityClass(Class<T> clazz) {
		this.entityClass = clazz;
	}

	/**
	 * @return service接口的实现
	 */
	public CrudService<T> getCrudService() {
		return this.crudService;
	}

	// CRUD's CU - 保存新创建的或被修改的对象
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			// 构建对象
			T entity = this.buildEntity(request, form);

			// 保存
			this.getCrudService().save(entity);

			// 返回结果
			response.getWriter().print(
					"{\"success\":true,\"id\":\"" + this.getEntityId(entity)
							+ "\",\"msg\":\"save success!\"}");
		} catch (Exception e) {
			if (logger.isInfoEnabled())
				logger.info(e.getMessage(), e);
			response.getWriter().print(
					"{\"success\":false,\"msg\":\"" + e.getMessage() + "\"}");
		}
		return null;
	}

	// 获取对象ID的字符串表示:通过反射获取方法getId()的返回值
	protected Serializable getEntityId(T entity) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (entity == null)
			return null;
		Method method = entity.getClass().getMethod("getId", (Class[]) null);
		if (method == null)
			return null;
		else
			return (Serializable) method.invoke(entity, (Object[]) null);
	}

	// 从请求中包含的信息构造出实例对象
	protected T buildEntity(HttpServletRequest request, ActionForm form)
			throws IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Serializable id = getEntityId(request, form);
		T entity;
		if (id != null) {
			entity = this.getCrudService().load(id);
			if (entity == null) {
				throw new CoreException("no entity exist with id '" + id + "'");
			}
		} else {
			entity = newEntityInstance(request);
		}

		BeanUtils.copyProperties(entity, form);// 将form信息复制到entity
		return entity;
	}

	// 创建实体对象的新实例
	protected T newEntityInstance(HttpServletRequest request)
			throws InstantiationException, IllegalAccessException {
		return getEntityClass().newInstance();
	}

	/**
	 * 从请求中获取实体id的值
	 * 
	 * @param request
	 * @return
	 */
	protected Serializable getEntityId(HttpServletRequest request,
			ActionForm form) {
		String id = request.getParameter("id");
		return parseId(id);
	}

	/**
	 * 解析请求参数中的id值，如果没有就默认为null，默认情况下自动转换为Long类型， 项目实际情况不一样时需要复写该方法转换为适当的主键类型
	 * 
	 * @param id
	 * @return
	 */
	protected Serializable parseId(String id) {
		return (StringUtils.hasText(id) && !"0".equals(id) && !"-1".equals(id)) ? new Long(
				id)
				: null;
	}

	// CRUD's U - 更新对象的部分信息
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			Serializable[] ids = this.getEntityIds(request);
			Map<String, Object> attributes = this.getUpdateAttributes(request,
					form);
			this.getCrudService().update(ids, attributes);
			response.getWriter().print("{\"success\":true}");
		} catch (Exception e) {
			response.getWriter().print(
					"{\"success\":false,\"msg\":\"" + e.getMessage() + "\"}");
		}
		return null;
	}

	// 获取要更新的属性
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getUpdateAttributes(
			HttpServletRequest request, ActionForm form) {
		DynaActionForm dynaBean = (DynaActionForm) form;
		return (Map<String, Object>) dynaBean.getMap();
	}

	// CRUD's C - 新建
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		T entity = newEntityInstance(request);

		// 将对象信息复制到form
		if (entity != null) {
			BeanUtils.copyProperties(form, entity);
		} else {
			throw new CoreException("faild to init entity.");
		}

		// 返回页面
		request.setAttribute("readOnly", false);
		String forwardName = getEntityClass().getSimpleName() + "Form";
		return mapping.findForward(forwardName);
	}

	// CRUD's R - 只读表单
	public ActionForward open(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return this.gotoFormPage(mapping, form, request, response, true);
	}

	// CRUD's U - 可编辑表单
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return this.gotoFormPage(mapping, form, request, response, false);
	}

	// CRUD's RU - 表单
	protected ActionForward gotoFormPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, boolean readOnly) throws Exception {
		request.setAttribute("readOnly", readOnly);
		Serializable id = getEntityId(request, form);
		T entity = this.getCrudService().load(id);

		// 将对象信息复制到form
		if (entity != null) {
			BeanUtils.copyProperties(form, entity);
		} else {
			ActionErrors errors = new ActionErrors();
			errors.add("password",new ActionMessage("error.entity.notexist",id));
			saveErrors(request,errors);
            return mapping.findForward("error");
			//throw new NotExistsException("no entity exist with id '" + id + "'");
		}

		// 返回页面
		String forwardName = getEntityClass().getSimpleName() + "Form";
		return mapping.findForward(forwardName);
	}

	// CRUD's R - 视图
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 判断是否要直接获取视图数据（默认获取）
		if (!"false".equals(request.getParameter("preload"))) {
			int pageNo = ServletRequestUtils.getIntParameter(request,
					"pageNo", 1);
			int pageSize = ServletRequestUtils.getIntParameter(request,
					"pageSize", getDefaultPageSize());
			Page<T> page = this.getCrudService().createQuery().condition(
					this.getQueryCondition(request)).page(pageNo,
					pageSize);
			request.setAttribute("page", page);

		}

		// 返回页面
		String forwardName = getEntityClass().getSimpleName() + "View";// 如:"UserView"
		return mapping.findForward(forwardName);
	}

	/**
	 * 默认的每页数据量限制(默认为50条)
	 * @return
	 */
	protected int getDefaultPageSize() {
		return 50;
	}

	// CRUD's R - ajax获取视图数据
	public ActionForward viewData(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int pageNo = ServletRequestUtils.getIntParameter(request,
				"pageNo", 1);
		int pageSize = ServletRequestUtils.getIntParameter(request,
				"pageSize", getDefaultPageSize());
		Page<T> page = this.getCrudService().createQuery().condition(
				this.getQueryCondition(request)).page(pageNo,
				pageSize);

		response.getWriter().print(this.page2Json(request, page));
		return null;
	}

	protected Condition getQueryCondition(HttpServletRequest request) {
		String condition = request.getParameter("condition");
		if (condition != null && condition.length() > 0) {
			return new WebCondition(condition);
		} else {
			return null;
		}
	}

	protected String page2Json(HttpServletRequest request, Page<T> page) {
		Type type = new TypeToken<Page<T>>() {
		}.getType();
		return new Gson().toJson(page, type);
	}

	// CRUD's D - 删除对象
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			this.getCrudService().delete(this.getEntityIds(request));
			response.getWriter().print(
					"{\"success\":true,\"msg\":\"delete success!\"}");
		} catch (Exception e) {
			if (logger.isInfoEnabled())
				logger.info(e.getMessage(), e);
			response.getWriter().print(
					"{\"success\":false,\"msg\":\"" + e.getMessage() + "\"}");
		}
		return null;
	}

	// 支持url中通过id=[id1],[id2],...或id=[id1]&id=[id2]&...两种方式传入多个id参数
	protected Serializable[] getEntityIds(HttpServletRequest request) {
		String[] ids = request.getParameterValues("id");
		Collection<Serializable> newIds = new ArrayList<Serializable>();
		Serializable parseId;
		for (String id : ids) {
			if (id.indexOf(",") != -1) {
				String[] _ids = id.split(",");// id=[id1],[id2],...
				for (String _id : _ids) {
					parseId = parseId(_id);
					if (parseId != null)
						newIds.add(parseId);
				}
			} else {
				parseId = parseId(id);
				if (parseId != null)
					newIds.add(parseId);
			}
		}
		return newIds.toArray(new Serializable[0]);
	}
}
