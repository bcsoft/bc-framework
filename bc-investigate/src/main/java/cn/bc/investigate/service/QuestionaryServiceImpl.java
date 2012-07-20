/**
 * 
 */
package cn.bc.investigate.service;

import java.io.Serializable;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.QuestionaryDao;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.domain.Respond;

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

	@Override
	public void delete(Serializable id) {
		Questionary questionary = this.questionaryDao.load(id);
		Set<Respond> respond = questionary.getResponds();

		if (respond.size() == 0) {
			super.delete(id);
		} else {
			// 抛出不能删除的异常
			throw new PermissionDeniedException();

		}

	}

	@Override
	public void delete(Serializable[] ids) {
		for (Serializable id : ids) {
			this.delete(id);
		}
	}

}