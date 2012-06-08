/**
 * 
 */
package cn.bc.investigate.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.investigate.domain.Questionary;

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

}