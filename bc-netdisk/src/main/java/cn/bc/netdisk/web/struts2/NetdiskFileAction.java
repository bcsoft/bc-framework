package cn.bc.netdisk.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.domain.NetdiskShare;
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
	private ActorService actorService;
	public String fileInfo;// 文件信息
	public String dialogType;// 新建对话框的类型
	public String title;// 文件名
	public String order;// 排序号
	public String pid;// 所属文件夹Id
	public String folder;// 所属文件夹名
	public boolean isRelevanceDelete = false;// 是否删除文件夹下的所有文件
	public int editRole;// 共享设置
	public String visitors;// 访问者

	@Autowired
	public void setNetdiskFileService(NetdiskFileService netdiskFileService) {
		this.setCrudService(netdiskFileService);
		this.netdiskFileService = netdiskFileService;
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
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
		NetdiskFile e = this.netdiskFileService.load(this.getId());
		this.setE(e);
		if (dialogType.equals("zhengliwenjian")) {
			return "zhengliwenjian";
		} else {
			return "gongxiang";
		}

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
					if (name.indexOf(".") != -1) {
						netdiskFile
								.setExt(name.substring(name.lastIndexOf(".")));
					}
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

	// 上传文件夹
	public String uploadfolder() {
		// 文件夹信息

		if (this.fileInfo != null && this.fileInfo.length() > 0) {
			JSONArray jsons;
			try {
				jsons = new JSONArray(this.fileInfo);
				JSONObject json1;
				NetdiskFile pNetdiskFile = null;
				for (int i = 0; i < jsons.length(); i++) {
					json1 = jsons.getJSONObject(i);
					// 先判断是否有父级文件夹，如果没有就创建
					String relativePath = json1.getString("relativePath");
					// 如果是文件夹就去掉路径名的最后一个点
					if (json1.getBoolean("isDir")) {
						relativePath = relativePath.replace(".", "");
					}
					String[] ss = relativePath.split("/");
					// 再判断父级下是否有文件，如果没有就创建
					for (String s : ss) {
						NetdiskFile netdiskFile = new NetdiskFile();
						// 如果pNetdiskFile为空则为第一层目录
						// 如果fileNo与batchNo相等则证明是同一批的文件夹的文件
						if (pNetdiskFile == null) {
							NetdiskFile cNetdiskFile = this.netdiskFileService
									.findNetdiskFileByName(s, null,
											NetdiskFile.TYPE_FOLDER,
											json1.getString("batchNo"));
							// 如果不存在应该文件就新建
							if (cNetdiskFile == null) {
								netdiskFile.setName(s);
								netdiskFile
										.setStatus(BCConstants.STATUS_ENABLED);
								netdiskFile.setType(NetdiskFile.TYPE_FOLDER);
								netdiskFile.setSize(new Long(0));
								netdiskFile.setBatchNo(json1
										.getString("batchNo"));
								SystemContext context = this
										.getSystyemContext();
								// 设置创建人信息
								netdiskFile.setFileDate(Calendar.getInstance());
								netdiskFile.setAuthor(context.getUserHistory());
								pNetdiskFile = this.netdiskFileService
										.save(netdiskFile);

							} else {
								pNetdiskFile = cNetdiskFile;
							}
						} else {
							// 下一层目录
							// 判断同一级目录下是否存在相同文件
							NetdiskFile gNetdiskFile = this.netdiskFileService
									.findNetdiskFileByName(s,
											pNetdiskFile.getId(), null,
											pNetdiskFile.getBatchNo());
							// 如果gNetdiskFile等于空就创建新的文件夹
							if (gNetdiskFile == null) {
								netdiskFile.setName(s);
								netdiskFile
										.setStatus(BCConstants.STATUS_ENABLED);
								netdiskFile.setBatchNo(json1
										.getString("batchNo"));
								netdiskFile.setPid(pNetdiskFile.getId());
								// 判断是否为文件夹类型
								if (json1.getBoolean("isDir")) {
									netdiskFile
											.setType(NetdiskFile.TYPE_FOLDER);
									netdiskFile.setSize(new Long(0));
								} else {
									netdiskFile.setType(NetdiskFile.TYPE_FILE);
									if (s.indexOf(".") != -1) {
										netdiskFile.setExt(s.substring(s
												.lastIndexOf(".")));
									}
									netdiskFile.setSize(json1.getLong("size"));
									netdiskFile
											.setPath(json1.getString("path"));
								}
								SystemContext context = this
										.getSystyemContext();
								// 设置创建人信息
								netdiskFile.setFileDate(Calendar.getInstance());
								netdiskFile.setAuthor(context.getUserHistory());
								pNetdiskFile = this.netdiskFileService
										.save(netdiskFile);
							} else {
								pNetdiskFile = gNetdiskFile;
							}

						}
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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

	// 删除
	public String delete() {
		try {
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.netdiskFileService.delete(ids, isRelevanceDelete);
			} else {
				this.netdiskFileService.delete(this.getId(), isRelevanceDelete);
			}
			jsonObject.put("success", true);
			jsonObject.put("msg", "删除成功！");
		} catch (DataIntegrityViolationException e) {
			jsonObject.put("msg", getDeleteExceptionMsg(e));
			jsonObject.put("e", e.getClass().getSimpleName());
			jsonObject.put("success", false);
		}
		this.json = jsonObject.toString();
		return "json";
	}

	// 提示文件下存在子文件不能删除
	@Override
	protected String getDeleteExceptionMsg(Exception e) {
		//
		if (e instanceof DataIntegrityViolationException) {
			return "该文件夹下存在子文件！不能删除！";
		}
		return super.getDeleteExceptionMsg(e);
	}

	// 共享
	public String share() {
		// 获取原对象
		NetdiskFile netdiskFile = this.netdiskFileService.load(this.getId());
		// 整理访问者

		try {
			Set<NetdiskShare> netdiskShares = null;
			if (this.visitors != null && this.visitors.length() > 0) {
				netdiskShares = new LinkedHashSet<NetdiskShare>();
				NetdiskShare resource;
				JSONArray jsons = new JSONArray(this.visitors);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new NetdiskShare();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setOrderNo(i);
					resource.setNetdiskFile(netdiskFile);
					resource.setRole(json.getString("role"));
					resource.setAid(Long.valueOf(json.getString("aid")));
					netdiskShares.add(resource);
				}
			}
			if (netdiskFile.getFileVisitors() != null) {
				netdiskFile.getFileVisitors().clear();
				netdiskFile.getFileVisitors().addAll(netdiskShares);

			} else {
				netdiskFile.setFileVisitors(netdiskShares);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		// 共享设置
		netdiskFile.setEditRole(editRole);
		this.netdiskFileService.save(netdiskFile);
		jsonObject.put("success", true);
		jsonObject.put("msg", "共享成功！");

		this.json = jsonObject.toString();
		return "json";
	}

	// 获取访问者的姓名
	public String getVisitorName(Long aid) {
		String name = null;
		Actor actor = this.actorService.load(aid);
		if (actor != null) {
			name = actor.getName();
		}
		return name;
	}

	public boolean haveAuthority(String role, int i) {
		boolean authority = false;
		String number = null;
		number = role.substring(i, i + 1);
		if (number.endsWith("1")) {
			authority = true;
		}
		return authority;
	}
}
