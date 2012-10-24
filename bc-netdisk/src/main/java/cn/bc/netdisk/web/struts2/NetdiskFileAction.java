package cn.bc.netdisk.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
	public String dialogType;// 新建对话框的类型
	public String title;// 文件名
	public String order;// 排序号
	public String pid;// 所属文件夹Id
	public String folder;// 所属文件夹名

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

	@Override
	protected void afterCreate(NetdiskFile entity) {
		super.afterCreate(entity);
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setType(NetdiskFile.TYPE_FOLDER);
		entity.setSize(new Long(0));

	}

	public String createDialog() {
		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);

		if (dialogType.equals("zhengliwenjian")) {
			return "zhengliwenjian";
		} else {
			return "gongxiang";
		}

	}

	// 共享
	public String share() {
		// 初始化E
		this.setE(createEntity());
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
	Json jsonObject = new Json();

	// 上传文件
	public String uploadfile() {
		NetdiskFile netdiskFile = new NetdiskFile();
		// 文件信息
		if (this.fileInfo != null && this.fileInfo.length() > 0) {
			JSONArray jsons;
			try {
				jsons = new JSONArray(this.fileInfo);
				JSONObject json1;
				for (int i = 0; i < jsons.length(); i++) {
					json1 = jsons.getJSONObject(i);
					String name = json1.getString("name");
					netdiskFile.setName(name);
					netdiskFile.setSize(json1.getLong("size"));
					netdiskFile.setPath(json1.getString("path"));
					netdiskFile.setExt(name.substring(name.lastIndexOf(".")));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		netdiskFile.setStatus(BCConstants.STATUS_ENABLED);
		netdiskFile.setType(NetdiskFile.TYPE_FILE);
		SystemContext context = this.getSystyemContext();
		// 设置创建人信息
		netdiskFile.setFileDate(Calendar.getInstance());
		netdiskFile.setAuthor(context.getUserHistory());
		this.netdiskFileService.save(netdiskFile);
		jsonObject.put("success", true);
		jsonObject.put("msg", "上传成功！");
		this.json = jsonObject.toString();
		return "json";

	}

	// 整理
	public String clearUp() {
		Map<String, Object> updateInfo = new HashMap<String, Object>();
		updateInfo.put("pid", new Long(pid));
		updateInfo.put("orderNo", order);
		updateInfo.put("name", title);
		this.netdiskFileService.update(this.getId(), updateInfo);
		jsonObject.put("success", true);
		jsonObject.put("msg", "保存成功！");
		this.json = jsonObject.toString();
		return "json";
	}
}
