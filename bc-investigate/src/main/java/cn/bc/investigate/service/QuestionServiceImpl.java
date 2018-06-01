/**
 *
 */
package cn.bc.investigate.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.QuestionDao;
import cn.bc.investigate.domain.Question;
import org.springframework.beans.factory.annotation.Autowired;

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