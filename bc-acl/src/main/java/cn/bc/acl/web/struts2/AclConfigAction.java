package cn.bc.acl.web.struts2;

import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessDoc;
import cn.bc.acl.service.AccessActorService;
import cn.bc.acl.service.AccessDocService;
import cn.bc.core.exception.CoreException;
import cn.bc.core.util.JsonUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import com.opensymphony.xwork2.ActionSupport;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 实现 ACL API 接口的 config 方法
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AclConfigAction extends ActionSupport {
	@Autowired
	private AccessActorService accessActorService;
	@Autowired
	private AccessDocService accessDocService;

	/** 文档ID */
	public String docId;
	/** 文档类型 */
	public String docType;
	/** 文档名称 */
	public String docName;
	/**
	 * [可选] ACL的控制点，默认为"01"，仅控制查阅点
	 */
	public String bit = "01";
	/**
	 * [可选] 修改ACL配置需要的额外角色，没有指定时为查看ACL配置
	 * <ul>
	 *		<li>如果为任意其中一个角色即可，则多个角色编码间用符号"|"链接</li>
	 *		<li>如果为必须拥有每一个角色，则多个角色编码间用符号"+"链接</li>
	 * </ul>
	 */
	public String role;

	public PageOption pageOption;// 页面配置
	public List<AccessActor> accessActors;// 访问者集合

	// 判断当前用户是否满足额外的角色要求
	public boolean isReadonly() {
		if(this.role == null || this.role.isEmpty()) {
			return false;
		}

		SystemContext context = SystemContextHolder.get();

		// 处理 | 和 +
		if(this.role.indexOf("|") != -1){// 任意其中一个角色的情况
			return !context.hasAnyOneRole(this.role.replace("|", ","));
		}else{// 必须拥有全部角色的情况
			String[] roles = this.role.split("\\+");
			for(String r : roles){
				if(!context.hasAnyRole(r)){
					return true;
				}
			}
			return false;
		}
	}

	// 判断bit中从右边数起的第n位的值是否为1
	public boolean isAllowableBit(String bit, int n) {
		if(bit == null || bit.isEmpty() || bit.length() < n) {
			return false;
		}
		int i = bit.length() - n;
		String b = bit.substring(i, i + 1);
		return "1".equals(b);
	}

	// 判断是否要控制指定的位
	public boolean isControlBit(int n) {
		return isAllowableBit(this.bit, n);
	}

	// ACL配置界面
	public String config() throws Exception {
		boolean readonly = this.isReadonly();

		// 设置页面参数
		this.pageOption = new PageOption();
		this.pageOption.setModal(true).setWidth(520).setMinWidth(400)
				.setHeight(300).setMinHeight(150);

		// 添加确认按钮
		if (!readonly) {
			this.pageOption.addButton(new ButtonOption(getText("label.ok"), null, "bc.aclConfigDialog.onOk"));
		}

		// 访问的文档
		AccessDoc accessDoc = this.accessDocService.load(this.docId, this.docType);
		if(accessDoc != null) {
			this.accessActors = this.accessActorService.findByPid(accessDoc.getId());
		}else{
			this.accessActors = new ArrayList<AccessActor>();
		}

		return SUCCESS;
	}

	public String details;// 访问配置明细[{id: 123, role: "01"},...]
	public String json;

	// 保存ACL配置界面的修改
	public String saveConfig() throws Exception {
		JSONObject json = new JSONObject();
		try {
			if(this.isReadonly()){
				throw new CoreException("Permission denied");
			}
			Assert.hasText(this.docId, "docId couldn't be empty.");
			Assert.hasText(this.docType, "docType couldn't be empty.");
			Collection<Map<String, Object>> details = JsonUtils.toCollection(this.details);
			boolean changed = this.accessDocService.saveConfig(this.docType, this.docId, this.docName, details);

			json.put("success", true);
			json.put("changed", changed);
			json.put("msg", "访问配置信息保存成功！");
		}catch (Exception e){
			json.put("success", false);
			json.put("msg", e.getMessage());
		}

		this.json = json.toString();
		return "json";
	}
}