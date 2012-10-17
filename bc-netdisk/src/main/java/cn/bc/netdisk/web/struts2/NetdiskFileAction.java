package cn.bc.netdisk.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.service.NetdiskFileService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 网络文件Action
 * 
 * @author zxr
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class NetdiskFileAction extends FileEntityAction<Long, NetdiskFile> {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private NetdiskFileService netdiskFileService;

	@Autowired
	public void setNetdiskFileService(NetdiskFileService netdiskFileService) {
		this.setCrudService(netdiskFileService);
		this.netdiskFileService = netdiskFileService;
	}

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(150)
				.setMinHeight(100).setMinWidth(350).setHeight(200);
	}

	// 整理
	public String clearUp() {
		// 初始化E
		this.setE(createEntity());
		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);
		// 初始化表单的其他配置
		try {
			this.initForm(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.afterCreate(this.getE());
		return "form";

	}
}
