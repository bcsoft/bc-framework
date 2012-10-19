package cn.bc.netdisk.web.struts2;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.service.NetdiskFileService;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

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
	private NetdiskFileService netdiskFileService;
	public String fileInfo;// 文件信息

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

	// 共享
	public String share() {
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

	public String json;

	// 上传文件
	public String uploadfile() {
		Json json = new Json();
		NetdiskFile netdiskFile = new NetdiskFile();
		// 文件信息
		if (this.fileInfo != null && this.fileInfo.length() > 0) {
			JSONArray jsons;
			try {
				jsons = new JSONArray(this.fileInfo);
				JSONObject json1;
				for (int i = 0; i < jsons.length(); i++) {
					json1 = jsons.getJSONObject(i);
					netdiskFile.setName(json1.getString("name"));
					netdiskFile.setSize(json1.getLong("size"));
					netdiskFile.setPath(json1.getString("path"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		netdiskFile.setStatus(BCConstants.STATUS_ENABLED);
		SystemContext context = this.getSystyemContext();
		// 设置创建人信息
		netdiskFile.setFileDate(Calendar.getInstance());
		netdiskFile.setAuthor(context.getUserHistory());
		this.netdiskFileService.save(netdiskFile);
		json.put("success", true);
		json.put("msg", "上传成功！");

		this.json = json.toString();
		return "json";

	}
}
