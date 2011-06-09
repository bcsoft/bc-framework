/**
 * 
 */
package cn.bc.web.struts2;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import cn.bc.core.SetEntityClass;
import cn.bc.core.service.CrudService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 必须通过spring进行配置才能实现注入entityClass
 * 
 * @author dragon
 * 
 * @param <T>
 */
public class StrutsCRUDAction<T extends Object> extends ActionSupport implements
		InitializingBean, SetEntityClass<T> {
	private static final long serialVersionUID = -1428717283670421814L;
	protected final Log logger = LogFactory.getLog(getClass());
	private CrudService<T> crudService;
	private Class<T> entityClass;
	private T entity;
	private List<T> entities;
	private String id;
	private boolean readOnly;

	public List<T> getEntities() {
		return entities;
	}

	public void setEntities(List<T> entities) {
		this.entities = entities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected Serializable parseId(String id) {
		return new Long(id);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
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
							+ this.getEntityClass() + "'");
				// 补充设置crudService的entityClass
				((SetEntityClass<T>) this.crudService)
						.setEntityClass(this.getEntityClass());
			}
			if (this.crudService.getEntityClass() == null) {
				logger.error("crudService's entityClass is still null!");
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

	public String execute() {
		return SUCCESS;
	}

	public String open() {
		Serializable id = parseId(this.getId());
		this.entity = this.crudService.load(id);
		return "open";
	}

	public String edit() {
		if(this.getId() == null || this.getId().length() == 0){
			this.setEntity(this.create());
		}
		return "edit";
	}

	protected T create() {
		return this.crudService.create();
	}

	public String save() {
		return "save";
	}

	public void validate() {
		//定义外在的bean来进行检验
	}

	public String delete() {
		return "delete";
	}

	public String display() {
		logger.debug("display...");
		this.setEntities(this.crudService.createQuery().list());
		return "display";
	}

	public String view() {
		return "view";
	}
}
