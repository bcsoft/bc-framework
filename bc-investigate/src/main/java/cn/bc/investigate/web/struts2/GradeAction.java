/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.web.SystemContext;
import cn.bc.investigate.domain.Grade;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.service.GradeService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 评分的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GradeAction extends EntityAction<Long, Grade> {

	private static final long serialVersionUID = 1L;
	// private QuestionaryService questionaryService;
	private GradeService gradeService;
	public Map<String, String> statusesValue;
	public Long qid;// 试卷Id
	public Long rid;// 作答表Id
	public Long aid;// 回答表Id
	public String answer;// 答卷人
	public String answerTime;// 答卷时间
	public String testTitle;// 试卷标题
	public String questionTitle;// 题目
	public String result;// 答案
	public int score;// 分数
	public int amount;// 计分
	public Long gid;// 评分表的Id
	public int status;// 试卷的状态

	@Override
	public boolean isReadonly() {
		// 网上考试管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.question.exam"),
				getText("key.role.bc.admin"));
	}

	public GradeService getGradeService() {
		return gradeService;
	}

	@Autowired
	public void setGradeService(GradeService gradeService) {
		this.setCrudService(gradeService);
		this.gradeService = gradeService;
	}

	// @Autowired
	// public void setQuestionaryService(QuestionaryService questionaryService)
	// {
	// this.questionaryService = questionaryService;
	// }

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		statusesValue = this.getBSStatuses();
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setWidth(445).setMinWidth(320)
				.setHeight(340);
		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);

		} else {
			pageOption.put("readonly", true);
		}
		// 添加按钮
		if (this.status == Questionary.STATUS_ISSUE) {
			pageOption.addButton(new ButtonOption(getText("label.ok"), null,
					"bc.gradeForm.grade"));
		}
		return pageOption;
	}

	// 编辑表单
	public String edit() throws Exception {

		this.formPageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);
		if (this.getId() != null) {
			Grade e = this.getCrudService().load(this.getId());
			amount = e.getScore();
			gid = e.getId();
			this.afterEdit(e);
		}
		return "form";
	}

	public String json;

	// 问答题评分
	public String testGrade() {
		SystemContext context = (SystemContext) this.getContext();
		json = this.gradeService.dograde(qid, aid, rid, amount, context, gid);
		return "json";
	}

	/**
	 * 状态值转换列表：待评分|已评分|全部
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

}