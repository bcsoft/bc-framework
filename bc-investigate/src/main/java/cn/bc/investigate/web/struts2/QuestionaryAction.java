/**
 * 
 */
package cn.bc.investigate.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.service.QuestionaryService;
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

	@Autowired
	public void setQuestionaryService(QuestionaryService questionaryService) {
		this.setCrudService(questionaryService);
		this.questionaryService = questionaryService;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(530)
				.setMinWidth(320);

	}

}