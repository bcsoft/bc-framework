package cn.bc.netdisk.web.struts2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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
import cn.bc.core.util.DateUtils;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
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
	private ActorHistoryService actorHistoryService;
	public String fileInfo;// 文件信息
	public String dialogType;// 新建对话框的类型
	public String title;// 整理对话框的标题
	public String order;// 排序号
	public String pid;// 所属文件夹Id
	public String folder;// 所属文件夹名
	public String name;// 文件名
	public boolean isRelevanceDelete = false;// 是否删除文件夹下的所有文件
	public int editRole;// 共享设置
	public String visitors;// 访问者
	public boolean isEditRole = false;// 是否编辑共享设置(只有拥有都才有编辑)
	public boolean isEditVisitorsAuthority = false;// 编辑访问者权限
	public boolean isClearUp = false;// 文件整理权限
	public boolean isDel = false;// 文件删除权限

	@Autowired
	public void setNetdiskFileService(NetdiskFileService netdiskFileService) {
		this.setCrudService(netdiskFileService);
		this.netdiskFileService = netdiskFileService;
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

	// 公共硬盘管理权限
	public boolean isPublicHardDiskManagement() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：公共管理员
		return context.hasAnyRole(getText("key.role.bc.netdisk.public"),
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
		// 新建个人文件夹
		if (dialogType.equals("xinjiangerenwenjianjia")) {
			title = getText("netdisk.xinjiangerenwenjianjia");
			entity.setFolderType(NetdiskFile.FOLDER_TYPE_PERSONAL);
		} else {
			// 新建公共文件夹
			title = getText("netdisk.xinjiangonggongwenjianjia");
			entity.setFolderType(NetdiskFile.FOLDER_TYPE_PUBLIC);

		}
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setType(NetdiskFile.TYPE_FOLDER);
		entity.setSize(new Long(0));

	}

	public String createDialog() {

		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);
		NetdiskFile e = this.netdiskFileService.load(this.getId());
		this.setE(e);
		// 判断是否拥有删除，整理，共享权限
		isWonerDelAndClearAndShare4Power(e);
		// 页面中跳转
		if (dialogType.equals("zhengliwenjian")) {

			if (e.getType() == NetdiskFile.TYPE_FOLDER) {
				title = getText("folder");
			} else {
				title = getText("file");
			}

			return "zhengliwenjian";
		} else {

			return "gongxiang";
		}

	}

	/**
	 * 判断是否拥有删除，整理，共享权限
	 * 
	 * @param e
	 */
	private void isWonerDelAndClearAndShare4Power(NetdiskFile e) {
		SystemContext context = this.getSystyemContext();
		// 判断是否可以编辑共享设置(拥有者才能编辑)
		if (e.getAuthor().getId().equals(context.getUserHistory().getId())) {
			// 共享界面权限
			isEditRole = true;
			isEditVisitorsAuthority = true;// 拥有都可以访问者
			// 整理界面权限
			isClearUp = true;
			// 文件删除
			isDel = true;
		} else {
			// 非拥有者
			// 如果设置了只有拥有都有有权限编辑访问者则其他用户不能添加或删除应该文件的访问者
			// 如果设置了编辑者可以添加访问者和更改权限则要判断添加的用户是否拥有编辑权限
			if (e.getEditRole() == NetdiskFile.ROLE_REVISABILITY) {
				// 查看当前文件是否设置访问权限，如果有就根据当前的权限来判断
				NetdiskShare myNetdiskShare = this.netdiskFileService
						.getNetdiskShare(context.getUser().getId(),
								this.getId());
				if (myNetdiskShare != null) {
					if (haveAuthority(myNetdiskShare.getRole(), 0)) {
						// 共享界面权限
						isEditVisitorsAuthority = true;
						// 整理界面权限
						isClearUp = true;
						// 文件删除
						isDel = true;
					}
				} else {
					// 获取当前的文件和父级文件
					Serializable[] ids = this.netdiskFileService
							.getMyselfAndParentsFileId(this.getId());
					// 判断当前文件或父级文件夹该用户是否拥有编辑权限
					for (Serializable id : ids) {
						NetdiskFile nf = this.netdiskFileService.load(id);
						Set<NetdiskShare> netdiskShare = nf.getFileVisitors();
						if (!netdiskShare.isEmpty()) {
							Iterator<NetdiskShare> n = netdiskShare.iterator();
							while (n.hasNext()) {
								NetdiskShare ns = n.next();
								if (ns.getAid().equals(
										context.getUser().getId())) {
									if (haveAuthority(ns.getRole(), 0)) {
										// 共享界面权限
										isEditVisitorsAuthority = true;
										// 整理界面权限
										isClearUp = true;
										// 文件删除
										isDel = true;
										break;
									}
								}
							}
							if (isEditVisitorsAuthority) {
								break;
							}
						}
					}
				}
			}
			// 公共文件权限判断
			// 先判断整理我共享的文件是否有权限修改再判断该文件是否为公共文件
			if (!isEditVisitorsAuthority) {
				// 获取当前的文件和父级文件
				Serializable[] ids = this.netdiskFileService
						.getMyselfAndParentsFileId(this.getId());
				// 判断当前文件或父级文件夹该用户是否拥有编辑权限
				for (Serializable id : ids) {
					NetdiskFile nf = this.netdiskFileService.load(id);
					if (nf != null) {
						// 如果该文件为公共文件并且当前用户拥有公共硬盘管理权限
						// 就可以共享或管理该文件
						if (nf.getFolderType() == NetdiskFile.FOLDER_TYPE_PUBLIC
								&& this.isPublicHardDiskManagement()) {
							// 共享界面权限
							isEditVisitorsAuthority = true;
							// 整理界面权限
							isClearUp = true;
							// 文件删除
							isDel = true;
							break;
						}
					}
				}
			}
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
		if (pid.length() == 0 || pid.equals("") || pid.equals("-1")) {
			updateInfo.put("pid", (Integer) null);
		} else if (pid.equals("-2")) {
			// 将文件整理到公共硬盘根目录下
			updateInfo.put("pid", (Integer) null);
			updateInfo.put("folderType", 1);
		} else {
			updateInfo.put("pid", new Long(pid));
		}
		updateInfo.put("orderNo", order);
		updateInfo.put("name", name);
		this.netdiskFileService.update(this.getId(), updateInfo);
		if (pid.length() == 0 || pid.equals("") || pid.equals("-1")) {
			jsonObject.put("pid", -1);
		} else {
			jsonObject.put("pid", pid);
		}
		jsonObject.put("success", true);
		jsonObject.put("msg", "保存成功！");
		this.json = jsonObject.toString();
		return "json";
	}

	// 删除
	public String delete() {
		// 判断是否有删除权限
		if (this.getIds() != null && this.getIds().length() > 0) {
			Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
					.getIds().split(","));
			for (int i = 0; i < ids.length; i++) {
				NetdiskFile e = this.netdiskFileService.load(ids[i]);
				this.setId(ids[i]);
				// 判断是否拥有删除，整理，共享权限
				isWonerDelAndClearAndShare4Power(e);
				// 如果没有删除权限就提示
				if (!isDel) {
					jsonObject.put("success", false);
					jsonObject.put("msg", "你没有删除" + e.getName() + "的权限！");
					break;
				}
			}

		} else {
			NetdiskFile e = this.netdiskFileService.load(this.getId());
			// 判断是否拥有删除，整理，共享权限
			isWonerDelAndClearAndShare4Power(e);
			// 如果没有删除权限就提示
			if (!isDel) {
				jsonObject.put("success", false);
				jsonObject.put("msg", "你没有删除“" + e.getName() + "”的权限！");
			}
		}
		// 正常删除
		if (isDel) {
			try {
				if (this.getIds() != null && this.getIds().length() > 0) {
					Long[] ids = cn.bc.core.util.StringUtils
							.stringArray2LongArray(this.getIds().split(","));
					this.netdiskFileService.delete(ids, isRelevanceDelete);
				} else {
					this.netdiskFileService.delete(this.getId(),
							isRelevanceDelete);
				}
				jsonObject.put("success", true);
				jsonObject.put("msg", "删除成功！");
			} catch (DataIntegrityViolationException e) {
				jsonObject.put("msg", getDeleteExceptionMsg(e));
				jsonObject.put("e", e.getClass().getSimpleName());
				jsonObject.put("success", false);
			}
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
		SystemContext context = this.getSystyemContext();
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
					// 添加者
					if (json.getString("authorId").equals("")) {
						resource.setAuthorId(context.getUserHistory().getId());
					} else {
						resource.setAuthorId(Long.valueOf(json
								.getString("authorId")));
					}
					// 添加时间
					if (("").equals(json.getString("fileDate"))) {
						resource.setFileDate(Calendar.getInstance());
					} else {
						new DateUtils();
						resource.setFileDate(DateUtils.getCalendar(json
								.getString("fileDate")));
					}

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

	/**
	 * 获取访问者的姓名
	 * 
	 * @param aid
	 *            访问者Id
	 * @return
	 */
	public String getVisitorName(Long aid) {
		String name = null;
		Actor actor = this.actorService.load(aid);
		if (actor != null) {
			name = actor.getName();
		}
		return name;
	}

	/**
	 * 判断访问的权限
	 * 
	 * @param role
	 *            访问者拥有的权限 如：‘0101’(wrfd:w-编辑,r-查看,f-评论,d-下载)1表示是0表示否
	 * @param i
	 *            第0位表是编辑权限，第1位表示查看
	 * @return
	 */
	public boolean haveAuthority(String role, int i) {
		boolean authority = false;
		String number = null;
		number = role.substring(i, i + 1);
		if (number.endsWith("1")) {
			authority = true;
		}
		return authority;
	}

	/**
	 * 获取添加者姓名
	 * 
	 * @param authorId
	 *            添加者的Id
	 * @return
	 */
	public String getAuthorName(Long authorId) {
		String name = null;
		ActorHistory actorHistory = this.actorHistoryService.load(authorId);
		if (actorHistory != null) {
			name = actorHistory.getName();
		}
		return name;
	}
}
