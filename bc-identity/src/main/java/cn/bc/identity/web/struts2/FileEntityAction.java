/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.io.Serializable;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.Context;
import cn.bc.identity.domain.FileEntity;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;

/**
 * FileEntity的CRUD通用Action的基类
 * 
 * @author dragon
 * 
 */
public class FileEntityAction<K extends Serializable, E extends FileEntity<K>>
		extends cn.bc.web.struts2.EntityAction<K, E> {
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务

	public IdGeneratorService getIdGeneratorService() {
		return idGeneratorService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public SystemContext getSystyemContext() {
		return (SystemContext) this.session.get(Context.KEY);
	}

	@Override
	protected void afterCreate(E entity) {
		super.afterCreate(entity);

		SystemContext context = this.getSystyemContext();

		// 设置创建人信息
		entity.setFileDate(Calendar.getInstance());
		entity.setAuthor(context.getUserHistory());
	}

	@Override
	protected void beforeSave(E entity) {
		super.beforeSave(entity);

		SystemContext context = this.getSystyemContext();

		// 设置最后更新人的信息
		entity.setModifier(context.getUserHistory());
		entity.setModifiedDate(Calendar.getInstance());
	}
}