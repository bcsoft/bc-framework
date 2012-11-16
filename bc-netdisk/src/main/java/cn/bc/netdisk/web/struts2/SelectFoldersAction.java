package cn.bc.netdisk.web.struts2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.domain.NetdiskShare;
import cn.bc.netdisk.service.NetdiskFileService;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择视图Action
 * 
 * @author zxr
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectFoldersAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);
	public Long folderId;// 文件夹不能隶属于自己
	private NetdiskFileService netdiskFileService;

	@Autowired
	public void setNetdiskFileService(NetdiskFileService netdiskFileService) {
		this.netdiskFileService = netdiskFileService;
	}

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.netdisk"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("f.status_").add("f.order_", Direction.Asc)
				.add("f.file_date", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.name");
		sql.append(" from bc_netdisk_file f");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("name", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("f.id", "id"));
		columns.add(new TextColumn4MapKey("f.status_", "status",
				getText("netdisk.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("f.name", "name",
				getText("netdisk.name")).setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "f.name" };
	}

	@Override
	protected String getFormActionName() {
		return "selectFolder";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(450).setHeight(450);
	}

	@Override
	protected Condition getGridSpecalCondition() {
		OrCondition orCondition = new OrCondition();
		// AndCondition andCondition = new AndCondition();
		// 状态条件
		Condition statusCondition = null;
		Condition typeCondition = null;
		// Condition userCondition = null;
		Condition authorityCondition = null;
		Condition eliminateCondition = null;
		// Condition tierCondition = null;

		// 状态
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("f.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("f.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		// 文件夹类型
		typeCondition = new EqualsCondition("f.type_", NetdiskFile.TYPE_FOLDER);
		// 当前用户只能查看自己上传的文件
		SystemContext context = (SystemContext) this.getContext();
		// userCondition = new EqualsCondition("f.author_id", context
		// .getUserHistory().getId());

		// 文件夹不能隶属于自己
		if (folderId != null) {
			eliminateCondition = new NotEqualsCondition("f.id", folderId);
		}
		// 父级文件夹不能隶属子级
		// 当前用户有权限查看的文件
		Serializable[] ids = this.netdiskFileService
				.getUserSharFileId2All(context.getUser().getId());
		// 查找当前文件的父级
		// 可以操作的文件夹id
		List<Object> operateId = new ArrayList<Object>();
		if (folderId != null) {
			Serializable[] myselfAndChildId = this.netdiskFileService
					.getMyselfAndChildFileId(folderId);
			// 排除自己与子文件id后来的数组
			List<Object> eliminateId = new ArrayList<Object>();
			if (ids != null) {
				for (Serializable id : ids) {
					boolean isIn = false;
					for (Serializable pid : myselfAndChildId) {
						if (id.equals(pid)) {
							isIn = true;
							break;
						}
					}
					if (!isIn) {
						eliminateId.add(id);
					}
				}
				// 找出可操作的id
				if (!eliminateId.isEmpty()) {
					for (int i = 0; i < eliminateId.size(); i++) {
						// 查看当前文件是否设置访问权限，如果有就根据当前的权限来判断
						NetdiskShare myNetdiskShare = this.netdiskFileService
								.getNetdiskShare(context.getUser().getId(),
										Long.valueOf(eliminateId.get(i)
												.toString()));
						// 如果是自己的建的文件夹也操作
						NetdiskFile netdiskFile = this.netdiskFileService
								.load(Long.valueOf(eliminateId.get(i)
										.toString()));
						if (netdiskFile.getAuthor().getId()
								.equals(context.getUserHistory().getId())) {
							operateId.add(eliminateId.get(i));
						} else if (myNetdiskShare != null) {
							if (haveAuthority(myNetdiskShare.getRole(), 0)) {
								// 如果有权限就添加
								operateId.add(eliminateId.get(i));
							}
						} else {
							// 获取当前的文件和父级文件
							Serializable[] ParentIds = this.netdiskFileService
									.getMyselfAndParentsFileId(Long
											.valueOf(eliminateId.get(i)
													.toString()));
							// 判断当前文件或父级文件夹该用户是否拥有编辑权限
							for (Serializable pid : ParentIds) {
								boolean isOwer = false;
								NetdiskFile nf = this.netdiskFileService
										.load(pid);
								Set<NetdiskShare> netdiskShare = nf
										.getFileVisitors();
								if (!netdiskShare.isEmpty()) {
									Iterator<NetdiskShare> n = netdiskShare
											.iterator();
									while (n.hasNext()) {
										NetdiskShare ns = n.next();
										if (ns.getAid().equals(
												context.getUser().getId())) {
											if (haveAuthority(ns.getRole(), 0)) {
												// 如果有权限就添加
												operateId.add(eliminateId
														.get(i));
												isOwer = true;
												break;
											}
										}
									}
									if (isOwer) {
										break;
									}
								}
							}
						}
					}
				}
			}

			// 组装条件
			String qlStr4File = "";
			if (operateId.size() != 0) {
				for (int i = 0; i < operateId.size(); i++) {
					if (i + 1 != operateId.size()) {
						qlStr4File += "?,";
					} else {
						qlStr4File += "?";
					}
				}
			}
			authorityCondition = orCondition.add(
					(operateId.size() != 0 ? new QlCondition("f.id in ("
							+ qlStr4File + ")", operateId) : null))
					.setAddBracket(true);

		}
		// 可以操作的文件夹不为空
		if (operateId.size() != 0) {
			return ConditionUtils.mix2AndCondition(statusCondition,
					typeCondition, eliminateCondition, authorityCondition);
		} else {
			return null;
		}
	}

	@Override
	protected Page<Map<String, Object>> findPage() {
		// 如果返回为null则不进行查询
		if (this.getGridSpecalCondition() != null) {
			return super.findPage();
		} else {
			return new Page<Map<String, Object>>(1, 1, 0,
					new ArrayList<Map<String, Object>>());
		}
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

		if (folderId != null) {
			json.put("folderId", folderId);
		}

	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/netdiskFile/selectFolder.js";
	}

	/**
	 * 状态值转换列表：使用中|已删除|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("netdiskFile.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DELETED),
				getText("entity.status.deleted"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}

	@Override
	protected String getClickOkMethod() {
		return "bc.folderSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + "/bc";
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

}
