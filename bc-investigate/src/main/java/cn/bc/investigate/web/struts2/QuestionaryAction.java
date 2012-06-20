/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.service.QuestionaryService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 调查问卷的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class QuestionaryAction extends FileEntityAction<Long, Questionary> {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private QuestionaryService questionaryService;
	public Set<Actor> ownedUsers;// 已分配的用户

	@Autowired
	public void setQuestionaryService(QuestionaryService questionaryService) {
		this.setCrudService(questionaryService);
		this.questionaryService = questionaryService;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(630)
				.setMinWidth(320);

	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		pageOption.addButton(new ButtonOption(getText("label.save"), null,
				"bc.questionaryForm.save"));
		pageOption.addButton(new ButtonOption(getText("questionary.preview"),
				null, null));
		pageOption.addButton(new ButtonOption(getText("questionary.issue"),
				null, null));

	}

	@Override
	protected void afterCreate(Questionary entity) {
		super.afterCreate(entity);
		this.getE().setIssuer(null);
	}

	@Override
	protected void beforeSave(Questionary entity) {
		super.beforeSave(entity);
		this.getE().setIssuer(null);
	}

	@Override
	protected void afterEdit(Questionary entity) {
		super.afterEdit(entity);

		// 加载已参与的人
		this.ownedUsers = entity.getActors();
	}

	@Override
	protected void afterOpen(Questionary entity) {
		super.afterOpen(entity);

		// 加载已参与的人
		this.ownedUsers = entity.getActors();
	}
}