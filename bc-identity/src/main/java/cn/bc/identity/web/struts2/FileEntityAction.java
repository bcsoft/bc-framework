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
	private IdGeneratorService idGeneratorService;//用于生成uid的服务

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
	public String create() throws Exception {
		SystemContext context = this.getSystyemContext();
		this.readonly = false;
		
		//初始化E
		this.setE(this.getCrudService().create());
		
		//初始化表单的配置信息
		this.formPageOption = buildFormPageOption();
		
		//设置创建人信息
		E e = this.getE();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUserHistory());
		
		return "form";
	}

	@Override
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		E e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		return super.save();
	}
}