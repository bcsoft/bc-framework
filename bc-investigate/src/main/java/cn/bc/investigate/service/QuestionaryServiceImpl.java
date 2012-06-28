/**
 * 
 */
package cn.bc.investigate.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.QuestionaryDao;
import cn.bc.investigate.domain.Questionary;

/**
 * 调查问卷的实现
 * 
 * @author zxr
 */
public class QuestionaryServiceImpl extends DefaultCrudService<Questionary>
		implements QuestionaryService {
	private QuestionaryDao questionaryDao;

	public QuestionaryDao getQuestionaryDao() {
		return questionaryDao;
	}

	@Autowired
	public void setQuestionaryDao(QuestionaryDao questionaryDao) {
		this.questionaryDao = questionaryDao;
		this.setCrudDao(questionaryDao);
	}

}