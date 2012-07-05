/**
 * 
 */
package cn.bc.investigate.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.QuestionDao;
import cn.bc.investigate.domain.Question;

/**
 * 调查问卷的实现
 * 
 * @author zxr
 */
public class QuestionServiceImpl extends DefaultCrudService<Question> implements
		QuestionService {
	private QuestionDao questionDao;

	public QuestionDao getQuestionDao() {
		return questionDao;
	}

	@Autowired
	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
		this.setCrudDao(questionDao);
	}

}