/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.service.QuestionaryService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 评分的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Score4QuesAction extends FileEntityAction<Long, Questionary> {

	private static final long serialVersionUID = 1L;
	private QuestionaryService questionaryService;
	public Set<Actor> ownedUsers;// 已分配的用户
	public String topics;// 问卷中题目的json字符串
	public String optionItemsValue;// 问题选项
	public Map<String, String> statusesValue;

	@Override
	public boolean isReadonly() {
		// 网上考试管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.question.exam"),
				getText("key.role.bc.admin"));
	}

	@Autowired
	public void setQuestionaryService(QuestionaryService questionaryService) {
		this.setCrudService(questionaryService);
		this.questionaryService = questionaryService;
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		statusesValue = this.getBSStatuses();
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setWidth(645).setMinWidth(320)
				.setHeight(500);
		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);

		} else {
			pageOption.put("readonly", true);
		}
		// 添加按钮
		buildFormPageButtons(pageOption, editable);

		return pageOption;
	}

	protected PageOption buildPreviewFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setWidth(645).setMinWidth(320)
				.setHeight(550);
		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", false);

		} else {
			pageOption.put("readonly", true);
		}
		// 添加按钮
		// buildPreviewFormPageButtons(pageOption, editable);

		return pageOption;
	}

	/**
	 * 状态值转换列表：待发布|已发布|已归档|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Questionary.STATUS_DRAFT),
				getText("questionary.release.wait"));
		statuses.put(String.valueOf(Questionary.STATUS_ISSUE),
				getText("questionary.release.already"));
		statuses.put(String.valueOf(Questionary.STATUS_END),
				getText("questionary.release.end"));
		statuses.put("", getText("questionary.release.all"));
		return statuses;
	}

	// 提示只能删除未作答过的试卷
	@Override
	protected String getDeleteExceptionMsg(Exception e) {
		//
		if (e instanceof PermissionDeniedException) {
			return "只能删除删除未作答过的试卷！";
		}
		return super.getDeleteExceptionMsg(e);
	}

}